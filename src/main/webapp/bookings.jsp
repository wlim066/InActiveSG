<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>bookings</title>
<%@ page import = "database.BookingDBAO"%>
<%BookingDBAO booking = new BookingDBAO();
booking.getBookings();
%>
</head>
<body>
bookings test page
</body>
</html>  --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Bookings</title>
<!-- Include Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body>
	<div class="container">
		<h1 class="mt-5">Bookings with Facility Information</h1>

		<table class="table table-striped mt-3" id="bookingsTable">
			<thead class="thead-dark">
				<tr>
					<th scope="col">Booking ID</th>
					<th scope="col">Facility ID</th>
					<th scope="col">Email</th>
					<th scope="col">Date</th>
					<th scope="col">Timeslot</th>
				</tr>
			</thead>
			<tbody id="bookingsBody">
				<!-- Booking rows will be dynamically inserted here by JavaScript -->
			</tbody>
		</table>

		<div id="noBookingsAlert" class="alert alert-warning mt-3"
			style="display: none;" role="alert">No bookings available.</div>
	</div>

	<!-- JavaScript to fetch and display bookings using AJAX -->
	<script>
        // Fetch booking data when the page loads
        window.onload = function() {
            fetchBookings();
        };

        // Function to fetch bookings via AJAX
        function fetchBookings() {
            const xhr = new XMLHttpRequest();
            xhr.open("GET", "BookingServlet", true); // Adjust URL based on servlet mapping
            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.onload = function() {
                if (xhr.status === 200) {
                    const bookings = JSON.parse(xhr.responseText);
                    console.log("Bookings fetched:", bookings);
                    populateTable(bookings);
                } else {
                    console.error("Failed to fetch bookings:", xhr.status, xhr.statusText);
                }
            };

            xhr.onerror = function() {
                console.error("Network error while fetching bookings");
            };

            xhr.send();
        }

 function populateTable(bookings) {
    const tableBody = document.getElementById("bookingsBody");
    if (!tableBody) {
        console.error("Table body element not found");
        return;
    }
    tableBody.innerHTML = ""; // Clear the existing table rows

    if (bookings.length === 0) {
        console.log("No bookings to display");
        document.getElementById("noBookingsAlert").style.display = "block";
        return;
    }

    bookings.forEach(function(booking, index) {
        console.log(`Processing booking ${index}:`, booking);
        const row = document.createElement("tr");
        
        // Debug: Log each property individually
/*         console.log(`Booking ${index} properties:`, {
            bookingId: booking.bookingId,
            facilityId: booking.facilityId,
            email: booking.email,
            date: booking.date,
            timeslot: booking.timeslot
        }); */

        // Create cells individually for better debugging
        const idCell = document.createElement("td");
        idCell.textContent = booking.bookingId || '';
        row.appendChild(idCell);

        const facilityCell = document.createElement("td");
        facilityCell.textContent = booking.facilityId || '';
        row.appendChild(facilityCell);

        const emailCell = document.createElement("td");
        emailCell.textContent = booking.email || '';
        row.appendChild(emailCell);

        const dateCell = document.createElement("td");
        dateCell.textContent = booking.date || '';
        row.appendChild(dateCell);

        const timeslotCell = document.createElement("td");
        timeslotCell.textContent = booking.timeslot || '';
        row.appendChild(timeslotCell);

        tableBody.appendChild(row);
        console.log(`Row ${index} added to table:`, row.innerHTML);
    });

    console.log("Table population complete");
}
    </script>

	<!-- Include Bootstrap JS (optional for interactive components) -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>