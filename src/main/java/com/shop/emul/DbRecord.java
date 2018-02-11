package com.shop.emul;

import static com.shop.emul.DbRecord.Columns.*;

public class DbRecord {
  
  private int id;
  private int number;
  private double basePrice;
  private String title;
  private String[] initBaseRecord;
  
  public DbRecord(int id, String[] baseColumns) {
    this.id = id;
    title = baseColumns[TITLE.ordinal()] + baseColumns[VOLUME.ordinal()];
    basePrice = Double.parseDouble(baseColumns[BASE_PRICE.ordinal()]
                                           .replaceAll(" ", ""));
    this.number = Integer.parseInt(baseColumns[NUMBER.ordinal()]
                                           .replaceAll(" ", ""));
    this.initBaseRecord = baseColumns;
  }
  
  public int getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public double getBasePrice() {
    return basePrice;
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
  
  public String[] getInitBaseRecord() {
    return initBaseRecord;
  }
  
  public void updateInitRecord() {
    initBaseRecord[NUMBER.ordinal()] = " " + Integer.toString(number);
  }
  
  enum Columns {
    TITLE,
    BASE_PRICE,
    CLASS,
    VOLUME,
    DESCRIPTION,
    NUMBER
  }
}