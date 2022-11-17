package es.storeapp.business.repositories;

import es.storeapp.business.entities.Order;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository extends AbstractRepository<Order> {
    //OLD Code - Vulnerable to SQL Injection
    //private static final String FIND_BY_USER_QUERY = "SELECT o FROM Order o WHERE o.user.id = {0} ORDER BY o.timestamp DESC";
        
    public List<Order> findByUserId(Long userId) {
        //OLD Code - Vulnerable to SQL Injection
        //query = entityManager.createQuery(MessageFormat.format(FIND_BY_USER_QUERY, userId));
        /////////////
        //NEW Code
        Query query = entityManager.createNamedQuery("Order.FindByUser", Order.class)
                .setParameter("userId", userId);
        return query.getResultList();
    }
   
}
