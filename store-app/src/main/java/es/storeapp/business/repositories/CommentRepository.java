package es.storeapp.business.repositories;

import es.storeapp.business.entities.Comment;
import java.text.MessageFormat;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository extends AbstractRepository<Comment>{


    //OLD Code - Vulnerable to SQL Injection
    /*
    private static final String COUNT_BY_USER_AND_PRODUCT_QUERY = 
        "SELECT COUNT(*) FROM Comment c WHERE c.user.id = {0} and c.product.id = {1}";
    
    private static final String FIND_BY_USER_AND_PRODUCT_QUERY = 
        "SELECT c FROM Comment c WHERE c.user.id = {0} and c.product.id = {1}";

     */
    public Integer countByUserAndProduct(Long userId, Long productId) {
        /*
        OLD Code - Vulnerable to SQL Injection
        query = entityManager.createQuery(MessageFormat
            .format(COUNT_BY_USER_AND_PRODUCT_QUERY, userId, productId));
         */
        Query query = entityManager.createNamedQuery("Comment.CountByUserAndProduct", Long.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId);
        return (Integer) query.getSingleResult();
    }
    
    public Comment findByUserAndProduct(Long userId, Long productId) {
        /*
        OLD Code - Vulnerable to SQL Injection
        query = entityManager.createQuery(MessageFormat
            .format(FIND_BY_USER_AND_PRODUCT_QUERY, userId, productId));
         */
        Query query = entityManager.createNamedQuery("Comment.FindByUserAndProduct", Comment.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId);
        return (Comment) query.getSingleResult();
    }
    
}
