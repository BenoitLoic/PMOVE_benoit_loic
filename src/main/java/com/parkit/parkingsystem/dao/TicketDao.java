package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contain DAO method for ticket table saving ticket, retrieve ticket, updating ticket and check
 * recurrent user.
 */
public class TicketDao {

  private static final Logger logger = LogManager.getLogger("TicketDAO");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Method to save ticket in DB.
   *
   * @param ticket
   * @return boolean
   * @throws SQLException
   */
  public boolean saveTicket(Ticket ticket) throws SQLException {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.SAVE_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      //            ps.setInt(1,ticket.getId());
      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(
          5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
      return ps.execute();
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return false;
  }

  /**
   * Method to retrieve ticket from DB.
   *
   * @param vehicleRegNumber
   * @return ticket
   * @throws SQLException
   */
  public Ticket getTicket(String vehicleRegNumber) throws SQLException, ClassNotFoundException {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    Ticket ticket = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.GET_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(1, vehicleRegNumber);
      rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot =
            new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
      }
    } catch ( SQLException e) {
      logger.error("Error fetching next available slot");
      throw e;
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeResultSet(rs);
    }
    return ticket;
  }

  /**
   * Method to update ticket outTime and price.
   *
   * @param ticket
   * @return boolean
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public boolean updateTicket(Ticket ticket) throws SQLException, ClassNotFoundException {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      boolean recurrentUser = checkRecurrentUser(ticket);
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.UPDATE_TICKET);
      if (recurrentUser) {
        ps.setDouble(1, ticket.getPriceRecurrentUser());
      } else {
        ps.setDouble(1, ticket.getPrice());
      }
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setString(3, ticket.getVehicleRegNumber());
      ps.execute();
      return true;
    } catch (SQLException ex) {
      logger.error("Error saving ticket info");
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return false;
  }

  /**
   * Method to check if the vehicleRegNumber already exist in DB.
   *
   * @param ticket
   * @return boolean
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public boolean checkRecurrentUser(Ticket ticket) throws SQLException, ClassNotFoundException {

    boolean result = false;
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {

      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.CHECK_RECURRENT_USER);
      ps.setString(1, ticket.getVehicleRegNumber());
      rs = ps.executeQuery();
      if (rs.next()) {
        final int count = rs.getInt(1);
        if (count > 1) {
          result = true;
        }
      }

    } catch (SQLException ex) {
      logger.error("Error checking recurrent user");
      throw ex;
    } finally {
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return result;
  }
}
