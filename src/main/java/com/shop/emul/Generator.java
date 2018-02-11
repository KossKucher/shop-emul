package com.shop.emul;

import java.util.Random;

public abstract class Generator {
  
  private static final int BUYERS_RANDOM_BOUND = 10;
  
  private static Random random;
  
  static {
    random = new Random();
  }
  
  public static Order genOrder() {
    Order order = new Order(Order.OrderType.SELL);
    DbManager storage = DbManager.get();
    final int storageBound = storage.totalRecords();
    for (int i = 0, j = random.nextInt(BUYERS_RANDOM_BOUND) + 1, id, value; i < j; i++) {
      id = random.nextInt(storageBound);
      value = (order.get(id) != null) ? order.get(id) + 1 : 1;
      if (storage.isEnough(id, value)) {
        order.put(id, 1);
      }
    }
    return order;
  }
}
