/**
 * The MIT License (MIT)
Copyright (c) 2015 Saqib Nizam Shamsi
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;


public class DBManager {
	public static final String[] eventMap = {"centralevent", "branchevent", "preference"}; //Event type
	public static final String[] tableMap = {"college", "external"}; //Category
	public static void main(String[] args) throws Exception
	{
		computeResults(getConnection());
		
//		System.out.println(getEventId(getConnection(), 1, "ranbhumi", "centralevents"));
//		System.out.println(getEventId(getConnection(), 1, "i-bridge", "branchevents"));
//		System.out.println(getEventId(getConnection(), 1, "ranbhumi", "centralevents"));
//		System.out.println(getEventId(getConnection(), 1, "i-bridge", "allevents"));
//		System.out.println(getEventId(getConnection(), 1, "enigma", "allevents"));
//		System.out.println(idExists(getConnection(), "COLO12"));
//		vote(getConnection(), "42074", 1, 2, 1);
//		vote(getConnection(), "42074", 2, 8, 1);
		//vote(getConnection(), "42074", 1, 2, 1);
//		boolean[] b = hasVoted(getConnection(), "42070", 1);
//		boolean[] c = hasVoted(getConnection(), "COLO55", 2);
//		System.out.println(Arrays.toString(b));
//		System.out.println(Arrays.toString(c));
//		vote(getConnection(), "42070", 2, 3, 1);
//		vote(getConnection(), "42070", 1, 5, 1);
//		
//		vote(getConnection(), "COLO55", 2, 7, 2);
//		vote(getConnection(), "COLO55", 3, 9, 2);
//		
//		b = hasVoted(getConnection(), "42070", 1);
//		c = hasVoted(getConnection(), "COLO55", 2);
//		System.out.println(Arrays.toString(b));
//		System.out.println(Arrays.toString(c));
	}
	
	public static void reset() throws SQLException, ClassNotFoundException, IOException
	{
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		statement.execute("DELETE FROM college");
		statement.execute("DELETE FROM external");
		statement.execute("DELETE FROM allevents");
		statement.execute("DELETE FROM centralevents");
		statement.execute("DELETE FROM branchevents");
		EventInfo central = new EventInfo(Files.getNAME_OF_THE_FILE_CONTAINING_NAMES_OF_CENTRAL_EVENTS());
		populateEventDBs(connection, "centralevents", central.getEventNames());
		EventInfo branch = new EventInfo(Files.getNAME_OF_THE_FILE_CONTAINING_NAMES_OF_BRANCH_EVENTS());
		populateEventDBs(connection, "branchevents", branch.getEventNames());
		populateAllEventsTable(connection, central.getEventNames(), branch.getEventNames());
		int start, end, college[];
		String prefix;
		college = Utilities.getIDRange();
		String[] tmp = Utilities.getExtIDRange();
		prefix = tmp[0];
		start = Integer.parseInt(tmp[1]);
		end = Integer.parseInt(tmp[2]);
		populateStudentTable(connection, "college", "", college[0], college[1]);
		populateStudentTable(connection, "external", prefix, start, end);
	}
	
	public static void computeResults(Connection connection)
	{
		try {
		connection.prepareStatement("UPDATE centralevents SET votes = 0").execute();
		connection.prepareStatement("UPDATE branchevents SET votes = 0").execute();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int points;
		/*Update vote count for events*/
		System.out.print("Points for Central Events [Internal]: ");
		points = Integer.parseInt(br.readLine());
		calculateVoteCount(connection, "college", "centralevent", "centralevents", points);
		System.out.println();
		
		System.out.print("Points for Branch Events [Internal]: ");
		points = Integer.parseInt(br.readLine());
		calculateVoteCount(connection, "college", "branchevent", "branchevents", points);
		System.out.println();
		
		System.out.print("Points for Central Events [External]: ");
		points = Integer.parseInt(br.readLine());
		calculateVoteCount(connection, "external", "centralevent", "centralevents", points);
		System.out.println();
		
		System.out.print("Points for Branch Events [External]: ");
		points = Integer.parseInt(br.readLine());
		calculateVoteCount(connection, "external", "branchevent", "branchevents", points);
		System.out.println();
		
		/*Update vote count for preferences*/
		System.out.print("Points for Preferences: ");
		points = Integer.parseInt(br.readLine());
		calculatePreferenceCount(connection, points);
		System.out.println();
		
		System.out.println("Success!");
		}
		catch(IOException e)
		{
			System.out.println("Failure!");
			e.printStackTrace();
		}
		catch(SQLException s)
		{
			System.out.println("Failure!");
			s.printStackTrace();
		}
	}
	
	private static void calculateVoteCount(Connection connection,
			String sourceTable, String sourceColumn, String targetTable, int points) {
		
		int id, count, previous;
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		System.out.println("\n" + sourceTable.toUpperCase() + "/" + sourceColumn.toUpperCase() + "S");
		try
		{
			Statement statement = connection.createStatement();
			Statement s2 = connection.createStatement();
			//System.out.println("SELECT " + sourceColumn + " COUNT(" + sourceColumn + ") FROM " + sourceTable + " GROUP BY " + sourceColumn);
			ResultSet resultSet = statement.executeQuery("SELECT " + sourceColumn + ", COUNT(" + sourceColumn + ") FROM " + sourceTable + " GROUP BY " + sourceColumn);
			ResultSet rs = s2.executeQuery("SELECT id, eventname from " + targetTable);
			
			while(rs.next())
			{
				map.put(rs.getInt(1), rs.getString(2));
			}
			
			while(resultSet.next())
			{
				id = resultSet.getInt(1);
				count = resultSet.getInt(2);
				System.out.println(map.get(id) + " : " + count);
				Statement s = connection.createStatement();
				ResultSet rs2 = s.executeQuery("SELECT votes FROM " + targetTable + " WHERE id = " + id);
				rs2.next();
				previous = rs2.getInt(1);
				connection.prepareStatement("UPDATE " + targetTable + " SET votes = " + (previous + count * points) + " WHERE id = " + id).execute();
			}
			
			
		}
		catch(SQLException s)
		{
			System.out.println("Failure!");
			s.printStackTrace();
		}
		
		
	}
	
	public static void calculatePreferenceCount(Connection connection, int points)
	{
		
		int id, count, previous1, previous2;
		HashMap<Integer, String> map = new HashMap<Integer, String>();

		try
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT preference, COUNT(preference) FROM external GROUP BY preference");
			
			Statement s2 = connection.createStatement();
			ResultSet rs = s2.executeQuery("SELECT id, eventname FROM allevents");
			
			while(rs.next())
			{
				map.put(rs.getInt(1), rs.getString(2));
			}
			
			while(resultSet.next())
			{
				previous1 = previous2 = 0;
				id = resultSet.getInt(1);
				count = resultSet.getInt(2);
				System.out.println(map.get(id) + " : " + count);
				Statement s = connection.createStatement();
				ResultSet rs2 = s.executeQuery("SELECT votes FROM centralevents WHERE eventname = \'" + map.get(id) + "\'");
				while(rs2.next())
				{
					previous1 = rs2.getInt(1);
				}
				Statement s22 = connection.createStatement();
				ResultSet rs22 = s22.executeQuery("SELECT votes FROM branchevents WHERE eventname = \'" + map.get(id) + "\'");
				
				while(rs22.next())
				{
					previous2 = rs22.getInt(1);
				}
				connection.prepareStatement("UPDATE centralevents SET votes = " + (previous1 + previous2 + count * points) + " WHERE eventname = \'" + map.get(id) + "\'").execute();
				connection.prepareStatement("UPDATE branchevents SET votes = " + (previous1 + previous2 + count * points) + " WHERE eventname = \'" + map.get(id) + "\'").execute();
			}
			
			
		}
		catch(SQLException s)
		{
			System.out.println("Failure!");
			s.printStackTrace();
		}
		
	}

	public static Connection getConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = (Connection) DriverManager.getConnection(Constants.DATABASE_URI, Constants.USERNAME, Constants.PASSWORD);
        return connection;
    }
	
	public static void populateEventDBs(Connection connection, String tableName, String[] events) throws SQLException
	{
		Statement statement = connection.createStatement();
		//System.out.println("INSERT INTO "+ tableName +" (id, eventname, votes) VALUES (0, \'"+ Constants.DUMMY_EVENT + "\', 0)");
		statement.execute("INSERT INTO "+ tableName +" (id, eventname, votes) VALUES (0, \'"+ Constants.DUMMY_EVENT + "\', 0)");
		for(int i = 0; events[i] != null; i++)
		{
			//System.out.println("INSERT INTO " + tableName + " (id, eventname, votes) " + " VALUES (" + (i + 1) + ", \'" + events[i] + "\', 0)");
			statement.execute("INSERT INTO " + tableName + " (id, eventname, votes) " + " VALUES (" + (i + 1) + ", \'" + events[i] + "\', 0)");
		}
	}
	
	public static void populateAllEventsTable(Connection connection, String[] centralEvents, String[] branchEvents) throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.execute("INSERT INTO allevents (id, eventname, votes) VALUES (0, \'"+ Constants.DUMMY_EVENT + "\', 0)");
		int idx = 1;
		for(int i = 0; centralEvents[i] != null; i++, idx++)
		{
			statement.execute("INSERT INTO " + "allevents" + " (id, eventname, votes) " + " VALUES (" + (idx) + ", \'" + centralEvents[i] + "\', 0)");
		}
		for(int i = 0; branchEvents[i] != null; i++, idx++)
		{
			//System.out.println("INSERT INTO " + "allevents" + " (id, eventname, votes) " + " VALUES (" + (idx) + ", \'" + branchEvents[i] + "\', 0)");
			statement.execute("INSERT INTO " + "allevents" + " (id, eventname, votes) " + " VALUES (" + (idx) + ", \'" + branchEvents[i] + "\', 0)");
		}
	}
	
	public static void populateStudentTable(Connection connection, String tableName, String prefix, int startId, int lastId) throws SQLException
	{
		Statement statement = connection.createStatement();
		for(int i = startId; i <= lastId; i++)
		{
			statement.execute("INSERT INTO " + tableName + " (id, centralevent, branchevent) VALUES (\'" + (prefix + i) + "\', 0, 0)");
		}
	}
	
	/**
	 * 
	 * @param connection Connection object to establish a connection with the database
	 * @param id ID of the student casting the vote
	 * @param category College(1), External(2)
	 * @return
	 * @throws Exception
	 */
	public static boolean[] hasVoted(Connection connection, String id, int category) throws Exception
	{
		Statement statement = connection.createStatement();
		switch(category)
		{
		case 1:
			//Internal
			boolean[] voted = new boolean[2];
			ResultSet rs = statement.executeQuery("SELECT centralevent, branchevent FROM college WHERE id=\'" + id + "\'");
			while(rs.next())
			{
				voted[0] = rs.getInt(1) > 0;
				voted[1] = rs.getInt(2) > 0;
			}
			return voted;
		case 2:
			//External
			boolean[] votede = new boolean[3];
			ResultSet rse = statement.executeQuery("SELECT centralevent, branchevent, preference FROM external WHERE id=\'" + id + "\'");
			while(rse.next())
			{
				votede[0] = rse.getInt(1) > 0;
				votede[1] = rse.getInt(2) > 0;
				votede[2] = rse.getInt(3) > 0;
			}
			return votede;
		default:
			throw new Exception("Invalid Category");
		}
	}
	/**
	 * 
	 * @param connection Connection object used to establish connection with a database
	 * @param id The ID of the student casting the vote
	 * @param eventType CentralEvent(1), BranchEvent(2) or Preference(3)
	 * @param eventId The numeric ID of the event being voted
	 * @param category Internal(1) or External(2)
	 * @throws Exception
	 */
	public static void vote(Connection connection, String id, int eventType, int eventId, int category) throws Exception
	{
		boolean[] voted = hasVoted(connection, id, category);
		if(!voted[eventType - 1])
		{
				//System.out.println("UPDATE " + tableMap[category - 1] + " SET " + eventMap[eventType - 1] + " = " + eventId + " WHERE id = \'" + id + "\'");
				PreparedStatement ps = connection.prepareStatement("UPDATE " + tableMap[category - 1] + " SET " + eventMap[eventType - 1] + " = " + eventId + " WHERE id = \'" + id + "\'");
				ps.execute();
		}
	}
	
	public static String getEventName(Connection connection, int category, int eventId, String tableName) throws SQLException
	{
		String name = null;
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT eventname from " + tableName + "WHERE id = " + eventId);
		while(rs.next())
			name = rs.getString(1);
		
		return name;
	}
	
	public static int getEventId(Connection connection, int category, String eventName, String tableName) throws SQLException
	{
		int id = 0;
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT id from " + tableName + " WHERE eventname = \'" + eventName + "\'");
		while(rs.next())
			id = rs.getInt(1);
		
		return id;
	}
	
	public static boolean idExists(Connection connection, String id) throws SQLException
	{
		Statement statement = connection.createStatement();
		String tableName = Utilities.getTableName(id);
		ResultSet rs = statement.executeQuery("SELECT id from " + tableName + " WHERE id = \'" + id + "\'");
		String tmp = null;
		while(rs.next())
		{
			tmp = rs.getString(1);
		}
		if(tmp == null)
			return false;
		else
			return true;
	}

}
