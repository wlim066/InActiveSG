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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Base64;

// is deployed. It maintains the Connection object to the
// database. The Connection object is created from DataSource
// object, which is retrieved through JNDI.
// For more information on DataSource, please see
// http://java.sun.com/j2se/1.4.2/docs/api/javax/sql/DataSource.html.
public class AccountDBAO {

	Connection con;
	private boolean conFree = true;

	// Database configuration
	public static String url = "jdbc:mysql://localhost:3306/test";
	public static String dbdriver = "com.mysql.jdbc.Driver";
	public static String username = "root";
	public static String password = "password";

	public AccountDBAO() throws Exception {
		try {
			/*
			 * Context initCtx = new InitialContext(); Context envCtx = (Context)
			 * initCtx.lookup("java:comp/env"); DataSource ds = (DataSource)
			 * envCtx.lookup("jdbc/BookDB"); con = ds.getConnection();
			 */
			Class.forName(dbdriver);
			con = DriverManager.getConnection(url, username, password);
			System.out.println("SUCCESS!" + con);

		} catch (Exception ex) {
			System.out.println("Exception in AccountDBAO: " + ex);
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

	public boolean authenticate(String id, String password) throws NoSuchAlgorithmException {
		boolean status = false;
		try {
//			String selectStatement = "select * " + "from accounts where id = ? and password = ?";
			String selectStatement = "select * from accounts where id =?";
			getConnection();

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, id);
//			prepStmt.setString(2, password);

			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				String hashPasswordDB = rs.getString("password");
				String saltDB = rs.getString("salt");

				if (hashPasswordDB.equals(hashPassword(password + saltDB))) {
					status = true;
				}

			}

			prepStmt.close();
			releaseConnection();

		} catch (SQLException ex) {
			releaseConnection();
			ex.printStackTrace();
		}
		return status;
	}

	public boolean create(String id, String password) throws NoSuchAlgorithmException {
		boolean status = false;

		try {
			// String passwordHashedSalted = hashAndSaltPassword(password);

			String salt = getSalt();
			String passwordHashedSalted = hashPassword(password + salt);
			String sqlStatement = "insert into accounts(id,password, salt) values (?,?,?);";
			getConnection();

			PreparedStatement prepStmt = con.prepareStatement(sqlStatement);
			System.out.println(prepStmt);
			prepStmt.setString(1, id);
			prepStmt.setString(2, passwordHashedSalted);
			prepStmt.setString(3, salt);

			int x = prepStmt.executeUpdate();

			if (x == 1) {
				status = true;
			}

			prepStmt.close();
			releaseConnection();
		} catch (SQLException ex) {
			releaseConnection();
			ex.printStackTrace();
		}
		return status;
	}

	public static String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(password.getBytes());
		byte[] mdArray = md.digest();
		StringBuilder sb = new StringBuilder(mdArray.length * 2);
		for (byte b : mdArray) {
			int v = b & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

	public static String hashAndSaltPassword(String password) throws NoSuchAlgorithmException {
		String salt = getSalt();
		return hashPassword(password + salt);
	}

	public static String getSalt() {
		Random r = new SecureRandom();
		byte[] saltBytes = new byte[32];
		r.nextBytes(saltBytes);
		return Base64.getEncoder().encodeToString(saltBytes);
	}

}
