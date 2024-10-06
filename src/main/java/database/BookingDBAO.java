/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */

package database;

import java.sql.*;

import javax.sql.*;
import javax.naming.*;
import java.util.*;

// is deployed. It maintains the Connection object to the
// database. The Connection object is created from DataSource
// object, which is retrieved through JNDI.
// For more information on DataSource, please see
// http://java.sun.com/j2se/1.4.2/docs/api/javax/sql/DataSource.html.
public class BookingDBAO {
	private ArrayList<BookingDetails> bookings;
	Connection con;
	private boolean conFree = true;

	// Database configuration
	public static String url = "jdbc:mysql://localhost:3306/test";
	public static String dbdriver = "com.mysql.jdbc.Driver";
	public static String username = "root";
	public static String password = "password";

	public BookingDBAO() throws Exception {
		try {
			Class.forName(dbdriver);
			con = DriverManager.getConnection(url, username, password);
			System.out.println("SUCCESS!" + con);
		} catch (Exception ex) {
			System.out.println("Exception in BookingDBAO: " + ex);
			throw new Exception("Couldn't open connection to database: " + ex.getMessage());
		}
	}

	public void remove() {
		try {
			con.close();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	protected synchronized Connection getConnection() {
		while (conFree == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		conFree = false;
		notify();

		return con;
	}

	protected synchronized void releaseConnection() {
		while (conFree == true) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		conFree = true;
		notify();
	}

	public List<BookingDetails> getBookings() {
		bookings = new ArrayList();
		try {
			String selectStatement = "SELECT b.id, b.email, b.date, b.timeslot, b.facilityId,"
					+ "f.name, f.locationName " + "FROM test.BOOKING b "
					+ "JOIN test.FACILITY f ON b.facilityId = f.id";

			getConnection();

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				BookingDetails bd = new BookingDetails(rs.getInt("id"), rs.getString("email"), rs.getString("date"),
						rs.getString("timeslot"), rs.getInt("facilityId"), rs.getString("name"),
						rs.getString("locationName"));

				// Assuming you want to add all bookings to the list
				bookings.add(bd);
			}

			prepStmt.close();

			System.out.println("BookingDBAO Get bookings:" + bookings);
		} catch (SQLException ex) {
			System.out.println("getBookings error" + ex);
		}

		releaseConnection();
		Collections.sort(bookings);

		return bookings;
	}

	public boolean deleteBooking(int bookingId) {
		boolean success = false;
		String deleteStatement = "DELETE FROM test.BOOKING WHERE id = ?";

		try {
			getConnection();
			PreparedStatement prepStmt = con.prepareStatement(deleteStatement);
			prepStmt.setInt(1, bookingId);

			int rowsAffected = prepStmt.executeUpdate();
			if (rowsAffected > 0) {
				success = true;
			}

			prepStmt.close();
		} catch (SQLException ex) {
			System.out.println("Delete booking error: " + ex.getMessage());
		} finally {
			releaseConnection();
		}

		return success;
	}
	
	public boolean createBooking(String email, String date, String timeslot, int facilityId) {
		boolean success = false;
		String insertSQL = "INSERT INTO booking (email, date, timeslot, facilityId) VALUES (?, ?, ?, ?)";


		try {
			getConnection();
			PreparedStatement prepStmt = con.prepareStatement(insertSQL);
			prepStmt.setString(1, email);
			prepStmt.setString(2, date);
			prepStmt.setString(3, timeslot);
			prepStmt.setInt(4, facilityId);

			int rowsAffected = prepStmt.executeUpdate();
			if (rowsAffected > 0) {
				success = true;
			}

			prepStmt.close();
		} catch (SQLException ex) {
			System.out.println("Insert booking error: " + ex.getMessage());
		} finally {
			releaseConnection();
		}

		return success;
	}
}
