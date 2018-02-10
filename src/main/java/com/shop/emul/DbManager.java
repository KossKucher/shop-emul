package com.shop.emul;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DbManager {
  
  private static final String BASE_FILE_NAME = "base.csv";
  
  private static DbManager dbManager = null;
  
  private List<DbRecord> db;
  
  private DbManager() {
    ClassLoader classLoader = getClass().getClassLoader();
    File baseFile = new File(classLoader.getResource(BASE_FILE_NAME).getFile());
    db = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new FileReader(baseFile))) {
      String[] line;
      while ((line = reader.readNext()) != null) {
        db.add(new DbRecord(db.size(), line));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static DbManager get() {
    if (dbManager == null) {
      dbManager = new DbManager();
    }
    return dbManager;
  }
  
  public DbRecord getRecord(int id) {
    return db.get(id);
  }
  
  public boolean isEnough(int id, int number) {
    return getRecord(id).getNumber() >= number;
  }
  
  public int recordsNumber() {
    return db.size();
  }
  
}
