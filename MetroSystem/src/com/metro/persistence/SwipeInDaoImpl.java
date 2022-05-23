package com.metro.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import com.metro.exceptions.LowBalanceException;

public class SwipeInDaoImpl implements SwipeInDao {

	private CardBalanceDao cardBalance = new CardBalanceDaoImpl();

	public boolean swipeIn(int metroCardId, int sourceStationId) {
		PreparedStatement preparedStatement = null;
		Timestamp current = Timestamp.from(Instant.now());
		double balance = 0.0d;
		balance = cardBalance.getCardBalance(metroCardId);
		if (balance >= 20) {
			try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/metro_system", 
					"root", "wiley");) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				preparedStatement = connection.prepareStatement("INSERT INTO JOURNEY"
						+ "(METRO_CARD_ID, SOURCE_STATION_ID, SOURCE_TIME_STAMP, DESTINATION_STATION_ID, DESTINATION_TIME_STAMP, TOTAL_FARE)"
						+ "VALUES(?,?,?,null,null,null)");
				preparedStatement.setInt(1, metroCardId);
				preparedStatement.setInt(2, sourceStationId);
				preparedStatement.setTimestamp(3, current);
				
				preparedStatement.executeUpdate();
				return true;

			} catch (SQLException e) {
				System.out.println(e);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		else {
			try {
				throw new LowBalanceException("Card Has Low Balance! Please Recharge...");
			} catch (LowBalanceException e) {
				System.out.println(e.getMessage());
			}
		}
		return false;
	}
}

