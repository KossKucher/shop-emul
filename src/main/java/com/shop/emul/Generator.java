package com.shop.emul;

import java.util.Random;

/**
 * Carries on generation of orders and buyers number.
 *
 * @author KossKucher
 * @version 1.0
 */
public abstract class Generator {
  
  private static final int BUYERS_RANDOM_BOUND = Integer
          .parseInt(System.getProperty(Config.BUYERS_RANDOM_BOUND.name(),
                                       Config.BUYERS_RANDOM_BOUND.getDefault()));
  
  private static Random random;
  
  static {
    random = new Random();
  }
  
  /**
   * Generates random number of buyers.
   *
   * @return {@code int} from 1 to 10
   */
  public static int randBuyers() {
    return random.nextInt(BUYERS_RANDOM_BOUND) + 1;
  }
  
  /**
   * Generates a single person order.
   * Order can contain from 0 to 10 product items.
   *
   * @return {@link Order} filled with products
   */
  public static Order genOrder() {
    Order order = new Order(Order.OrderType.SELL);
    DbManager storage = DbManager.get();
    final int storageBound = storage.totalRecords();
    for (int i = 0, j = random.nextInt(BUYERS_RANDOM_BOUND + 1), id, value; i < j; i++) {
      id = random.nextInt(storageBound);
      value = (order.getContents().get(id) != null) ? order.getContents().get(id) + 1 : 1;
      if (storage.isEnough(id, value)) {
        order.add(id, 1);
      }
    }
    return order;
  }
}
