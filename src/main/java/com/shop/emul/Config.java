package com.shop.emul;

public enum Config {
  WHOLESALE_THRESHOLD("1"),
  REPORT_FILENAME("month_report.txt"),
  MONTH_LENGTH("30"),
  OPEN_TIME("8"),
  CLOSE_TIME("21"),
  BUY_THRESHOLD("10"),
  BUY_AMOUNT("150"),
  BASE_FILE_NAME("base.csv"),
  BUYERS_RANDOM_BOUND("10"),
  PROPERTIES_FILE("config.properties");
  
  private String defaultValue;
  
  Config(String defaultValue) {
    this.defaultValue = defaultValue;
  }
  
  public String getDefault() {
    return defaultValue;
  }
}
