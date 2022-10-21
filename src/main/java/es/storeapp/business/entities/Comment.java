package es.storeapp.business.entities;

import es.storeapp.common.Constants;
import java.io.Serializable;
import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = "Comment.CountByUserAndProduct",
                query = "SELECT COUNT(c.commentId) FROM Comment c WHERE c.user.userId = :userId AND c.product.productId = :productId"),
        @NamedQuery(name = "Comment.FindByUserAndProduct",
                query = "SELECT c FROM Comment c WHERE c.user.userId = :userId AND c.product.productId = :productId")
})
@Entity(name = Constants.COMMENT_ENTITY)
@Table(name = Constants.COMMENTS_TABLE)
public class Comment implements Serializable{

    private static final long serialVersionUID = -8821440815912953976L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    
    @Column(name = "text", nullable = false)
    private String text;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format("Comment{commentId=%s, text=%s, rating=%s, timestamp=%s, user=%s, product=%s}", 
            commentId, text, rating, timestamp, user, product);
    }
    
}
