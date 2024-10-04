
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
					<th scope="col">Email</th>
					<th scope="col">Facility ID</th>
					<th scope="col">Facility</th>
					<th scope="col">Location</th>
					<th scope="col">Date</th>
					<th scope="col">Timeslot</th>
					<th scope="col">Cancel Booking</th>
				</tr>
			</thead>
			<tbody id="bookingsBody">
				<!-- Booking rows will be dynamically inserted here by JavaScript -->
			</tbody>
		</table>

		<div id="noBookingsAlert" class="alert alert-warning mt-3"
			style="display: none;" role="alert">No bookings available.</div>
		<!-- Add the pagination controls here -->
		<nav aria-label="Bookings pagination">
			<ul class="pagination justify-content-center" id="pagination">
				<!-- Pagination items will be dynamically inserted here -->
			</ul>
		</nav>
	</div>

	<!-- Delete Confirmation Modal -->
	<div class="modal fade" id="deleteConfirmModal" tabindex="-1"
		aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="deleteConfirmModalLabel">Confirm
						Deletion</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p>Are you sure you want to cancel this booking?</p>
					<dl class="row">
						<dt class="col-sm-3">Location:</dt>
						<dd class="col-sm-9" id="modalLocation"></dd>
						<dt class="col-sm-3">Date:</dt>
						<dd class="col-sm-9" id="modalDate"></dd>
						<dt class="col-sm-3">Timeslot:</dt>
						<dd class="col-sm-9" id="modalTimeslot"></dd>
					</dl>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-danger" id="confirmDeleteBtn">Delete</button>
				</div>
			</div>
		</div>
	</div>

	<!-- JavaScript to fetch and display bookings using AJAX -->
	<script>
        // Fetch booking data when the page loads
        window.onload = function() {
            fetchBookings();
        };
        let bookings = [];
        let currentPage = 1;
        const itemsPerPage = 3; 

        // Function to fetch bookings via AJAX
        function fetchBookings() {
            const xhr = new XMLHttpRequest();
            xhr.open("GET", "BookingServlet", true); // Adjust URL based on servlet mapping
            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.onload = function() {
                if (xhr.status === 200) {
                    bookings = JSON.parse(xhr.responseText);
                    console.log("Bookings fetched:", bookings);
                    displayBookings(currentPage);
                    setupPagination();
                } else {
                    console.error("Failed to fetch bookings:", xhr.status, xhr.statusText);
                }
            };

            xhr.onerror = function() {
                console.error("Network error while fetching bookings");
            };

            xhr.send();
        }
        
        function deleteBooking(bookingId) {
            const xhr = new XMLHttpRequest();
            xhr.open("POST", "BookingServlet", true);
            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            xhr.onload = function() {
                if (xhr.status === 200) {
                    // Remove the deleted booking from the array
                    bookings = bookings.filter(booking => booking.bookingId !== bookingId);
                    // Refresh the display
                    displayBookings(currentPage);
                    setupPagination();
                } else {
                    console.error("Failed to delete booking:", xhr.status, xhr.statusText);
                    alert("Failed to delete booking. Please try again.");
                }
            };

            xhr.onerror = function() {
                console.error("Network error while deleting booking");
                alert("Network error. Please try again.");
            };

            xhr.send("_method=DELETE&id=" + encodeURIComponent(bookingId));
        }
 
 function displayBookings(page) {
	 console.log("displayBookings: "+ page);
	    const tableBody = document.getElementById("bookingsBody");
	    if (!tableBody) {
	        console.error("Table body element not found");
	        return;
	    }
	    tableBody.innerHTML = ""; // Clear the existing table rows

	    const startIndex = (page - 1) * itemsPerPage;
	    const endIndex = startIndex + itemsPerPage;
	    const pageBookings = bookings.slice(startIndex, endIndex);

	    if (pageBookings.length === 0) {
	        document.getElementById("noBookingsAlert").style.display = "block";
	        return;
	    } else {
	        document.getElementById("noBookingsAlert").style.display = "none";
	    }

	    pageBookings.forEach(function(booking, index) {
	        const row = document.createElement("tr");
	        
	        const idCell = document.createElement("td");
	        idCell.textContent = booking.bookingId || '';
	        row.appendChild(idCell);
	        
	        const emailCell = document.createElement("td");
	        emailCell.textContent = booking.email || '';
	        row.appendChild(emailCell);

	        const facilityCell = document.createElement("td");
	        facilityCell.textContent = booking.facilityId || '';
	        row.appendChild(facilityCell);
	        
	        const facilityNameCell = document.createElement("td");
	        facilityNameCell.textContent = booking.facilityName || '';
	        row.appendChild(facilityNameCell);
	      
	        const facilityLocationCell = document.createElement("td");
	        facilityLocationCell.textContent = booking.location || '';
	        row.appendChild(facilityLocationCell);

	        const dateCell = document.createElement("td");
	        dateCell.textContent = booking.date || '';
	        row.appendChild(dateCell);

	        const timeslotCell = document.createElement("td");
	        timeslotCell.textContent = booking.timeslot || '';
	        row.appendChild(timeslotCell);
	        
            const actionCell = document.createElement("td");
            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.classList.add("btn", "btn-danger", "btn-sm");
            deleteButton.onclick = function() {
                showCancelConfirmModal(booking);
            };
            actionCell.appendChild(deleteButton);
            row.appendChild(actionCell);

	        tableBody.appendChild(row);
	    });
	}
 
/*  function confirmDelete(bookingId) {
     if (confirm("Are you sure you want to delete this booking?")) {
         deleteBooking(bookingId);
     }
 }
  */
  
  function showCancelConfirmModal(booking) {
	  const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
	  const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
	  // Populate the modal with booking details
	  document.getElementById('modalLocation').textContent = booking.location || 'N/A';
	  document.getElementById('modalDate').textContent = booking.date || 'N/A';
	  document.getElementById('modalTimeslot').textContent = booking.timeslot || 'N/A';
	  confirmDeleteBtn.onclick = function() {
	    deleteBooking(booking.bookingId);
	    modal.hide();
	  };
	  
	  modal.show();
	}
  
 function setupPagination() {
	 console.log("Setting up pagination");
	    const pageCount = Math.ceil(bookings.length / itemsPerPage);
	    const paginationElement = document.getElementById("pagination");
	    paginationElement.innerHTML = "";

	    // Previous button
	    addPaginationItem(paginationElement, "Previous", currentPage > 1, () => {
	        if (currentPage > 1) {
	            currentPage--;
	            displayBookings(currentPage);
	            setupPagination();
	        }
	    });

	    // Page numbers
	    const maxPagesToShow = 5;
	    let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
	    let endPage = Math.min(pageCount, startPage + maxPagesToShow - 1);
	    startPage = Math.max(1, endPage - maxPagesToShow + 1);

	    for (let i = startPage; i <= endPage; i++) {
	        addPaginationItem(paginationElement, i.toString(), true, () => {
	            currentPage = i;
	            displayBookings(currentPage);
	            setupPagination();
	        }, i === currentPage);
	    }

	    // Next button
	    addPaginationItem(paginationElement, "Next", currentPage < pageCount, () => {
	        if (currentPage < pageCount) {
	            currentPage++;
	            displayBookings(currentPage);
	            setupPagination();
	        }
	    });
	}

	function addPaginationItem(parent, text, enabled, onClick, isActive = false) {
	    const li = document.createElement("li");
	    li.classList.add("page-item");
	    if (!enabled) li.classList.add("disabled");
	    if (isActive) li.classList.add("active");
	    const a = document.createElement("a");
	    a.classList.add("page-link");
	    a.href = "#";
	    a.textContent = text;
	    if (enabled) {
	        a.addEventListener("click", function(e) {
	            e.preventDefault();
	            onClick();
	        });
	    }
	    li.appendChild(a);
	    parent.appendChild(li);
	}
	
    </script>

	<!-- Include Bootstrap JS (optional for interactive components) -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>