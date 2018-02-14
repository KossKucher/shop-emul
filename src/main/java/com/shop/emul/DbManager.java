package com.shop.emul;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.shop.emul.Config.BASE_FILE_NAME;
import static com.shop.emul.Config.BUY_AMOUNT;
import static com.shop.emul.Config.BUY_THRESHOLD;

/**
 * Database driver singleton.
 *
 * @author KossKucher
 * @version 1.0
 */
public class DbManager {
  
  private static DbManager dbManager = null;
  
  private final int buyThreshold;
  private final int buyAmount;
  private final String baseFileName;
  
  private List<DbRecord> db;
  
  /**
   * Private constructor.
   * Tries to read the db file from "user.dir" location first.
   * If no base in the "user.dir" location, then default base file is loaded.
   */
  private DbManager() {
    buyThreshold = Integer.parseInt(System.getProperty(BUY_THRESHOLD.name(),
                                                       BUY_THRESHOLD.getDefault()));
    buyAmount = Integer.parseInt(System.getProperty(BUY_AMOUNT.name(), BUY_AMOUNT.getDefault()));
    baseFileName = System.getProperty(BASE_FILE_NAME.name(), BASE_FILE_NAME.getDefault());
    db = new ArrayList<>();
    File baseFile = new File(System.getProperty("user.dir"), baseFileName);
    if (baseFile.exists()) {
      try (CSVReader reader = new CSVReader(new FileReader(baseFile))) {
        String[] line;
        while ((line = reader.readNext()) != null) {
          db.add(new DbRecord(db.size(), line));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try (InputStream in = getClass().getResourceAsStream("/" + baseFileName);
           BufferedReader br = new BufferedReader(new InputStreamReader(in));
           CSVReader reader = new CSVReader(br)) {
        String[] line;
        while ((line = reader.readNext()) != null) {
          db.add(new DbRecord(db.size(), line));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Singleton getter.
   * Creates new instance when is run for the first time, else returns existing instance.
   *
   * @return instance of this class
   */
  public static DbManager get() {
    if (dbManager == null) {
      dbManager = new DbManager();
    }
    return dbManager;
  }
  
  /**
   * Gets db record.
   *
   * @param id {@code int} identifier of the db record
   * @return {@link DbRecord} instance representing the db table line
   */
  public DbRecord getRecord(int id) {
    return db.get(id);
  }
  
  /**
   * Checks if it is enough product to fulfil the order.
   *
   * @param id     {@code int} identifier of the db record
   * @param number {@code int} quantity of the product to check against db
   * @return {@code boolean} true if there is enough product, otherwise false
   */
  public boolean isEnough(int id, int number) {
    return getRecord(id).getNumber() >= number;
  }
  
  /**
   * Checks for db size.
   *
   * @return {@code int} number of the records in db
   */
  public int totalRecords() {
    return db.size();
  }
  
  /**
   * Wrapper method for order processing variants.
   *
   * @param order {@link Order} class to be processed against db
   */
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
  
  /**
   * Processes sell orders against db. Decrements values in db.
   *
   * @param order {@link Order} defines type and amount of product
   */
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
  
  /**
   * Processes buy product orders. Increments db values.
   *
   * @param order {@link Order} defines type and amount of product to process
   */
  private void processBuy(Order order) {
    order.forEach((id, number) ->
                  {
                    DbRecord record = getRecord(id);
                    record.setNumber(record.getNumber() + number);
                  });
  }
  
  /**
   * Generates buy order to fill up the storage and triggers processing by the {@link Cashbox}.
   */
  public void refreshStorage() {
    Order order = new Order(Order.OrderType.BUY);
    for (DbRecord record : db) {
      if (record.getNumber() < buyThreshold) {
        order.put(record.getId(), buyAmount);
      }
    }
    if (order.size() > 0) {
      process(order);
      Cashbox.get().process(order);
    }
  }
  
  /**
   * Writes current base state to the csv file.
   */
  public void backupBase() {
    File baseFile = new File(System.getProperty("user.dir"), baseFileName);
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
