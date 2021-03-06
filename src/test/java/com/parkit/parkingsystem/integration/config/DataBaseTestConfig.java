package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseTestConfig extends DataBaseConfig {

  private static final Logger LOGGER = LogManager.getLogger("DataBaseTestConfig");

  @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    LOGGER.info("Create DB connection");
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/test?serverTimezone=UTC", "root", "rootroot");
  }


    }


