package Manage;

import java.awt.HeadlessException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class DatabaseManager {
	
	
	public static boolean dbExists;
	Connection conn = null;
	public static String mypath;
	public Connection connection;
	
	public DatabaseManager() {
		connection = dbConnector();
		setUpDataBase();
		
	}
	
	public Connection dbConnector() {
		try {
			Class.forName("org.sqlite.JDBC");
			Path workingDirectory = Paths.get("").toAbsolutePath();
			mypath = workingDirectory + File.separator + "RollerData.sqlite";
			if(new File(mypath).exists()) {
				dbExists = true;
			}
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + mypath);			
			return conn;
				
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
		
	}

	public void executeQuery(String query) {
		String q = query;
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.execute();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQueryWithResults(String query) {
		ResultSet rs = null;
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			rs = ps.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public JComboBox<String> executeQueryWithComboBox(String query) throws SQLException {
		JComboBox<String> j = new JComboBox<String>();
		ResultSet rs = executeQueryWithResults(query);
		while(rs.next()) {
			try {
				j.addItem(rs.getString(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return j;
	}
	
	public ArrayList<String> executeQueryWithArrayListName(String query) {
		ArrayList<String> j = new ArrayList<>();
		ResultSet rs = executeQueryWithResults(query);
		try {
			while(rs.next()) {
				try {
					j.add(rs.getString(1));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return j;
	}
	public ArrayList<Double> executeQueryWithArrayListMoney(String query) {
		ArrayList<Double> j = new ArrayList<Double>();
		ResultSet rs = executeQueryWithResults(query);
		try {
			while(rs.next()) {
				try {
					j.add(Double.parseDouble(rs.getString(3)));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(NullPointerException e) {
					j.add(0.0);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	public ArrayList<Integer> executeQueryWithArrayListAssists(String query) {
		ArrayList<Integer> j = new ArrayList<>();
		ResultSet rs = executeQueryWithResults(query);
		try {
			while(rs.next()) {
				try {
					j.add(Integer.parseInt(rs.getString(4)));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(NullPointerException e) {
					j.add(0);
				 } catch(NumberFormatException e){
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	public ArrayList<Integer> executeQueryWithArrayListGoals(String query) {
		ArrayList<Integer> j = new ArrayList<>();
		ResultSet rs = executeQueryWithResults(query);
		try {
			while(rs.next()) {
				try {
					j.add(Integer.parseInt(rs.getString(5)));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(NullPointerException e) {
					j.add(0);
				} catch(NumberFormatException e){
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	
	public int executeQueryPointStats(String query, String point_type) {
		int generic_point = 0;
		int column_num = 0;
		if(point_type.equalsIgnoreCase("Assist")) {
			column_num = 4;
		} else if(point_type.equalsIgnoreCase("Goal")){
			column_num = 5;
		} else if(point_type.equals("shots")) {
			column_num = 6;
		} else if(point_type.equals("ga")) {
			column_num = 7;
		}
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			generic_point = Integer.parseInt(rs.getString(column_num));
		} catch(NumberFormatException ne) {
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return generic_point;
	}
	
	private void setUpDataBase() {
		if(!dbExists){
			String query = "CREATE TABLE 'PlayerInfo' ('Name' TEXT, 'Number' INTEGER, 'Money_Owed' DOUBLE, 'Assists' INTEGER, 'Goals' INTEGER, 'Shots' INTEGER, 'Goals_Against' INTEGER, 'isGoalie', INTEGER)";
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.execute();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		} else {

		}

	}
	
	
	public void updatePlayer(String name,int number, double money, int goals, int assists) {
		this.executeQuery("update PlayerInfo set Goals = '"+goals+"' "
				+ ",Assists = '"+assists+"'"
				+ ",Money_Owed = '"+money+"' "
				+ ",Number = '"+number+"' "
				+ "WHERE Name = '"+name+"'");
	}
	
	public void updateGoalie(String name,int number, double money, int goals, int assists, int shots, int ga) {
		this.executeQuery("update PlayerInfo set Goals = '"+goals+"' "
				+ ",Assists = '"+assists+"'"
				+ ",Shots = '"+shots+"'"
				+ ",Goals_Against = '"+ga+"'"
				+ ",Money_Owed = '"+money+"' "
				+ ",Number = '"+number+"' "
				+ "WHERE Name = '"+name+"'");
										
	}
	public double getOriginalMoney(String name) {
	String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
	double money = 0;
		ResultSet rs = this.executeQueryWithResults(query);
		try {
			money = Double.parseDouble(rs.getString(3));
		} catch (NumberFormatException e) {
			return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return money;
	}
	
	public void updateMoney(String name, double money) {
		this.executeQuery("UPDATE PlayerInfo set Money_Owed = '"+money+"' WHERE Name = '"+name+"'");
	}
	
	public boolean nameCheck(String name) {
		String query = "SELECT Name FROM PlayerInfo";
		ResultSet rs = this.executeQueryWithResults(query);
		try {
			while(rs.next()) {
				if(rs.getString(1).equalsIgnoreCase(name)) {
					return false;
			}
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			return true;
		}
		return true;
		
	}
	
	public int getTotalSaves() {
		ArrayList<Integer> shots = new ArrayList<>();
		ArrayList<Integer> ga = new ArrayList<>();
		int totShots = 0;
		int totGA = 0;
		ResultSet rs = this.executeQueryWithResults("SELECT * FROM PlayerInfo");
		try {
			while(rs.next()) {
				shots.add(rs.getInt(6));
				ga.add(rs.getInt(7));
			}
			for(int shot : shots) {
				totShots += shot;
				totGA += ga.get(shots.indexOf(shot));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totShots - totGA;
	}
	
	public int getTotalGoals() {
		ArrayList<Integer> goals = new ArrayList<>();
		int totGoals = 0;
		ResultSet rs = this.executeQueryWithResults("SELECT * FROM PlayerInfo");
		try {
			while(rs.next()) {
				goals.add(rs.getInt(5));
			}
			for(int goal : goals) {
				totGoals += goal;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totGoals;
	}
	
	public int getTotalAssists() {
		ArrayList<Integer> assists = new ArrayList<>();
		int totAssists = 0;
		ResultSet rs = this.executeQueryWithResults("SELECT * FROM PlayerInfo");
		try {
			while(rs.next()) {
				assists.add(rs.getInt(4));
			}
			for(int assist : assists) {
				totAssists += assist;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totAssists;
	}
	
	public void print(String string) {
		System.out.println(string);
	}

}