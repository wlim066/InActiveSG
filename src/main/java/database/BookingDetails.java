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

public class BookingDetails implements Comparable<BookingDetails>{
	private int bookingId = 0;
	private int facilityId = 0;
	private String email = null;
	private String date = null;
	private String timeslot = null;
	private String facilityName = null;
	private String locationName = null;

	public BookingDetails() {
	}

	public BookingDetails(int bookingId, String email, String date, String timeslot, int facilityId, String facilityName, String locationName) {
		this.bookingId = bookingId;
		this.facilityId = facilityId;
		this.email = email;
		this.date = date;
		this.timeslot = timeslot;
		this.facilityName = facilityName;
		this.locationName = locationName;
	}
	
    // Implement the compareTo method to define how to compare BookingDetails objects
    @Override
    public int compareTo(BookingDetails other) {
        return Integer.compare(this.bookingId, other.bookingId); // Compare by bookingId
    }

	public int getBookingId() {
		return this.bookingId;
	}

	public int getFacilityId() {
		return this.facilityId;
	}

	public String getEmail() {
		return this.email;
	}

	public String getDate() {
		return this.date;
	}

	public String getTimeslot() {
		return this.timeslot;
	}

	public void setBookingId(int id) {
		this.bookingId = id;
	}

	public void setFacilityId(int id) {
		this.facilityId = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


}
