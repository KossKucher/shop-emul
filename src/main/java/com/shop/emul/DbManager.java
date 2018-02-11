package com.shop.emul;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DbManager {
  
  private static final int BUY_THRESHOLD = 10;
  private static final int BUY_AMOUNT = 150;
  private static final String BASE_FILE_NAME = "base.csv";
  
  private static DbManager dbManager = null;
  
  private List<DbRecord> db;
  private File baseFile;
  
  private DbManager() {
    ClassLoader classLoader = getClass().getClassLoader();
    baseFile = new File(classLoader.getResource(BASE_FILE_NAME).getFile());
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
  
  public int totalRecords() {
    return db.size();
  }
  
  public void process(Order order) {
    if (order.size() == 0) {
      return;
    }
    if (order.getOrderType() == Order.OrderType.BUY) {
      processBuy(order);
    } else {
      processSell(order);
    }
  }
  
  private void processSell(Order order) {
    order.forEach((id, number) ->
                  {
                    DbRecord record = getRecord(id);
                    if (record.getNumber() < number) {
                      throw new IllegalStateException("Product number can not be pushed negative.");
                    }
                    record.setNumber(record.getNumber() - number);
                  });
  }
  
  private void processBuy(Order order) {
    order.forEach((id, number) ->
                  {
                    DbRecord record = getRecord(id);
                    record.setNumber(record.getNumber() + number);
                  });
  }
  
  public void refreshStorage() {
    Order order = new Order(Order.OrderType.BUY);
    for (DbRecord record : db) {
      if (record.getNumber() < BUY_THRESHOLD) {
        order.put(record.getId(), BUY_AMOUNT);
      }
    }
    if (order.size() > 0) {
      process(order);
      Cashbox.get().process(order);
    }
  }
  
  public void backupBase() {
    try (Writer writer = new BufferedWriter(new FileWriter(baseFile));
         CSVWriter csvWriter = new CSVWriter(writer,
                                             CSVWriter.DEFAULT_SEPARATOR,
                                             CSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             CSVWriter.NO_ESCAPE_CHARACTER,
                                             CSVWriter.DEFAULT_LINE_END)
    ) {
      for (DbRecord record : db) {
        record.updateInitRecord();
        csvWriter.writeNext(record.getInitBaseRecord());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
