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
public class FacilityDBAO {
	private ArrayList<FacilityDetails> facilities;
	private ArrayList<String> facilitiesLocation;
	private ArrayList<String> facilityTimeslot;
	Connection con;
	private boolean conFree = true;

	// Database configuration
	public static String url = "jdbc:mysql://localhost:3306/test";
	public static String dbdriver = "com.mysql.jdbc.Driver";
	public static String username = "root";
	public static String password = "password";

	public FacilityDBAO() throws Exception {
		try {
			Class.forName(dbdriver);
			con = DriverManager.getConnection(url, username, password);
			System.out.println("SUCCESS!" + con);
		} catch (Exception ex) {
			System.out.println("Exception in FacilityDBAO: " + ex);
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

	public List<String> getFacilities() {
		List<String> facilities = new ArrayList<>();

		try {
			String selectStatement = "select DISTINCT(name) from FACILITY";

			getConnection();

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				String facilityName = rs.getString("name");
				// Assuming you want to add all bookings to the list
				facilities.add(facilityName);
			}

			prepStmt.close();

			System.out.println("FacilityDBAO Get facilities:" + facilities);
		} catch (SQLException ex) {
			System.out.println("getfacilities error" + ex);
		}

		releaseConnection();
		Collections.sort(facilities);

		return facilities;
	}

	public List<FacilityDetails> getFacilityLocation(String facilityName) {
		List<FacilityDetails> facilitiesLocation = new ArrayList<>();
		System.out.println("getFacilityLocation: " + facilityName);
		String selectStatement = "SELECT id, locationName FROM FACILITY WHERE status = 1 AND name = ?";

		getConnection();
		try (PreparedStatement ps = con.prepareStatement(selectStatement)) {
			ps.setString(1, facilityName);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int facilityId = rs.getInt("id");
					String location = rs.getString("locationName");
					facilitiesLocation.add(new FacilityDetails(facilityId, location));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}

		Collections.sort(facilitiesLocation);
		return facilitiesLocation;
	}

	public List<String> getFacilityTimeslot(String facilityName, String location, String date) {
		facilityTimeslot = new ArrayList<String>();
		System.out.println("getFacilityTimeslot: " + facilityName + location + date);
		String selectStatement = "select booking.timeslot from FACILITY,BOOKING WHERE status = 1 AND name = ? AND locationName = ? AND date = ? AND FACILITY.id = BOOKING.facilityId";

		getConnection();
		try (PreparedStatement ps = con.prepareStatement(selectStatement)) {
			ps.setString(1, facilityName);
			ps.setString(2, location);
			ps.setString(3, date);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String timeslot = rs.getString("timeslot"); // Get the location
				facilityTimeslot.add(timeslot); // Add the location to the list
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		releaseConnection();
		Collections.sort(facilityTimeslot);
		return facilityTimeslot;
	}
}
