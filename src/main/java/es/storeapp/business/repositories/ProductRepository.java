package es.storeapp.business.repositories;

import es.storeapp.business.entities.Product;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends AbstractRepository<Product> {

    //OLD Code - Vulnerable to SQL Injection
    //private static final String FIND_BY_CATEGORY_QUERY "SELECT p FROM Product p WHERE p.category.name = ''{0}'' ORDER BY p.{1}";
    
    public List<Product> findByCategory(String category, String orderColumn) {
        //OLD Code - Vulnerable to SQL Injection
        //query = entityManager.createQuery(MessageFormat.format(FIND_BY_CATEGORY_QUERY, category, orderColumn));
        /////////////
        //NEW Code
        Query query = entityManager.createNamedQuery("Product.FindByCategory", Product.class)
                .setParameter("categoryName", category)
                .setParameter("order", orderColumn);
        return query.getResultList();
    }
    
}
