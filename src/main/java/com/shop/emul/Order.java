package com.shop.emul;

import java.util.HashMap;

public class Order extends HashMap<Integer, Integer> {
  
  private OrderType orderType;
  
  public Order(OrderType type) {
    orderType = type;
  }
  
  @Override
  public Integer put(Integer key, Integer value) {
    if (containsKey(key)) {
      return super.put(key, get(key) + value);
    }
    return super.put(key, value);
  }
  
  public OrderType getOrderType() {
    return orderType;
  }
  
  public enum OrderType {
    SELL,
    BUY
  }
  
}
