package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;
import java.io.PrintWriter;
import java.sql.SQLException;
import database.BookingDBAO;
import database.BookingDetails;

/**
 * Servlet implementation class BookingServlet
 */
public class BookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private BookingDBAO bookingDBAO;

	// Initialize the BookingDBAO in the init() method
	@Override
	public void init() throws ServletException {
		try {
			bookingDBAO = new BookingDBAO(); // Proper initialization with exception handling
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException("Failed to initialize BookingDBAO", e); // Handle exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void destroy() {
		bookingDBAO = null;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BookingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String escapeJson(String value) {
		if (value == null) {
			return null;
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("In booking servlet get");
		try {
			List<BookingDetails> bookings = bookingDBAO.getBookings();

			// Set response type to JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			// Write JSON response manually
			PrintWriter out = response.getWriter();
			out.print("["); // Start JSON array
			for (int i = 0; i < bookings.size(); i++) {
				BookingDetails booking = bookings.get(i);

				// Manually building the JSON string
				out.print("{");
				out.print("\"bookingId\": " + booking.getBookingId() + ",");
				out.print("\"facilityId\": " + booking.getFacilityId() + ",");
				out.print("\"email\": \"" + escapeJson(booking.getEmail()) + "\",");
				out.print("\"date\": \"" + escapeJson(booking.getDate()) + "\",");
				out.print("\"timeslot\": \"" + escapeJson(booking.getTimeslot()) + "\",");
				out.print("\"facilityName\": \"" + escapeJson(booking.getFacilityName()) + "\",");
				out.print("\"location\": \"" + escapeJson(booking.getLocationName()) + "\"");
				out.print("}");
				
//				System.out.println("Booking Servlet: " + booking.getBookingId());
				if (i < bookings.size() - 1) {
					out.print(","); // Add comma between objects
				}
			}
			out.print("]"); // End JSON array
			out.flush(); // Flush the output to send the response
		} catch (Exception e) {
			throw new ServletException("Error fetching bookings", e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
