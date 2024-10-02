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

// The instance of BookDBAO gets created when the application
// is deployed. It maintains the Connection object to the
// database. The Connection object is created from DataSource
// object, which is retrieved through JNDI.
// For more information on DataSource, please see
// http://java.sun.com/j2se/1.4.2/docs/api/javax/sql/DataSource.html.
public class BookingDBAO {
	private ArrayList<BookingDetails> bookings ;
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

	public List getBookings() {
		bookings = new ArrayList();

		try {
			String selectStatement = "select * " + "from BOOKING";
			getConnection();

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				BookingDetails bd = new BookingDetails(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5));

				if (rs.getInt(5) > 0) {
					bookings.add(bd);
				}
			}

			prepStmt.close();
			
			System.out.println("BookingDBAO Get bookings:"+ bookings);
		} catch (SQLException ex) {
			System.out.println("getBookings error" + ex);
		}

		releaseConnection();
		Collections.sort(bookings);

		return bookings;
	}

//    public BookingDetails getBookingDetails(int bookingId)
//    {
//        try {
//            String selectStatement = "select * " + "from books where id = ? ";
//            getConnection();
//            
//            PreparedStatement prepStmt = con.prepareStatement(selectStatement);
//            prepStmt.setString(1, bookId);
//            
//            ResultSet rs = prepStmt.executeQuery();
//            
//            if (rs.next()) {
//                BookingDetails bd =
//                        new BookingDetails(rs.getString(1), rs.getString(2),
//                        rs.getString(3), rs.getString(4), rs.getFloat(5),
//                        rs.getBoolean(6), rs.getInt(7), rs.getString(8),
//                        rs.getInt(9));
//                prepStmt.close();
//                releaseConnection();
//                
//                return bd;
//            } else {
//                prepStmt.close();
//                releaseConnection();
//            }
//        } catch (SQLException ex) {
//            releaseConnection();
//            System.out.println("getBookDetails error:" + ex);
//        }
//    }
}
