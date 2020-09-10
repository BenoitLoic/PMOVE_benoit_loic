package com.parkit.parkingsystem.constants;

public class DbConstants {

  public static final String GET_NEXT_PARKING_SPOT =
      "SELECT min(PARKING_NUMBER) "
          + "FROM parking "
            + "WHERE AVAILABLE = true "
                + "AND TYPE = ? ;";
  public static final String UPDATE_PARKING_SPOT =
      "UPDATE parking "
          + "SET available = ? "
            + "WHERE PARKING_NUMBER = ? ;";

  public static final String SAVE_TICKET =
      "INSERT INTO ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) "
          + "VALUES(?,?,?,?,?) ;";
  public static final String UPDATE_TICKET =
      "UPDATE ticket "
          + "SET PRICE=?, OUT_TIME=? "
            + "WHERE VEHICLE_REG_NUMBER=? "
                + "ORDER BY ID DESC LIMIT 1 ;";
  public static final String GET_TICKET =
      "SELECT t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE "
          + "FROM ticket t,parking p "
             + "WHERE p.parking_number = t.parking_number AND t.VEHICLE_REG_NUMBER=? "
                + "ORDER BY t.ID DESC  LIMIT 1 ;";
  public static final String CHECK_RECURRENT_USER =
      "SELECT count(*) "
          + "FROM ticket "
            + "WHERE VEHICLE_REG_NUMBER = ? ;";
}
