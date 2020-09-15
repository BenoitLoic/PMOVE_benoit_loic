package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDao parkingSpotDAO;
  private static TicketDao ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;
  private static ParkingService parkingService;
  private static Connection con;

  @Mock private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() {
    parkingSpotDAO = new ParkingSpotDao();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO = new TicketDao();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @AfterAll
  private static void tearDown() {}

  @BeforeEach
  private void setUpPerTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("CAR1");
    dataBasePrepareService.clearDataBaseEntries();
    con = dataBaseTestConfig.getConnection();
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
  }

  @Test
  public void testParkingACar() throws SQLException, ClassNotFoundException {
    String available = "";
    String regNumber = "";
    // GIVEN
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    // WHEN
    parkingService.processIncomingVehicle();
    // THEN
    String preparedString =
        "SELECT t.*, p.AVAILABLE, p.TYPE "
            + "FROM ticket t, parking p "
            + "WHERE t.PARKING_NUMBER = p.PARKING_NUMBER;";
    PreparedStatement prepStatement = con.prepareStatement(preparedString);
    ResultSet rs = prepStatement.executeQuery();
    while (rs.next()) {
      available = rs.getString("AVAILABLE");
      regNumber = rs.getString("VEHICLE_REG_NUMBER");
    }
    assertEquals("0", available);
    assertEquals("CAR1", regNumber);
    dataBaseTestConfig.closePreparedStatement(prepStatement);
    dataBaseTestConfig.closeResultSet(rs);
    dataBaseTestConfig.closeConnection(con);
  }

  @Test
  public void testParkingLotExit() throws SQLException, ClassNotFoundException {
    String availability = "";
    Timestamp outTime = new Timestamp(System.currentTimeMillis()-1000);
    double firstTimePrice = 0;
    double recurrentPrice = 0;
    // GIVEN
    parkingService.processIncomingVehicle();
    // changing IN_TIME in DB
    String subOneHourString =
        "UPDATE ticket " + "SET IN_TIME = SUBTIME (IN_TIME, '01:00:00') " + "WHERE ID = ?;";
    PreparedStatement ps = con.prepareStatement(subOneHourString);
    ps.setInt(1, 1);
    ps.execute();
    // WHEN
    parkingService.processExitingVehicle();
    parkingService.processIncomingVehicle();
    ps.setInt(1, 2);
    ps.execute();
    dataBaseTestConfig.closePreparedStatement(ps);
    parkingService.processExitingVehicle();
    // THEN
    String preparedString =
        "SELECT t.*, p.AVAILABLE, p.TYPE "
            + "FROM ticket t, parking p "
            + "WHERE t.PARKING_NUMBER = p.PARKING_NUMBER "
            + "AND ID = ?;";
    ps = con.prepareStatement(preparedString);
    ps.setInt(1, 1);
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      outTime = rs.getTimestamp("OUT_TIME");
      availability = rs.getString("AVAILABLE");
      firstTimePrice = rs.getDouble("PRICE");
    }
    dataBaseTestConfig.closeResultSet(rs);
    ps.setInt(1, 2);
    rs = ps.executeQuery();
    if (rs.next()) {
      recurrentPrice = rs.getDouble("PRICE");
    }
    //            check if parkingLot is freed
    assertEquals("1", availability);
    //            check if out_time is populated correctly in DB
    assertEquals(
        DateUtils.round(new Timestamp(System.currentTimeMillis()), Calendar.MINUTE),
        DateUtils.round(outTime, Calendar.MINUTE));
    //          check if fare generated is populated correctly in DB
    assertEquals(Fare.CAR_RATE_PER_HOUR, firstTimePrice, 0.001);
    //          check if discount is applied to recurrent user
    assertEquals(Fare.CAR_RATE_PER_HOUR - Fare.CAR_RATE_PER_HOUR * 5 / 100, recurrentPrice, 0.001);

    dataBaseTestConfig.closeResultSet(rs);
    dataBaseTestConfig.closePreparedStatement(ps);
    dataBaseTestConfig.closeConnection(con);
  }
}
