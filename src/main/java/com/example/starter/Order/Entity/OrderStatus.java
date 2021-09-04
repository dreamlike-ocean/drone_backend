package com.example.starter.Order.Entity;

public class OrderStatus {
    public static final Integer waiting = 1;
    public static final Integer accepted = 2;
    public static final Integer delivery = 3;
    public static final Integer end = 4;
    public static final Integer cancel = 5;

    public static final Integer max = 6;
    public static final Integer min = 0;

    public static boolean cancelAllowed(Integer status){
        return status.equals(waiting);
    }

    public static boolean needRemove(Integer statue){
        return statue.equals(end) || statue.equals(cancel);
    }
}
