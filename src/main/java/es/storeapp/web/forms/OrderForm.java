package es.storeapp.web.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OrderForm {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @NotNull
    private int price;
    @NotNull
    @Size(min = 1, max = 100)
    private String address;
    private Boolean payNow;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getPayNow() {
        return payNow;
    }

    public void setPayNow(Boolean payNow) {
        this.payNow = payNow;
    }
        
}
