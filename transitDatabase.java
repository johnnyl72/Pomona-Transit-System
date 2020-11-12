package cs4350;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
public class transitDatabase {

	private static final String ADDRESS_PORT = "jdbc:mysql://localhost:3306/lab4";
	private static final String USER = "root";
	private static final String PASSWORD = "randompassword";
	private static Scanner kb;
	private static Connection myConn;
	public static void main(String[] args) throws SQLException {
		
		displayCmds();
		String input;
		kb = new Scanner(System.in);
		myConn = DriverManager.getConnection(ADDRESS_PORT, USER, PASSWORD);

		
		do {
			
			System.out.print("Insert command: ");
			input = kb.nextLine();
			
			switch(input) {
			
			case "ds":
				displaySchedule();
				break;
			case "dt": 
				deleteTrip();
				break;
			case "at": 
				addTrip();
				break;
			case "cd": 
				changeDriver();
				break;
			case "cb": 
				changeBus();
				break;
			case "ss": 
				displayStops();
				break;
			case "dw": 
				displayWeeklySchedule();
				break;
			case "ad": 
				addDriver();
				break;
			case "ab": 
				addBus();
				break;
			case "db": 
				deleteBus();
				break;
			case "iat": 
				insertActualTrip();
				break;
			case "help":
				displayCmds();
				break;
			case "exit":
				System.out.println("Terminating program....");
				kb.close();
				myConn.close();
				System.exit(0);
			default:
				System.out.println("There was an error, please try again");
			}
		} while( !input.equalsIgnoreCase("exit"));
	}
	
	static void displaySchedule() {
		
		System.out.print("Insert StartLocationName: ");
		String StartLocationName = kb.nextLine();
		System.out.print("Insert DestinationName: ");
		String DestinationName = kb.nextLine();
		System.out.print("Insert Date: ");
		String Date = kb.nextLine();
		
		try {

			Statement stmt = myConn.createStatement();
			String queryString = "SELECT toff.scheduledstarttime, toff.scheduledarrivaltime, toff.drivername, toff.busid " + 
								 "FROM tripoffering AS toff JOIN trip AS t ON toff.tripnumber = t.tripnumber " + 
								 "WHERE t.startlocationname = \'" + StartLocationName + 
								 "\' AND t.destinationname = \'" + DestinationName + "\' AND toff.Date = \'" + Date + "\';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// Display column names as string
			String varColNames = "";
			int varColCount = rsMeta.getColumnCount();
			for(int col = 1; col <= varColCount; col++) {
				varColNames = varColNames + rsMeta.getColumnName(col) + " | ";
			}
			System.out.println(varColNames);
			
			// Display column values
			while(rs.next()) {
				for(int col = 1; col <= varColCount; col++) {
					System.out.print(rs.getString(col) + "         ");
				}
				System.out.println("");
			}
			
			// Clean up
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	static void deleteTrip() {
		
		System.out.print("Insert TripNumber: ");
		String TripNumber = kb.nextLine();
		System.out.print("Insert Date: ");
		String Date = kb.nextLine();
		System.out.print("Insert ScheduledStartTime: ");
		String ScheduledStartTime = kb.nextLine();
		
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT TripNumber, Date, ScheduledStartTime " +
								 "FROM TripOffering " +
								 "WHERE TripNumber = \'" + TripNumber + "\' AND Date = \'" + Date + "\' AND ScheduledStartTime = \'" + ScheduledStartTime + "\';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (!rs.next()) {
			// If get here, there is no data
				System.out.println("Data does not exist. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to delete data
			String deleteString = "DELETE FROM `tripoffering` " + 
								  "WHERE (TripNumber = '" + TripNumber + "') AND (Date = '" + Date + "') AND (ScheduledStartTime = '" + ScheduledStartTime+ "');";
//			System.out.println(deleteString);
			int result = stmt.executeUpdate(deleteString); 
			if (result == 0)
				System.out.println("Problem with delete") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void addTrip() {
		String input = "";
		do {
			System.out.print("Insert TripNumber: ");
			String TripNumber = kb.nextLine();
			System.out.print("Insert Date: ");
			String Date = kb.nextLine();
			System.out.print("Insert ScheduledStartTime: ");
			String ScheduledStartTime = kb.nextLine();
			System.out.print("Insert ScheduledArrivalTime: ");
			String ScheduledArrivalTime = kb.nextLine();
			System.out.print("Insert DriverName: ");
			String DriverName = kb.nextLine();
			System.out.print("Insert BusID: ");
			String BusID = kb.nextLine();
			
			try {
		
				Statement stmt = myConn.createStatement();
				String queryString = "SELECT TripNumber, Date, ScheduledStartTime " +
									 "FROM TripOffering " +
									 "WHERE TripNumber = \'" + TripNumber + "\' AND Date = \'" + Date + "\' AND ScheduledStartTime = \'" + ScheduledStartTime + "\';";
//				System.out.println(queryString);
				ResultSet rs = stmt.executeQuery(queryString);
				while (rs.next()) {
				// If get here, there is duplicate //data
					System.out.println("Duplicate Data. No changes made");    
				rs.close();	
				stmt.close();
				return;
				}	 
				// OK to insert new data
				String insertString ="INSERT INTO `tripoffering` (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) " + 
				"VALUES (\'" + TripNumber + "\', \'" + Date + "\', \'" + ScheduledStartTime + "\', \'" +  ScheduledArrivalTime + "\', \'" + 
				 DriverName + "\', \'" +  BusID + "\');";
//				System.out.println(insertString);
				int result = stmt.executeUpdate(insertString) ; 
				if (result == 0)
					System.out.println("Problem with insert") ;
				
				rs.close ();
				stmt.close ();
				
				System.out.print("Add more trips? (Yes/No): ");
				input = kb.nextLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while(input.equalsIgnoreCase("yes"));
	
		
	}
	static void changeDriver() {
		
		System.out.print("Insert TripNumber: ");
		String TripNumber = kb.nextLine();
		System.out.print("Insert Date: ");
		String Date = kb.nextLine();
		System.out.print("Insert ScheduledStartTime: ");
		String ScheduledStartTime = kb.nextLine();
		System.out.print("Insert updated DriverName: ");
		String DriverName = kb.nextLine();
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT TripNumber, Date, ScheduledStartTime " +
								 "FROM TripOffering " +
								 "WHERE TripNumber = \'" + TripNumber + "\' AND Date = \'" + Date + "\' AND ScheduledStartTime = \'" + ScheduledStartTime + "\';";
			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (!rs.next()) {
			// If get here, there is no data
				System.out.println("Data does not exist. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to update new data
			String updateString = "UPDATE `tripoffering` SET `DriverName` = '" + DriverName + "' " + 
					"WHERE (`TripNumber` = '" +TripNumber+ "') and (`Date` = '" +Date+ "') and (`ScheduledStartTime` = '"+ScheduledStartTime+"');";  
			System.out.println(updateString);
			int result = stmt.executeUpdate(updateString) ; 
			if (result == 0)
				System.out.println("Problem with update") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void changeBus() {
		
		System.out.print("Insert TripNumber: ");
		String TripNumber = kb.nextLine();
		System.out.print("Insert Date: ");
		String Date = kb.nextLine();
		System.out.print("Insert ScheduledStartTime: ");
		String ScheduledStartTime = kb.nextLine();
		System.out.print("Insert updated BusID: ");
		String BusID = kb.nextLine();
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT TripNumber, Date, ScheduledStartTime " +
								 "FROM TripOffering " +
								 "WHERE TripNumber = \'" + TripNumber + "\' AND Date = \'" + Date + "\' AND ScheduledStartTime = \'" + ScheduledStartTime + "\';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (!rs.next()) {
			// If get here, there is no data
				System.out.println("Data does not exist. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to update new data
			String updateString = "UPDATE `tripoffering` SET `BusID` = '" +BusID+ "' " +
								  "WHERE (`TripNumber` = '"+TripNumber+"') and (`Date` = '"+Date+"') and (`ScheduledStartTime` = '"+ScheduledStartTime+"');";  
//			System.out.println(updateString);
			int result = stmt.executeUpdate(updateString) ; 
			if (result == 0)
				System.out.println("Problem with update") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	static void displayStops() {
		
		System.out.print("Insert TripNumber: ");
		String TripNumber = kb.nextLine();
		
		try {

			Statement stmt = myConn.createStatement();
			String queryString = "SELECT tripnumber, stopnumber, sequencenumber, drivingtime " + 
								 "FROM tripstopinfo " + 
								 "WHERE tripnumber = " + "\'" + TripNumber + "\';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// Display column names as string
			String varColNames = "";
			int varColCount = rsMeta.getColumnCount();
			for(int col = 1; col <= varColCount; col++) {
				varColNames = varColNames + rsMeta.getColumnName(col) + " | ";
			}
			System.out.println(varColNames);
			
			// Display column values
			while(rs.next()) {
				for(int col = 1; col <= varColCount; col++) {
					System.out.print(rs.getString(col) + "         ");
				}
				System.out.println("");
			}
			
			// Clean up
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	static void displayWeeklySchedule() {
		
		System.out.print("Insert DriverName: ");
		String DriverName = kb.nextLine();
		System.out.print("Insert Date: ");
		String Date = kb.nextLine();
		
		String[] token = Date.split("/");
		int day = Integer.parseInt(token[1]) + 6;
		token[1] = Integer.toString(day);
		String endOfWeek = String.join("/", token);
//		System.out.println(endOfWeek);
		try {

			Statement stmt = myConn.createStatement();
			String queryString = "SELECT tripnumber, date, scheduledstarttime, scheduledarrivaltime, drivername, busid\r\n" + 
					"FROM tripoffering\r\n" + 
					"WHERE drivername = '"+DriverName+"' AND date BETWEEN '"+Date+"' AND '" + endOfWeek +"';";
			
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// Display column names as string
			String varColNames = "";
			int varColCount = rsMeta.getColumnCount();
			for(int col = 1; col <= varColCount; col++) {
				varColNames = varColNames + rsMeta.getColumnName(col) + " | ";
			}
			System.out.println(varColNames);
			
			// Display column values
			while(rs.next()) {
				for(int col = 1; col <= varColCount; col++) {
					System.out.print(rs.getString(col) + "         ");
				}
				System.out.println("");
			}
			
			// Clean up
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	static void addDriver() {
		
		System.out.print("Insert DriverName:");
		String DriverName = kb.nextLine();
		System.out.print("Insert DriverTelephoneNumber:");
		String DriverTelephoneNumber = kb.nextLine();
		
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT DriverName " +
								 "FROM Driver " +
								 "WHERE DriverName = \'" + DriverName +"\';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
			// If get here, there is duplicate //data
				System.out.println("Duplicate Data. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to insert new data
			String insertString = "INSERT INTO `driver` (`DriverName`, `DriverTelephoneNumber`) " +
								  "VALUES ('" + DriverName + "', '" + DriverTelephoneNumber + "')";
//			System.out.println(insertString);
			int result = stmt.executeUpdate(insertString) ; 
			if (result == 0)
				System.out.println("Problem with insert") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	static void addBus() {
		
		System.out.print("Insert BusID:");
		String BusID = kb.nextLine();
		System.out.print("Insert Model:");
		String Model = kb.nextLine();
		System.out.print("Insert Year:");
		String Year = kb.nextLine();
		
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT BusID " +
								 "FROM Bus " +
								 "WHERE BusID = \'" + BusID +"\';";
			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
			// If get here, there is duplicate //data
				System.out.println("Duplicate Data. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to insert new data
			String insertString = "INSERT INTO `bus` (`BusID`, `Model`, `Year`)" + 
								  "VALUES ('" + BusID + "', '" + Model + "', '" + Year + "');";

			System.out.println(insertString);
			int result = stmt.executeUpdate(insertString) ; 
			if (result == 0)
				System.out.println("Problem with insert") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void deleteBus() {
		
		System.out.print("Insert BusID:");
		String BusID = kb.nextLine();
		
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT BusID " +
								 "FROM Bus " +
								 "WHERE BusID = \'" + BusID +"\';";
			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (!rs.next()) {
			// If get here, there is no data
				System.out.println("Data does not exist. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to delete new data
			String deleteString = "DELETE FROM `bus` WHERE (`BusID` = '" + BusID + "');";

			System.out.println(deleteString);
			int result = stmt.executeUpdate(deleteString) ; 
			if (result == 0)
				System.out.println("Problem with delete") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void insertActualTrip() {
		
		System.out.print("Insert TripNumber:");
		String TripNumber = kb.nextLine();
		System.out.print("Insert Date:");
		String Date = kb.nextLine();
		System.out.print("Insert ScheduledStartTime:");
		String ScheduledStartTime = kb.nextLine();
		System.out.print("Insert StopNumber:");
		String StopNumber = kb.nextLine();
		System.out.print("Insert ScheduledArrivalTime:");
		String ScheduledArrivalTime = kb.nextLine();
		System.out.print("Insert ActualStartTime:");
		String ActualStartTime = kb.nextLine();
		System.out.print("Insert ActualArrivalTime:");
		String ActualArrivalTime = kb.nextLine();
		System.out.print("Insert NumberOfPassengerIn:");
		String NumberOfPassengerIn = kb.nextLine();
		System.out.print("Insert NumberOfPassengerOut:");
		String NumberOfPassengerOut = kb.nextLine();
		
		try {
			
			Statement stmt = myConn.createStatement();
			String queryString = "SELECT TripNumber, Date, ScheduledStartTime, StopNumber " +
								 "FROM ActualTripStopInfo " +
								 "WHERE TripNumber = '" + TripNumber + "' AND Date = '" + Date + "' AND ScheduledStartTime = '" + 
								 ScheduledStartTime + "' AND StopNumber = '" + StopNumber + "';";
//			System.out.println(queryString);
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
			// If get here, there is duplicate //data
				System.out.println("Duplicate Data. No changes made");    
			rs.close();	
			stmt.close();
			return;
			}	 
			// OK to insert new data
			String insertString = "INSERT INTO `actualtripstopinfo` (`TripNumber`, `Date`, `ScheduledStartTime`, `StopNumber`, " + 
			"`ScheduledArrivalTime`, `ActualStartTime`, `ActualArrivalTime`, `NumberOfPassengerIn`, `NumberOfPassengerOut`) " +
			"VALUES ('" + TripNumber + "', '" + Date + "', '" + ScheduledStartTime + "', '" + StopNumber +
			"', '" + ScheduledArrivalTime + "', '" + ActualStartTime +"', '" + ActualArrivalTime+"', '" + NumberOfPassengerIn + "', '" + NumberOfPassengerOut +"');";

//			System.out.println(insertString);
			int result = stmt.executeUpdate(insertString) ; 
			if (result == 0)
				System.out.println("Problem with insert") ;
			
			rs.close ();
			stmt.close ();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void displayCmds() {
		
		System.out.println("-------------------- Pomona Transit System --------------------\n");
		System.out.println("ds:\t\tDisplay the schedule");
		System.out.println("dt:\t\tDelete trip offering");
		System.out.println("at:\t\tAdd trip offering");
		System.out.println("cd:\t\tChange the driver for a given trip offering");
		System.out.println("cb:\t\tChange the bus for a given trip offering");
		System.out.println("ss:\t\tDisplay the stops of a given trip");
		System.out.println("dw:\t\tDisplay the weekly schedule");
		System.out.println("ad:\t\tAdd a driver");
		System.out.println("ab:\t\tAdd a bus");
		System.out.println("db:\t\tDelete a bus");
		System.out.println("iat:\t\tRecord(insert) to ActualTripStopInfo");
		System.out.println("help:\t\tDisplay commands");
		System.out.println("exit:\t\tTerminate program");
		
	}
}
