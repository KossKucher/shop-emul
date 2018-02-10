package com.shop.emul;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.shop.emul.TimeKeeper.Markup.WHOLESALE;

public class Cashbox {
  
  private static final int WHOLESALE_THRESHOLD = 1;
  
  private static Cashbox cashbox = null;
  
  private int[] sold;
  private int[] bought;
  private double income;
  private double spent;
  private DbManager dbManager;
  
  private Cashbox() {
    dbManager = DbManager.get();
    sold = new int[dbManager.recordsNumber()];
    bought = new int[dbManager.recordsNumber()];
    
  }
  
  public static Cashbox get() {
    if (cashbox == null) {
      cashbox = new Cashbox();
    }
    return cashbox;
  }
  
  public void process(Order order) {
    if (order.getOrderType() == Order.OrderType.BUY) {
    
    }
    processSell(order);
  }
  
  private void processSell(Order order) {
    if (order.getOrderType() != Order.OrderType.SELL) {
      throw new IllegalArgumentException();
    }
    double markup = TimeKeeper.get().markup();
    order.forEach((id, number) ->
                  {
                    double sum = calcPrice(id, number, markup);
                    sold[id] += number;
                    income += sum;
                    System.out.println(dbManager.getRecord(id).getTitle() + " - " + number);
      
                  });
  }
  
  private double calcPrice(int id, int number, double markup) {
    double basePrice = dbManager.getRecord(id).getBasePrice();
    double result = (number > WHOLESALE_THRESHOLD)
                    ? (WHOLESALE_THRESHOLD * (basePrice * markup))
                            + ((number - WHOLESALE_THRESHOLD) * (basePrice * WHOLESALE.getMarkup()))
                    : WHOLESALE_THRESHOLD * (basePrice * markup);
    return roundToCents(result);
  }
  
  private double roundToCents(double value) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
  
}
