package com.jitendra.cartservice.visitor;

import com.jitendra.cartservice.model.CartItem;

public class DiscountVisitor implements Visitor {
    @Override
    public void visit(CartItem item) {
        double discount = item.getPrice() * 0.1;
        item.setTotalPrice(item.getPrice() - discount);
    }
}