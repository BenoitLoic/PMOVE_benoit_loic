package com.parkit.parkingsystem.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration for Data Base contain methods to get connection to DB.
 */
public class DataBaseConfig {

  private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");

  /**
   * Method to create Connection to DB.
   *
   * @return DriverManager.getConnection (DB id)
   * @throws SQLException if an error occur wit DB
   * @throws ClassNotFoundException if jdbc.Driver class can't be located
   */
  @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
  public Connection getConnection() throws SQLException, ClassNotFoundException {
    LOGGER.info("Create DB connection");
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/prod?serverTimezone=UTC", "root", "rootroot");
  }

}
