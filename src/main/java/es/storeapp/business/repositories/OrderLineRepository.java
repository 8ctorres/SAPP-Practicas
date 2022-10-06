package es.storeapp.business.repositories;

import es.storeapp.business.entities.*;
import java.text.MessageFormat;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrderLineRepository extends AbstractRepository<OrderLine>{

    /*
    OLD Come - Vulnerable to SQL Injection
    private static final String FIND_BY_USER_AND_PRODUCT_QUERY =
            "SELECT COUNT(*) FROM OrderLine o WHERE " +
            "o.order.state = es.storeapp.business.entities.OrderState.COMPLETED " + 
            "AND o.order.user.id = {0} AND o.product.id = {1}";
    */
    public boolean findIfUserBuyProduct(Long userId, Long productId) {
        //OLD Code - Vulnerable to SQL Injection
        //query = entityManager.createQuery(MessageFormat.format(FIND_BY_USER_AND_PRODUCT_QUERY, userId, productId));
        /////////////
        //NEW Code
        Query query = entityManager.createNamedQuery("OrderLine.FindByUserAndProduct", OrderLine.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId);
        return ((Long) query.getSingleResult()) > 0;
    }
    
}
