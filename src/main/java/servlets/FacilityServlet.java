package servlets;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import java.util.List;
import java.io.PrintWriter;

import database.BookingDetails;
import database.FacilityDBAO;
import database.FacilityDetails;

/**
 * Servlet implementation class FacilityServlet
 */
public class FacilityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private FacilityDBAO facilityDBAO;

	// Initialize the BookingDBAO in the init() method
	@Override
	public void init() throws ServletException {
		try {
			facilityDBAO = new FacilityDBAO(); // Proper initialization with exception handling
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException("Failed to initialize FacilityDBAO", e); // Handle exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void destroy() {
		facilityDBAO = null;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FacilityServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		System.out.println("In faciliy servlet get");
		// Check which request we are processing
		String action = request.getParameter("action");
	    System.out.println(action);
		if ("getFacilitiesName".equals(action)) {
			getFacilitiesName(response);
		} else if ("getFacilityLocations".equals(action)) {
			getFacilitiesLocation(request, response);
		} else {
			// Handle invalid action
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
		}

	}

	private void getFacilitiesName(HttpServletResponse response) throws ServletException, IOException {

		System.out.println("In faciliy servlet getFaciltiesName");

		try {
			List<String> facilities = facilityDBAO.getFacilities();

			// Set response type to JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			// Write JSON response manually
			PrintWriter out = response.getWriter();
			out.print("["); // Start JSON array

			// Loop through facility names
			for (int i = 0; i < facilities.size(); i++) {
				String facilityName = facilities.get(i);

				// Manually build JSON string for each facility
				out.print("{\"facilityName\": \"" + facilityName + "\"}"); // Properly adding facilityName

				if (i < facilities.size() - 1) {
					out.print(","); // Add comma between objects, but not after the last one
				}
			}

			out.print("]"); // End JSON array
			out.flush(); // Flush the output to send the response

		} catch (Exception e) {
			throw new ServletException("Error fetching facilities", e);
		}

	}

	private void getFacilitiesLocation(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("In faciliy servlet getFaciltiesName");

		try {
			String facilityName = request.getParameter("facilityName");
			System.out.println("getFacilitiesLocation in servlet: " + facilityName);
			List<String> facilityLocations = facilityDBAO.getFacilityLocation(facilityName);

			// Set response type to JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			// Write JSON response manually
			PrintWriter out = response.getWriter();
			out.print("["); // Start JSON array

			// Loop through facility names
			for (int i = 0; i < facilityLocations.size(); i++) {
				String facilityLocation = facilityLocations.get(i);

				// Manually build JSON string for each facility
				out.print("{\"facilityLocation\": \"" + facilityLocation + "\"}");

				if (i < facilityLocations.size() - 1) {
					out.print(","); // Add comma between objects, but not after the last one
				}
			}

			out.print("]"); // End JSON array
			out.flush(); // Flush the output to send the response

		} catch (Exception e) {
			throw new ServletException("Error fetching facilities", e);
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
