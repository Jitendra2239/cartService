package com.jitendra.cartservice.visitor;

import com.jitendra.cartservice.model.CartItem;

public class TaxVisitor implements Visitor {
    @Override
    public void visit(CartItem item) {
        double tax = item.getPrice() * 0.18;
        item.setTotalPrice(item.getTotalPrice() + tax);
    }
}