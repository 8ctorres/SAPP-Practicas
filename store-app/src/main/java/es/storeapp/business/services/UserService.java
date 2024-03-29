package es.storeapp.business.services;

import es.storeapp.business.entities.User;
import es.storeapp.business.exceptions.AuthenticationException;
import es.storeapp.business.exceptions.DuplicatedResourceException;
import es.storeapp.business.exceptions.InstanceNotFoundException;
import es.storeapp.business.exceptions.ServiceException;
import es.storeapp.business.repositories.UserRepository;
import es.storeapp.business.utils.ExceptionGenerationUtils;
import es.storeapp.common.ConfigurationParameters;
import es.storeapp.common.Constants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.HtmlEmail;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    //Removed because it is insecure
    //private static final String SALT = "$2a$10$MN0gK0ldpCgN9jx6r0VYQO";

    @Autowired
    ConfigurationParameters configurationParameters;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    ExceptionGenerationUtils exceptionGenerationUtils;

    private File resourcesDir;

    @PostConstruct
    public void init() {
        resourcesDir = new File(configurationParameters.getResources());
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User login(String email, String clearPassword) throws AuthenticationException {
        /* Buscamos el usuario por email y luego comprobamos con bcrypt
        *  que el password coincida con el hash que hay guardado */
        User user = userRepository.findByEmail(email);
        /* En lugar de primero comprobar si existe y luego buscarlo, es más eficiente buscarlo
        *  directamente, y si sale "null" es que no existe. Así es una sola consulta en vez de dos.
        *  Además, independientemente de si lo que falla es el username o el password, devolvemos
        *  exactamente el mismo mensaje de error, minimizando la información que damos a un atacante. */
        if (user == null){
            logger.info(MessageFormat.format("User {0} does not exist", email));
            throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_LOGIN_MESSAGE);
        }
        if (!BCrypt.checkpw(clearPassword, user.getPassword())) {
            logger.warn(MessageFormat.format("User {0} trying to log in with wrong password!", email));
            throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_LOGIN_MESSAGE);
        }
        return user;
    }

    @Transactional()
    public void sendResetPasswordEmail(String email, String url, Locale locale)
            throws AuthenticationException, ServiceException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn(MessageFormat.format("Tried to recover password for nonexistent user {0}", email));
            return;
            // No mostramos información al usuario de si ese correo existe o no. Simplemente si no existe, no hacemos nada
            //throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_USER_MESSAGE, email);
        }
        String token = UUID.randomUUID().toString();

        try {

            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName(configurationParameters.getMailHost());
            htmlEmail.setSmtpPort(configurationParameters.getMailPort());
            htmlEmail.setSslSmtpPort(Integer.toString(configurationParameters.getMailPort()));
            htmlEmail.setAuthentication(configurationParameters.getMailUserName(),
                    configurationParameters.getMailPassword());
            htmlEmail.setSSLOnConnect(configurationParameters.getMailSslEnable() != null
                    && configurationParameters.getMailSslEnable());
            if (configurationParameters.getMailStartTlsEnable()) {
                htmlEmail.setStartTLSEnabled(true);
                htmlEmail.setStartTLSRequired(true);
            }
            htmlEmail.addTo(email, user.getName());
            htmlEmail.setFrom(configurationParameters.getMailFrom());
            htmlEmail.setSubject(messageSource.getMessage(Constants.MAIL_SUBJECT_MESSAGE,
                    new Object[]{user.getName()}, locale));

            String link = url + Constants.PARAMS
                    + Constants.TOKEN_PARAM + Constants.PARAM_VALUE + token + Constants.NEW_PARAM_VALUE
                    + Constants.EMAIL_PARAM + Constants.PARAM_VALUE + email;

            htmlEmail.setHtmlMsg(messageSource.getMessage(Constants.MAIL_TEMPLATE_MESSAGE,
                    new Object[]{user.getName(), link}, locale));

            htmlEmail.setTextMsg(messageSource.getMessage(Constants.MAIL_HTML_NOT_SUPPORTED_MESSAGE,
                    new Object[0], locale));

            htmlEmail.send();
            logger.info(MessageFormat.format("Sent password recovery email for user {0}", email));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ServiceException(ex.getMessage());
        }

        user.setResetPasswordToken(token);
        userRepository.update(user);
    }

    @Transactional
    public User create(String name, String email, String password, String address,
            String image, byte[] imageContents) throws DuplicatedResourceException {
        if (userRepository.findByEmail(email) != null) {
            throw exceptionGenerationUtils.toDuplicatedResourceException(Constants.EMAIL_FIELD, email,
                    Constants.DUPLICATED_INSTANCE_MESSAGE);
        }
        // Cambiamos para que el SALT se genere aleatoriamente
        User user = userRepository.create(new User(name, email, BCrypt.hashpw(password, BCrypt.gensalt()), address, image));
        saveProfileImage(user.getUserId(), image, imageContents);
        return user;
    }

    @Transactional
    public User update(Long id, String name, String email, String address, String image, byte[] imageContents)
            throws DuplicatedResourceException, InstanceNotFoundException, ServiceException {
        User user = userRepository.findById(id);
        User emailUser = userRepository.findByEmail(email);
        if (emailUser != null && !Objects.equals(emailUser.getUserId(), user.getUserId())) {
            throw exceptionGenerationUtils.toDuplicatedResourceException(Constants.EMAIL_FIELD, email,
                    Constants.DUPLICATED_INSTANCE_MESSAGE);
        }
        user.setName(name);
        user.setEmail(email);
        user.setAddress(address);
        if (image != null && image.trim().length() > 0 && imageContents != null) {
            try {
                deleteProfileImage(id, user.getImage());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            saveProfileImage(id, image, imageContents);
            user.setImage(image);
        }
        return userRepository.update(user);
    }

    @Transactional
    public User changePassword(Long id, String oldPassword, String password)
            throws InstanceNotFoundException, AuthenticationException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw exceptionGenerationUtils.toAuthenticationException(
                    Constants.AUTH_INVALID_USER_MESSAGE, id.toString());
        }
        // Comprobamos con bcrypt que la contraseña sea correcta
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_PASSWORD_MESSAGE,
                    id.toString());
        }
        // Cambiamos para que el SALT se genere aleatoriamente
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        return userRepository.update(user);
    }

    @Transactional
    public User changePassword(String email, String password, String token) throws AuthenticationException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_USER_MESSAGE, email);
        }
        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(token)) {
            throw exceptionGenerationUtils.toAuthenticationException(Constants.AUTH_INVALID_TOKEN_MESSAGE, email);
        }
        // Cambiamos para que el SALT se genere aleatoriamente
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setResetPasswordToken(null);
        return userRepository.update(user);
    }

    @Transactional
    public User removeImage(Long id) throws InstanceNotFoundException, ServiceException {
        User user = userRepository.findById(id);
        try {
            deleteProfileImage(id, user.getImage());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ServiceException(ex.getMessage());
        }
        user.setImage(null);
        return userRepository.update(user);
    }

    @Transactional
    public byte[] getImage(Long id) throws InstanceNotFoundException {
        User user = userRepository.findById(id);
        try {
            return getProfileImage(id, user.getImage());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    private void saveProfileImage(Long id, String image, byte[] imageContents) {
        if (image != null && image.trim().length() > 0 && imageContents != null) {
            File userDir = new File(resourcesDir, id.toString());
            userDir.mkdirs();
            File profilePicture = new File(userDir, image);
            try (FileOutputStream outputStream = new FileOutputStream(profilePicture);) {
                IOUtils.copy(new ByteArrayInputStream(imageContents), outputStream);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void deleteProfileImage(Long id, String image) throws IOException {
        if (image != null && image.trim().length() > 0) {
            File userDir = new File(resourcesDir, id.toString());
            File profilePicture = new File(userDir, image);
            Files.delete(profilePicture.toPath());
        }
    }

    private byte[] getProfileImage(Long id, String image) throws IOException {
        if (image != null && image.trim().length() > 0) {
            File userDir = new File(resourcesDir, id.toString());
            File profilePicture = new File(userDir, image);
            try (FileInputStream input = new FileInputStream(profilePicture)) {
                return IOUtils.toByteArray(input);
            }
        }
        return null;
    }

}
