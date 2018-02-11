package com.shop.emul;

import com.opencsv.CSVReader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.shop.emul.Cashbox.Markup.WHOLESALE;

public class Cashbox {
  
  private static final int WHOLESALE_THRESHOLD = 1;
  private static final String REPORT_FILENAME = "month_report.txt";
  
  private static Cashbox cashbox = null;
  
  private int receiptCounter;
  private int[] sold;
  private int[] bought;
  private double income;
  private double expense;
  private DbManager dbManager;
  
  private Cashbox() {
    dbManager = DbManager.get();
    sold = new int[dbManager.totalRecords()];
    bought = new int[dbManager.totalRecords()];
  }
  
  public static Cashbox get() {
    if (cashbox == null) {
      cashbox = new Cashbox();
    }
    return cashbox;
  }
  
  public void process(Order order) {
    if (order.getOrderType() == Order.OrderType.BUY) {
      processBuy(order);
    } else {
      processSell(order);
    }
  }
  
  private void processSell(Order order) {
    if (order.size() == 0) {
      return;
    }
    double markup = TimeKeeper.get().getMarkup().getValue();
    order.forEach((id, number) ->
                  {
                    double sum = calcPrice(id, number, markup);
                    sold[id] += number;
                    income += sum;
                    roundToCents(income);
                  });
    System.out.println(makeReceipt(order));
  }
  
  private void processBuy(Order order) {
    order.forEach((id, number) ->
                  {
                    bought[id] += number;
                    expense += dbManager.getRecord(id).getBasePrice() * number;
                    roundToCents(expense);
                  });
  }
  
  private double calcPrice(int id, int number, double markup) {
    double basePrice = dbManager.getRecord(id).getBasePrice();
    double result = (number > WHOLESALE_THRESHOLD)
                    ? (WHOLESALE_THRESHOLD * (basePrice * markup))
                            + ((number - WHOLESALE_THRESHOLD) * (basePrice * WHOLESALE.getValue()))
                    : WHOLESALE_THRESHOLD * (basePrice * markup);
    return roundToCents(result);
  }
  
  private double roundToCents(double value) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
  
  private String makeReceipt(Order order) {
    receiptCounter++;
    TimeKeeper timeKeeper = TimeKeeper.get();
    StringBuilder sb = new StringBuilder();
    sb.append("Receipt #").append(receiptCounter).append(System.lineSeparator());
    double markup = timeKeeper.getMarkup().getValue();
    order.forEach((id, number) ->
                  {
                    double sum = calcPrice(id, number, markup);
                    sb.append(String.format("\t%s x %d = %.2f (Markups: %s",
                                            dbManager.getRecord(id).getTitle(),
                                            number, sum, timeKeeper.getMarkup().toString()));
                    if (number > WHOLESALE_THRESHOLD) {
                      sb.append(", ").append(WHOLESALE.toString());
                    }
                    sb.append(")").append(System.lineSeparator());
                  });
    sb.append("****************************").append(System.lineSeparator());
    return sb.toString();
  }
  
  private String makeReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("Month report -------------------------------").append(System.lineSeparator());
    sb.append("Sold:").append(System.lineSeparator());
    for (int i = 0, j = sold.length; i < j; i++) {
      sb.append("\t");
      sb.append(String.format("%s - %d pcs", dbManager.getRecord(i).getTitle(), sold[i]));
      sb.append(System.lineSeparator());
    }
    sb.append(System.lineSeparator());
    sb.append("Bought:").append(System.lineSeparator());
    for (int i = 0, j = bought.length; i < j; i++) {
      sb.append("\t");
      sb.append(String.format("%s - %d pcs", dbManager.getRecord(i).getTitle(), bought[i]));
      sb.append(System.lineSeparator());
    }
    sb.append(System.lineSeparator());
    sb.append(String.format("Total income:\t%.2f", income)).append(System.lineSeparator());
    sb.append(String.format("Total expense:\t%.2f", expense)).append(System.lineSeparator());
    sb.append(String.format("Total profit:\t%.2f", income - expense)).append(System.lineSeparator());
    return sb.toString();
  }
  
  public void writeReport() {
    String monthReport = makeReport();
    File report = new File(System.getProperty("user.dir"), REPORT_FILENAME);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(report))) {
      writer.write(monthReport);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Represents all types of markup rates.
   *
   * @author KossKucher
   * @version 1.0
   */
  public enum Markup {
    NORMAL_DAY(1.1d, "10%"),
    WEEKEND_DAY(1.15d, "15%"),
    EVENING(1.08d, "8%"),
    WHOLESALE(1.07d, "7%");
    
    private double value;
    private String printable;
    
    Markup(double value, String printable) {
      this.value = value;
      this.printable = printable;
    }
    
    public double getValue() {
      return value;
    }
    
    @Override
    public String toString() {
      return printable;
    }
  }
}
