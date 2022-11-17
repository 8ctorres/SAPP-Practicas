package es.storeapp.business.repositories;

import es.storeapp.business.entities.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;

@Repository
public class UserRepository extends AbstractRepository<User> {

    //OLD Code - Vulnerable to SQL Injection
    //private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT u FROM User u WHERE u.email = ''{0}''";
    //private static final String COUNT_USER_BY_EMAIL_QUERY = "SELECT COUNT(*) FROM User u WHERE u.email = ''{0}''";
    //private static final String LOGIN_QUERY = "SELECT u FROM User u WHERE u.email = ''{0}'' AND u.password = ''{1}''";

    public User findByEmail(String email) {
        try {
            //OLD Code - Vulnerable to SQL Injection
            //query = entityManager.createQuery(MessageFormat.format(FIND_USER_BY_EMAIL_QUERY, email));
            /////////////
            //NEW Code
            Query query = entityManager.createNamedQuery("User.FindByEmail", User.class)
                    .setParameter("email", email);
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public boolean existsUser(String email) {
        //OLD Code - Vulnerable to SQL Injection
        //query = entityManager.createQuery(MessageFormat.format(COUNT_USER_BY_EMAIL_QUERY, email));
        /////////////
        //NEW Code
        Query query = entityManager.createNamedQuery("User.CountByEmail", Long.class)
                .setParameter("email", email);
        return ((Long) query.getSingleResult() > 0);
    }

}
