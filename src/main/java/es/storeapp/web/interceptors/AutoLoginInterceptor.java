package es.storeapp.web.interceptors;

import es.storeapp.business.entities.User;
import es.storeapp.business.services.UserService;
import es.storeapp.common.Constants;
import es.storeapp.web.cookies.UserInfo;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutoLoginInterceptor extends HandlerInterceptorAdapter {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AutoLoginInterceptor.class);

    public AutoLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(true);
        if (session.getAttribute(Constants.USER_SESSION) != null || request.getCookies() == null) {
            return true;
        }
        for (Cookie c : request.getCookies()) {
            if (Constants.PERSISTENT_USER_COOKIE.equals(c.getName())) {
                String cookieValue = c.getValue();
                if (cookieValue == null) {
                    // Si la cookie está vacía, lanzamos excepción
                    throw new IllegalArgumentException("Empty user-info cookie");
                }
                try {
                    // Decodificamos el valor de la Cookie
                    byte[] userInfoXML = Base64.getDecoder().decode(cookieValue);
                    // Para solventar la vulnerabilidad de deserialización insegura, primero parseamos
                    // el XML con un parser DOM y comprobamos que la clase que viene es un UserInfo
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    // Habilitamos procesado seguro para mitigar XXE
                    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    // Parseamos el XML a un documento y normalizamos el documento
                    Document doc = db.parse(new ByteArrayInputStream(userInfoXML));
                    doc.getDocumentElement().normalize();
                    // Comprobamos que el contenido del XML es realmente un objeto UserInfo
                    if (!(doc.getDocumentElement().getNodeName().equals("java"))){
                        // El nodo raíz debe ser un nodo "java". Si no lo es, ignoramos la cookie
                        throw new IllegalArgumentException("Invalid user-info cookie");
                    }
                    NodeList objectNodes = doc.getElementsByTagName("*");
                    if (objectNodes.getLength() != 1){
                        // Si no hay exactamente un solo objeto, la cookie es inválida
                        throw new IllegalArgumentException("Invalid user-info cookie");
                    }
                    Node objectNode = objectNodes.item(0);
                    if (!(objectNode.getNodeName().equals("object"))){
                        // Si no es un elemento de tipo "object", lanzamos excepcion
                        throw new IllegalArgumentException("Invalid user-info cookie");
                    }
                    if (!(((Element) objectNode).getAttribute("class").equals("es.storeapp.web.cookies.UserInfo"))) {
                        // Si la clase no es UserInfo, lanzamos excepcion
                        throw new IllegalArgumentException("Invalid user-info cookie");

                    }
                    // Finalmente, si llegamos aquí es que la cookie es un archivo XML que contiene un documento java
                    // con un único elemento "object" de la clase "es.storeapp.web.cookies.UserInfo"
                    // Por lo tanto ahora podemos pasarle el documento al XMLDecoder para que instancie el objeto.
                    XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(userInfoXML));
                    UserInfo userInfo = (UserInfo) xmlDecoder.readObject();
                    User user = userService.findByEmail(userInfo.getEmail());
                    if (user != null && user.getPassword().equals(userInfo.getPassword())) {
                        session.setAttribute(Constants.USER_SESSION, user);
                    }
                } catch (IllegalArgumentException | SAXException | ClassCastException e){
                    // IllegalArgumentException -> Base64 inválido
                    // SAXException -> XML Inválido
                    // ClassCastException -> XML Válido pero no tiene ningún Elemento
                    // Si no se puede parsear correctamente, ignoramos la cookie y seguimos adelante
                    logger.warn("Detected invalid user-info cookie");
                    continue;
                }
            }
        }
        return true;
    }
}
