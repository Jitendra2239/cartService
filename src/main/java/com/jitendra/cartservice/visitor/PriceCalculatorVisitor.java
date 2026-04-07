package com.jitendra.cartservice.visitor;

import com.jitendra.cartservice.model.CartItem;

public class PriceCalculatorVisitor implements Visitor {

    private  double total = 0;



    public double getTotal() {
        return total;
    }

    @Override
    public void visit(CartItem item) {
        total+=item.getPrice();
    }
}