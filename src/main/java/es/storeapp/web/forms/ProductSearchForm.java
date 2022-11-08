package es.storeapp.web.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductSearchForm {

    @NotNull
    @Size(min = 1, max = 60)
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
}
