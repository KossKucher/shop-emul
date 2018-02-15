package com.shop.emul;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.shop.emul.Cashbox.Markup.WHOLESALE;
import static com.shop.emul.Config.REPORT_FILENAME;
import static com.shop.emul.Config.WHOLESALE_THRESHOLD;

/**
 * Singleton, carries on finance operations, generates outputs.
 *
 * @author KossKucher
 * @version 1.0
 */
public class Cashbox implements OrderProcessor {
  
  private static Cashbox cashbox = null;
  
  private final int wholesaleThreshold;
  private final String reportFilename;
  
  private int receiptCounter;
  private int[] sold;
  private int[] bought;
  private double income;
  private double expense;
  private DbManager dbManager;
  
  /**
   * Default private constructor.
   */
  private Cashbox() {
    dbManager = DbManager.get();
    sold = new int[dbManager.totalRecords()];
    bought = new int[dbManager.totalRecords()];
    wholesaleThreshold = Integer.parseInt(System.getProperty(WHOLESALE_THRESHOLD.name(),
                                                             WHOLESALE_THRESHOLD.getDefault()));
    reportFilename = System.getProperty(REPORT_FILENAME.name(), REPORT_FILENAME.getDefault());
  }
  
  /**
   * Singleton getter.
   *
   * @return current instance of this class if exists, otherwise initializes a new instance
   */
  public static Cashbox get() {
    if (cashbox == null) {
      cashbox = new Cashbox();
    }
    return cashbox;
  }
  
  /**
   * Implementation of method defined in {@link OrderProcessor}.
   * Wrapper for order processing methods.
   *
   * @param order {@link Order} to be processed
   */
  @Override
  public void process(Order order) {
    if (order.getContents().size() == 0) {
      return;
    }
    if (order.getOrderType() == Order.OrderType.BUY) {
      processBuy(order);
    } else {
      processSell(order);
    }
  }
  
  /**
   * Makes calculations and updates financial metrics and quantity. Increases values of income.
   *
   * @param order {@link Order} to process its values
   */
  private void processSell(Order order) {
    if (order.getContents().size() == 0) {
      return;
    }
    double markup = TimeKeeper.get().getMarkup().getValue();
    order.getContents().forEach((id, number) ->
                                {
                                  double sum = calcPrice(id, number, markup);
                                  sold[id] += number;
                                  income += sum;
                                  roundToCents(income);
                                });
    System.out.println(makeReceipt(order));
  }
  
  /**
   * Makes calculations and updates financial metrics and quantities. Increases value of expense.
   *
   * @param order {@link Order} to process its values
   */
  private void processBuy(Order order) {
    order.getContents().forEach((id, number) ->
                                {
                                  bought[id] += number;
                                  expense += dbManager.getRecord(id).getBasePrice() * number;
                                  roundToCents(expense);
                                });
  }
  
  /**
   * Calculates product price based on the base price and markups.
   *
   * @param id     {@code int} id of the product
   * @param number {@code int} quantity of the product
   * @param markup {@code double} time based markup
   * @return {@code double} rounded to cents price value
   */
  private double calcPrice(int id, int number, double markup) {
    double basePrice = dbManager.getRecord(id).getBasePrice();
    double result = (number > wholesaleThreshold)
                    ? (wholesaleThreshold * (basePrice * markup))
                            + ((number - wholesaleThreshold) * (basePrice * WHOLESALE.getValue()))
                    : wholesaleThreshold * (basePrice * markup);
    return roundToCents(result);
  }
  
  /**
   * Rounds floating point values to 2 decimal places to minimize calc errors.
   *
   * @param value {@code double} value to be rounded
   * @return {@code double} rounded value
   */
  private static double roundToCents(double value) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
  
  /**
   * Creates a printable receipt of the order.
   *
   * @param order {@link Order} order to be processed
   * @return {@link String} formatted receipt based on order values
   */
  private String makeReceipt(Order order) {
    receiptCounter++;
    TimeKeeper timeKeeper = TimeKeeper.get();
    StringBuilder sb = new StringBuilder();
    sb.append("Receipt #").append(receiptCounter).append(System.lineSeparator());
    double markup = timeKeeper.getMarkup().getValue();
    order.getContents()
         .forEach((id, number) ->
                  {
                    double sum = calcPrice(id, number, markup);
                    sb.append(String.format("\t%s x %d = %.2f (Markups: %s",
                                            dbManager.getRecord(id).getTitle(),
                                            number, sum, timeKeeper.getMarkup().toString()));
                    if (number > wholesaleThreshold) {
                      sb.append(", ").append(WHOLESALE.toString());
                    }
                    sb.append(")").append(System.lineSeparator());
                  });
    sb.append("****************************").append(System.lineSeparator());
    return sb.toString();
  }
  
  /**
   * Creates a printable month report.
   *
   * @return {@link String} formatted month report
   */
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
  
  /**
   * Writes month report to the file inside "user.dir" directory.
   */
  private void writeReport() {
    String monthReport = makeReport();
    File report = new File(System.getProperty("user.dir"), reportFilename);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(report))) {
      writer.write(monthReport);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Implements method defined in {@link OrderProcessor}.
   * Wrapper for data output methods.
   */
  @Override
  public void extractData() {
    writeReport();
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
    
    /**
     * Default constructor.
     */
    Markup(double value, String printable) {
      this.value = value;
      this.printable = printable;
    }
    
    public double getValue() {
      return value;
    }
    
    /**
     * Overrides standard implementation to return printable markups.
     *
     * @return {@link String} markup in user friendly print format
     */
    @Override
    public String toString() {
      return printable;
    }
  }
}
