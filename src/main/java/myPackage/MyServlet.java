package myPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String api_key = "2f63047f7cd540e8a52936b832942a03";
		String city = request.getParameter("city");
		
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + api_key;
		
		
		try {
			//api integration
			URL url = new URL(apiUrl);
			
			//establish the connection
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			
			//reading the data from the network
			InputStream inputStream = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream); 
			
			//store the data
			StringBuilder responseData = new StringBuilder();
			
			Scanner sc = new Scanner(isr);
			while(sc.hasNext()) {
				responseData.append(sc.nextLine());
			}
			sc.close();
			
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseData.toString(), JsonObject.class);
			
			long dateTimeStamp = jsonObject.get("dt").getAsLong()*1000;
			String date = new Date(dateTimeStamp).toString();
			
			double tempInKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int tempInCelsius = (int)(tempInKelvin - 273.15);
			
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
			
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
			
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
			
			//set the data as request attributes
			
			request.setAttribute("date", date);
			request.setAttribute("weatherCondition", weatherCondition);
			request.setAttribute("temperature", tempInCelsius);
			request.setAttribute("humidity", humidity);
			request.setAttribute("windSpeed", windSpeed);
			request.setAttribute("city", city);
			request.setAttribute("weatherData", responseData.toString());
			
			connection.disconnect();
			
			//forward the data to the client
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}

}
