package database;

public class FacilityDetails implements Comparable<FacilityDetails> {
	private int facilityId = 0;
	private int status = 0;
	private String facilityName = null;
	private String locationName = null;

	public FacilityDetails() {
	}

	public FacilityDetails(int facilityId, String facilityName, String locationName, int status) {
		this.facilityId = facilityId;
		this.facilityName = facilityName;
		this.locationName = locationName;
		this.status = status;
	}

	// Implement the compareTo method to define how to compare BookingDetails
	// objects
	@Override
	public int compareTo(FacilityDetails other) {
		return Integer.compare(this.facilityId, other.facilityId); // Compare by bookingId
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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