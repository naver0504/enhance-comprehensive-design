package com.example.query.adapter.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CustomPageable {

    public CustomPageable(Order order, OrderType orderType) {
        this(Order.DESC, orderType, 0);
    }
    public CustomPageable(OrderType orderType, int page) {
        this(Order.DESC, orderType, page);
    }

    public static final int DEFAULT_SIZE = 12;

    private Order order = Order.DESC;
    private OrderType orderType = OrderType.DEAL_DATE;
    private int page = 0;

    public int getOffset() {
        return page * DEFAULT_SIZE;
    }

    public int getLimit() {
        return DEFAULT_SIZE;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, DEFAULT_SIZE);
    }

    public OrderSpecifier<? extends Comparable> orderBy() {
        ComparableExpressionBase<? extends Comparable> expressionBase = orderType.getComparableExpressionBase();
        return order == Order.ASC ? expressionBase.asc() : expressionBase.desc();
    }
}
