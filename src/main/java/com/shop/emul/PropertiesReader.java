package com.shop.emul;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
  
  private String sourceFileName;
  
  public PropertiesReader(String sourceFileName) {
    this.sourceFileName = sourceFileName;
  }
  
  public void pushToSystem() {
    Properties systemProp = System.getProperties();
    try (InputStream in = getClass().getResourceAsStream("/" + sourceFileName)) {
      systemProp.load(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.setProperties(systemProp);
  }
  
}
