package es.storeapp.web.forms;

import javax.validation.constraints.NotNull;

public class CommentForm {

    @NotNull
    private Long productId;
    private String text;
    private Integer rating;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    
}
