package com.jitendra.cartservice.model;

import com.jitendra.cartservice.visitor.Item;
import com.jitendra.cartservice.visitor.Visitor;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String productId;

    private String productName;

    private Integer quantity;

    private Double price;

    private Double totalPrice;

    public CartItem() {
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }


}