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
 * Configuration for Data Base contain methods to get connection to DB and close
 * connection/ResultSet/PreparedStatement.
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

  /**
   * Method to close connection to DB.
   *
   * @param con Connection to close
   * @throws SQLException if there is an error with db
   */
  public void closeConnection(Connection con) throws SQLException {
    if (con != null) {
      try {
        con.close();
        LOGGER.info("Closing DB connection");
      } catch (SQLException e) {
        LOGGER.error("Error while closing connection");
        throw e;
      }
    }
  }

  /**
   * Method to close prepared statement.
   *
   * @param ps PreparedStatement to close
   * @throws SQLException if there is an error with db
   */
  public void closePreparedStatement(PreparedStatement ps) throws SQLException {
    if (ps != null) {
      try {
        ps.close();
        LOGGER.info("Closing Prepared Statement");
      } catch (SQLException e) {
        LOGGER.error("Error while closing prepared statement");
        throw e;
      }
    }
  }

  /**
   * Method to close result set.
   *
   * @param rs result set to close
   * @throws SQLException if there is an error with db
   */
  public void closeResultSet(ResultSet rs) throws SQLException {
    if (rs != null) {
      try {
        rs.close();
        LOGGER.info("Closing Result Set");
      } catch (SQLException e) {
        LOGGER.error("Error while closing result set");
        throw e;
      }
    }
  }
}
