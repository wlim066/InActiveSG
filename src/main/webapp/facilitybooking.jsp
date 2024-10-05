<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Facility Booking</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- jQuery (for AJAX) -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
	<div class="container mt-5">
		<h1 class="text-center">Book a Facility</h1>

		<!-- Booking Form -->
		<form id="bookingForm" method="post" action="BookingServlet"
			class="mt-4">
			<!-- Facility Selection -->
			<div class="mb-3">
				<label for="facility" class="form-label">Select Facility</label> <select
					class="form-select" id="facility" name="facility" required>
					<option value="">Choose a facility</option>
				</select>
			</div>

			<!-- Location Selection -->
			<div class="mb-3">
				<label for="location" class="form-label">Select Location</label> <select
					class="form-select" id="location" name="location" required>
					<option value="">Choose a location</option>
				</select>
			</div>

			<!-- Date Selection -->
			<div class="mb-3">
				<label for="date" class="form-label">Select Date</label> <input
					type="date" class="form-control" id="date" name="date" required>
			</div>

			<!-- Time Slot Selection -->
			<div class="mb-3">
				<label for="timeslot" class="form-label">Select Time Slot</label> <select
					class="form-select" id="timeslot" name="timeslot" required>
					<option value="">Choose a time slot</option>
				</select>
			</div>

			<!-- User Information -->
			<div class="mb-3">
				<label for="name" class="form-label">Full Name</label> <input
					type="text" class="form-control" id="name" name="name" required>
			</div>

			<div class="mb-3">
				<label for="email" class="form-label">Email Address</label> <input
					type="email" class="form-control" id="email" name="email" required>
			</div>

			<!-- Submit Button -->
			<div class="d-grid gap-2">
				<button type="submit" class="btn btn-primary">Submit
					Booking</button>
			</div>
		</form>

		<!-- Modal for Success Confirmation (optional) -->
		<div class="modal fade" id="bookingConfirmation" tabindex="-1"
			aria-labelledby="bookingConfirmationLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="bookingConfirmationLabel">Booking
							Confirmed</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">Your booking has been successfully
						confirmed.</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

	<!-- jQuery AJAX -->
	<script>
	// Load facilities on page load
    window.onload = function() {
        loadFacilities();
        setMinDate();
    };

    function setMinDate() {
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('date').setAttribute('min', today);
    }
    
    // Function to fetch facilities
    function loadFacilities() {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "FacilityServlet?action=getFacilitiesName", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onload = function() {
            if (xhr.status === 200) {
                const facilities = JSON.parse(xhr.responseText);
                const facilitySelect = document.getElementById("facility");

                facilities.forEach(facility => {
                    const option = document.createElement("option");
                    option.value = facility.facilityName;
                    option.textContent = facility.facilityName;
                    facilitySelect.appendChild(option);
                });
            } else {
                console.error("Failed to fetch facilities:", xhr.status, xhr.statusText);
            }
        };

        xhr.onerror = function() {
            console.error("Network error while fetching facilities");
        };

        xhr.send();
    }

    // Load locations when a facility is selected
    $("#facility").change(function() {
        const facilityName = $(this).val();
        if (facilityName) {
            loadLocations(facilityName);
        } else {
            $("#location").html('<option value="">Choose a location</option>'); // Reset locations
            $("#timeslot").html('<option value="">Choose a timeslot</option>'); // Reset timeslots
        }
        $("#date").val('');
    });

    // Function to fetch locations based on the selected facility
    function loadLocations(facilityName) {
        if (!facilityName) {
            console.error("Facility name is not provided.");
            return; // Exit early if facilityName is not valid
        }
        const xhr = new XMLHttpRequest();
        xhr.open("GET", `FacilityServlet?action=getFacilityLocations&facilityName=` + encodeURIComponent(facilityName), true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onload = function() {
            if (xhr.status === 200) {
                const locations = JSON.parse(xhr.responseText);
                const locationSelect = document.getElementById("location");
                locationSelect.innerHTML = '<option value="">Choose a location</option>'; // Reset options

                locations.forEach(location => {
                    const option = document.createElement("option");
                    option.value = location.facilityLocation;
                    option.textContent = location.facilityLocation;
                    locationSelect.appendChild(option);
                });
            } else {
                console.error("Failed to fetch locations:", xhr.status, xhr.statusText);
            }
        };

        xhr.onerror = function() {
            console.error("Network error while fetching locations");
        };

        xhr.send();
    }

    // Load available timeslots when a location is selected
    $("#location").change(function() {
    	$("#date").val('');
    });
    
    // Load available timeslots when a location is selected
    $("#date").change(function() {
    	const selectedDate = $(this).val();
        const location = $("#location").val();
        const facilityName = $("#facility").val(); // Get the selected facility name
        if (location && facilityName) {
            loadTimeslots(location, facilityName, selectedDate);
        } else {
            $("#timeslot").html('<option value="">Choose a timeslot</option>'); // Reset timeslots
        }
    });

    // Function to fetch available timeslots based on the selected location and facility
    function loadTimeslots(location, facilityName, selectedDate) {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", `FacilityServlet?action=getFacilityTimeslots&facilityName=` + encodeURIComponent(facilityName) +`&location=` +encodeURIComponent(location)
        		 +`&date=` +encodeURIComponent(selectedDate), true);
        console.log("url:"+ `TimeslotServlet?action=getFacilityTimeslots&facilityName=` + encodeURIComponent(facilityName) +`&location=` +encodeURIComponent(location)
       		 +`&date=` +encodeURIComponent(selectedDate));
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onload = function() {
            if (xhr.status === 200) {
                const timeslots = JSON.parse(xhr.responseText);
                const timeslotSelect = document.getElementById("timeslot");
                timeslotSelect.innerHTML = '<option value="">Choose a timeslot</option>'; // Reset options

                timeslots.forEach(timeslot => {
                    const option = document.createElement("option");
                    option.value = timeslot.facilityTimeslot;
                    option.textContent = timeslot.facilityTimeslot;
                    timeslotSelect.appendChild(option);
                });
            } else {
                console.error("Failed to fetch timeslots:", xhr.status, xhr.statusText);
            }
        };

        xhr.onerror = function() {
            console.error("Network error while fetching timeslots");
        };

        xhr.send();
    }
	</script>
</body>
</html>
