package com.shop.emul;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper to a Map, represents an order entity.
 */
public class Order {
  
  private final OrderType orderType;
  
  private Map<Integer, Integer> order;
  
  /**
   * Constructor
   */
  public Order(OrderType type) {
    orderType = type;
    order = new HashMap<>();
  }
  
  /**
   * Puts single product to the order. Increments number if this product is already added.
   *
   * @param id     {@code int} product id
   * @param number {@code int} product quantity
   */
  public void add(int id, int number) {
    if (order.containsKey(id)) {
      order.put(id, order.get(id) + number);
    } else {
      order.put(id, number);
    }
  }
  
  /**
   * Gets copy of the base map.
   *
   * @return new {@link HashMap} which contains values from the base map.
   */
  public Map<Integer, Integer> getContents() {
    return new HashMap<>(order);
  }
  
  /**
   * Generated getter.
   */
  public OrderType getOrderType() {
    return orderType;
  }
  
  /**
   * Order type constants.
   */
  public enum OrderType {
    SELL,
    BUY
  }
}
