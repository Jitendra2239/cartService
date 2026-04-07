package com.jitendra.cartservice.visitor;

import com.jitendra.cartservice.model.CartItem;

public interface Visitor {
    void visit(CartItem item);
}