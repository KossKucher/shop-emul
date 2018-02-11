package com.shop.emul;

import static com.shop.emul.DbRecord.Columns.*;

/**
 * Represents db record entity.
 *
 * @author KossKucher
 * @version 1.0
 */
public class DbRecord {
  
  private int id;
  private int number;
  private double basePrice;
  private String title;
  private String[] initBaseRecord;
  
  /**
   * Initializer constructor.
   *
   * @param id          {@code int} id value to be associated with the record
   * @param baseColumns {@link String[]} line from the db file to read values
   */
  public DbRecord(int id, String[] baseColumns) {
    this.id = id;
    title = baseColumns[TITLE.ordinal()] + baseColumns[VOLUME.ordinal()];
    basePrice = Double.parseDouble(baseColumns[BASE_PRICE.ordinal()]
                                           .replaceAll(" ", ""));
    this.number = Integer.parseInt(baseColumns[NUMBER.ordinal()]
                                           .replaceAll(" ", ""));
    this.initBaseRecord = baseColumns;
  }
  
  /**
   * Generated getter.
   */
  public int getId() {
    return id;
  }
  
  /**
   * Generated getter.
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * Generated getter.
   */
  public double getBasePrice() {
    return basePrice;
  }
  
  /**
   * Generated getter.
   */
  public int getNumber() {
    return number;
  }
  
  /**
   * Updates quantity of the product.
   *
   * @param number {@code int} to be stored in the number column
   */
  public void setNumber(int number) {
    this.number = number;
  }
  
  /**
   * Generated getter.
   */
  public String[] getInitBaseRecord() {
    return initBaseRecord;
  }
  
  /**
   * Updates initial db record with current number value.
   */
  public void updateInitRecord() {
    initBaseRecord[NUMBER.ordinal()] = " " + Integer.toString(number);
  }
  
  /**
   * Constants to identify db file table columns.
   */
  enum Columns {
    TITLE,
    BASE_PRICE,
    CLASS,
    VOLUME,
    DESCRIPTION,
    NUMBER
  }
}