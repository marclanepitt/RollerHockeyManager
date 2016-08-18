package Manage;

import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.MenuBar;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Font;
import java.awt.Image;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import java.awt.ScrollPane;

public class Manage {

	/*TO DO
	1)focus joption panes on first option
	*/

	private JFrame frame;
	private DatabaseManager db_manager;
	private int games_played;
	ChartPanel chart_panel;
	private boolean chart_created;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Manage window = new Manage();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JTextField mvpDisplay;
	ArrayList<String> names = new ArrayList<>();
	ArrayList<Double> money_owed = new ArrayList<>();
	ArrayList<Integer> goals = new ArrayList<>();
	ArrayList<Integer> assists = new ArrayList<>();
	ActionListener addPlayer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JTextField nameInput = new JTextField();
			JTextField numberInput = new JTextField();
			Object[] message = {"Name: " , nameInput, "Number: " , numberInput, "Money Paid: "};
			String moneyInput = JOptionPane.showInputDialog(message);
			boolean isNotRepeat = db_manager.nameCheck(nameInput.getText());
			if(isNotRepeat) {
				addPlayerToTable(nameInput.getText(),numberInput.getText(),moneyInput);
				db_manager.executeQuery("update PlayerInfo set isGoalie = 2 WHERE Name = '"+nameInput.getText()+"'");
				calcAndShowMVP();
			} else {
				JOptionPane.showMessageDialog(null, "Name already used!");
			}

		}
	};
	
	ActionListener addGoalie = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JTextField nameInput = new JTextField();
			JTextField numberInput = new JTextField();
			Object[] message = {"Name: " , nameInput, "Number: " , numberInput, "Money Paid: "};
			String moneyInput = JOptionPane.showInputDialog(message);
			boolean isNotRepeat = db_manager.nameCheck(nameInput.getText());
			if(isNotRepeat) {
				addPlayerToTable(nameInput.getText(),numberInput.getText(),moneyInput);
				db_manager.executeQuery("update PlayerInfo set isGoalie = 1 WHERE Name = '"+nameInput.getText()+"'");
				calcAndShowMVP();
			} else {
				JOptionPane.showMessageDialog(null, "Name already used!");
			}
		}
	};
	
	ActionListener removePlayer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String toRemove = null;
			JComboBox<String> j =  getAllJComboBox();
			Object[] message = {"Select Player to Remove", j};
			int isYes = JOptionPane.showConfirmDialog(null,message);
			if(isYes == 0) {
				toRemove = (String)j.getSelectedItem();
			}
			String query = "DELETE FROM PlayerInfo WHERE UPPER(Name) LIKE '"+toRemove+"' ";
			db_manager.executeQuery(query);
			calcAndShowMVP();
		}
	};


	public Manage() {
		initialize();
		db_manager = new DatabaseManager();
	}


	private void initialize() {
		frame =  new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		
		JButton showGraph = new JButton("Show Graph");
		showGraph.setForeground(Color.WHITE);
		showGraph.setBackground(Color.GRAY);
		showGraph.setBounds(260, 27, 150, 70);
		frame.add(showGraph);
		showGraph.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					chart_created = true;
					showGraph.setVisible(false);
					int totSaves = db_manager.getTotalSaves();
					int totGoals = db_manager.getTotalGoals();
					int totAssists = db_manager.getTotalAssists();
					DefaultPieDataset dataset = new DefaultPieDataset();
					dataset.setValue("Saves", totSaves);
					dataset.setValue("Goals", totGoals);
					dataset.setValue("Assists", totAssists);
					JFreeChart chart = ChartFactory.createPieChart3D("Team Statistics", dataset, true, true, true);
					chart_panel = new ChartPanel(chart);
					chart_panel.setVisible(true);
					chart_panel.setBounds(222, 0, 220, 125);
					chart_panel.setLayout(null);
					JButton back = new JButton("Hide");
					back.setFont(new Font("Arial", Font.PLAIN, 8));
					back.setBounds(169,110,50,15);
					back.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							chart_panel.setVisible(false);
							showGraph.setVisible(true);
						}
					});
					chart_panel.add(back);
					frame.getContentPane().add(chart_panel);
					frame.repaint();
					} catch (NullPointerException e) {
						JOptionPane.showMessageDialog(null, "Add Data to show graph!");
					}
			}
		});
		
		
		JPanel teamMVP = new JPanel();
		teamMVP.setBackground(Color.DARK_GRAY);
		teamMVP.setForeground(Color.WHITE);
		teamMVP.setBounds(0, 0, 220, 129);
		frame.getContentPane().add(teamMVP);
		teamMVP.setLayout(null);

		JLabel lblTeamMvp = new JLabel("TEAM MVP");
		lblTeamMvp.setBackground(Color.WHITE);
		lblTeamMvp.setForeground(Color.WHITE);
		lblTeamMvp.setBounds(51, 11, 126, 31);
		lblTeamMvp.setFont(new Font("Stencil", Font.PLAIN, 24));
		teamMVP.add(lblTeamMvp);

		mvpDisplay = new JTextField();
		mvpDisplay.setEditable(false);
		mvpDisplay.setBounds(51, 55, 120, 20);
		teamMVP.add(mvpDisplay);
		mvpDisplay.setColumns(10);
		calcAndShowMVP();

		JPanel moneyStats = new JPanel();
		moneyStats.setBackground(Color.LIGHT_GRAY);
		moneyStats.setForeground(Color.WHITE);
		moneyStats.setBounds(220, 129, 241, 129);
		frame.getContentPane().add(moneyStats);
		moneyStats.setLayout(null);


		moneyStats.validate();
		moneyStats.repaint();
		
		JButton btnAddPlayer = new JButton("Add Skater");
		btnAddPlayer.setBackground(Color.LIGHT_GRAY);
		btnAddPlayer.setBounds(10, 150, 99, 23);
		frame.getContentPane().add(btnAddPlayer);
		btnAddPlayer.addActionListener(addPlayer);
		
		JButton btnAddGoalie = new JButton("Add Goalie");
		btnAddGoalie.setBackground(Color.LIGHT_GRAY);
		btnAddGoalie.setBounds(103, 150, 99, 23);
		frame.getContentPane().add(btnAddGoalie);
		btnAddGoalie.addActionListener(addGoalie);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setBackground(Color.LIGHT_GRAY);
		btnRemove.setBounds(60, 184, 89, 23);
		frame.getContentPane().add(btnRemove);
		btnRemove.addActionListener(removePlayer);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JButton moneyManage = new JButton("Manage Money");
		moneyManage.setForeground(Color.WHITE);
		moneyManage.setBackground(Color.BLUE);
		moneyManage.setBounds(40, 21, 150, 70);
		moneyStats.add(moneyManage);
		
		moneyManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuBar.setVisible(false);
				JLayeredPane game_screen = new JLayeredPane();
				game_screen.setBounds(0, 0, 450, 300);
				game_screen.setBackground(new Color(1,1,1));
				game_screen.setLayout(null);
				game_screen.setVisible(true);
				game_screen.setBackground(new Color(1,5,1));
				game_screen.setPosition(game_screen, 0);
				frame.getContentPane().add(game_screen);
				teamMVP.setVisible(false);
				moneyStats.setVisible(false);
				btnAddPlayer.setVisible(!true);
				btnAddGoalie.setVisible(!true);
				btnRemove.setVisible(!true);
				showGraph.setVisible(false);
				if(chart_created) {
				chart_panel.setVisible(false);
				
				}
				frame.repaint();

				int m_y =10;
				int totMoney = 0;
				names = getNameList();
				money_owed = getMoneyList();

				for(String name:names) {
					JLabel temp = new JLabel();
					double money = money_owed.get(names.indexOf(name));
					if(money>=94) {
						temp = new JLabel(name+"- $" + money);
						temp.setForeground(Color.GREEN);
					} else {
						temp = new JLabel(name+"- $" + money);
						temp.setForeground(Color.RED);

					}
					temp.setBounds(20,m_y,200,15);
					game_screen.add(temp);
					m_y+=15;

					totMoney+=money;

				}

				JTextField totMoneyDisplay = new JTextField();
				totMoneyDisplay.setBounds(340, 30, 70, 70);
				totMoneyDisplay.setEditable(false);
				game_screen.add(totMoneyDisplay);
				totMoneyDisplay.setText("$"+ totMoney);
				totMoneyDisplay.setHorizontalAlignment(totMoneyDisplay.CENTER);

				JLabel totMoneylbl = new JLabel("Total Money");
				totMoneylbl.setBounds(340,15,70,15);
				game_screen.add(totMoneylbl);
				
				JTextField leftMoneyDisplay = new JTextField();
				leftMoneyDisplay.setBounds(330, 130, 90, 40);
				leftMoneyDisplay.setEditable(false);
				game_screen.add(leftMoneyDisplay);
				leftMoneyDisplay.setText("$"+ (1500-totMoney));
				leftMoneyDisplay.setHorizontalAlignment(leftMoneyDisplay.CENTER);

				JLabel leftMoneylbl = new JLabel("Amount Needed");
				leftMoneylbl.setBounds(330,110,100,15);
				game_screen.add(leftMoneylbl);
				

				JButton backButton = new JButton("Exit");
				backButton.setBounds(330, 180, 90, 50);
				game_screen.add(backButton);
				backButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						frame.remove(game_screen);
						teamMVP.setVisible(!false);
						moneyStats.setVisible(!false);
						btnAddPlayer.setVisible(true);
						btnAddGoalie.setVisible(true);
						btnRemove.setVisible(true);
						showGraph.setVisible(true);
						calcAndShowMVP();
						frame.validate();
						frame.repaint();
						menuBar.setVisible(true);
						if(chart_created) {
						chart_panel.setVisible(true);
						if(showGraph.isVisible()) {
						chart_panel.setVisible(false);
						}
						}
					}

				});
			}
		});

		JMenu file = new JMenu("File");
		menuBar.add(file);
		
		JMenu stats = new JMenu("Stats");
		menuBar.add(stats);
		
		JMenuItem checkStats = new JMenuItem("Individual Stats");
		checkStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuBar.setVisible(false);
				JLayeredPane game_screen = new JLayeredPane();
				game_screen.setBounds(0, 0, 450, 300);
				game_screen.setBackground(new Color(1,1,1));
				game_screen.setLayout(null);
				game_screen.setVisible(true);
				game_screen.setBackground(new Color(1,5,1));
				game_screen.setPosition(game_screen, 0);
				frame.getContentPane().add(game_screen);
				teamMVP.setVisible(false);
				moneyStats.setVisible(false);
				btnAddPlayer.setVisible(!true);
				btnAddGoalie.setVisible(!true);
				btnRemove.setVisible(!true);
				showGraph.setVisible(false);
				if(chart_created) {
				chart_panel.setVisible(false);
				
				}
				
				frame.repaint();
				JButton exit = new JButton("Exit");
				exit.setBounds(360,200,60,40);
				game_screen.add(exit);
				exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						frame.remove(game_screen);
						teamMVP.setVisible(!false);
						moneyStats.setVisible(!false);
						calcAndShowMVP();
						menuBar.setVisible(true);
						btnAddPlayer.setVisible(true);
						btnAddGoalie.setVisible(true);
						btnRemove.setVisible(true);
						showGraph.setVisible(true);
						if(chart_created) {
						chart_panel.setVisible(true);
						if(showGraph.isVisible()){
							chart_panel.setVisible(false);
						}
						}
					}

				});

				int m_y =10;
				int m_x = 20;
				int totMoney = 0;
				names = getNameList();
				goals = getGoalList();
				assists = getAssistList();

				for(String name:names) {
					JLabel temp1 = new JLabel();
					int goals1 = goals.get(names.indexOf(name));
					int assists1 = assists.get(names.indexOf(name));
					
					temp1 = new JLabel(name+ " G: " + goals1 + "  A: " + assists1);
					temp1.setForeground(Color.BLUE);
					temp1.setFont(new Font("Times new roman", 15, 13));
					temp1.setBounds(m_x,m_y,300,25);
					game_screen.add(temp1);
					m_y+=14;
					
					if(m_y > 300) {
						m_y = 10;
						m_x = 60;
					}
					

				}
				

			}
			
			
		});
		stats.add(checkStats);

		JMenuItem mntmAddSkater = new JMenuItem("Add Skater");
		mntmAddSkater.addActionListener(addPlayer);
		file.add(mntmAddSkater);

		JMenuItem mntmAddGoalie = new JMenuItem("Add Goalie");
		file.add(mntmAddGoalie);
		mntmAddGoalie.addActionListener(addGoalie);
		file.addSeparator();

		JMenuItem mntmRemoveByName = new JMenuItem("Custom Remove");
		mntmRemoveByName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String toRemove = JOptionPane.showInputDialog("Remove Player");
				ArrayList<String> names = getNameList();
				boolean isValid = false;
				for(String name:names) {
					if(toRemove.equalsIgnoreCase(name)) {
						isValid = true;
					}
				}
				if(isValid) {
				String query = "DELETE FROM PlayerInfo WHERE UPPER(Name) LIKE '"+toRemove+"' ";
				db_manager.executeQuery(query);
				calcAndShowMVP();
				} else {
					JOptionPane.showMessageDialog(null, "Name not Found!");
				}
			}
		});
		file.add(mntmRemoveByName);

		JMenuItem mntmRemoveByOptions = new JMenuItem("Remove by Options");
		file.add(mntmRemoveByOptions);
		mntmRemoveByOptions.addActionListener(removePlayer);

		file.addSeparator();
		JMenu editPlayer = new JMenu("Edit Player");
		editPlayer.setMnemonic(KeyEvent.VK_S);

		JMenuItem menuItem = new JMenuItem("Select Skater");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<String> j = new JComboBox<>();
				try {
					j = getPlayerJComboBox();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
				Object[] message = {"Select Players",j};
				int isYes = JOptionPane.showConfirmDialog(null, message);
				if(isYes == 0) {

					ResultSet rs = getPlayersRow((String)j.getSelectedItem());

					JTextField nameInput = null;
					JTextField numberInput = null;
					JTextField moneyInput = null;
					JTextField goalsInput = null;
					JTextField assistsInput = null;
					try {
						nameInput = new JTextField(rs.getString(1));
						nameInput.setEditable(false);
						numberInput = new JTextField(rs.getString(2));
						moneyInput = new JTextField(rs.getString(3));
						goalsInput = new JTextField(rs.getString(5));
						assistsInput = new JTextField(rs.getString(4));
					} catch (SQLException e) {
						e.printStackTrace();
					}

					Object[] message1 = {"Name" ,nameInput, "Number", numberInput, "Money Paid",moneyInput, "Goals" ,goalsInput, "Assists", assistsInput};
					int isYes1 = JOptionPane.showConfirmDialog(null, message1);
					if(isYes1 == 0) {
						updatePlayer(nameInput.getText(),Integer.parseInt(numberInput.getText()),Double.parseDouble(moneyInput.getText()),
								Integer.parseInt(goalsInput.getText()), Integer.parseInt(assistsInput.getText()));
					}
				}
				calcAndShowMVP();
			}});

		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_2, ActionEvent.ALT_MASK));
		editPlayer.add(menuItem);

		JMenuItem goalieItem = new JMenuItem("Select Goalie");
		goalieItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<String> j = new JComboBox<>();
				try {
					j = getGoalieJComboBox();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Object[] message = {"Select Goaltenders",j};
				int isYes = JOptionPane.showConfirmDialog(null, message);
				if(isYes == 0) {

					ResultSet rs = getPlayersRow((String)j.getSelectedItem());

					JTextField nameInput = null;
					JTextField numberInput = null;
					JTextField moneyInput = null;
					JTextField goalsInput = null;
					JTextField assistsInput = null;
					JTextField shotsInput = null;
					JTextField gaInput = null;
					try {
						nameInput = new JTextField(rs.getString(1));
						nameInput.setEditable(false);
						numberInput = new JTextField(rs.getString(2));
						moneyInput = new JTextField(rs.getString(3));
						goalsInput = new JTextField(rs.getString(5));
						assistsInput = new JTextField(rs.getString(4));
						shotsInput = new JTextField(rs.getString(6));
						gaInput = new JTextField(rs.getString(7));

					} catch (SQLException e) {
						e.printStackTrace();
					}

					Object[] message1 = {"Name" ,nameInput, "Number", numberInput, "Money Paid",moneyInput, "Goals" ,goalsInput, "Assists", assistsInput, "Shots Faced",shotsInput, "Goals Against",gaInput};
					int isYes1 = JOptionPane.showConfirmDialog(null, message1);
					if(isYes1 == 0) {
						try {
						updateGoalie(nameInput.getText(),Integer.parseInt(numberInput.getText()),Double.parseDouble(moneyInput.getText()),
								Integer.parseInt(goalsInput.getText()), Integer.parseInt(assistsInput.getText()), Integer.parseInt(shotsInput.getText()),Integer.parseInt(gaInput.getText()));
						} catch(NumberFormatException e) {
							db_manager.executeQuery("UPDATE PlayerInfo set Goals_Against = 0,"
									+ "Shots = 0 WHERE Name = '"+nameInput.getText()+"'");
							updatePlayer(nameInput.getText(),Integer.parseInt(numberInput.getText()),Double.parseDouble(moneyInput.getText()),
								Integer.parseInt(goalsInput.getText()), Integer.parseInt(assistsInput.getText()));
						}
					}
				}
				calcAndShowMVP();
			}
		});
		goalieItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.ALT_MASK));
		editPlayer.add(goalieItem);

		file.add(editPlayer);

		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);

		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mntmNewGame.addActionListener(new ActionListener() {
			String name;
			int number;
			double money;
			int assists;
			int lbl_y = 40;
			int lbl_y1 = 40;
			int lbl_y2 = 40;

			public void actionPerformed(ActionEvent arg0) {
				menuBar.setVisible(false);
				JLayeredPane game_screen = new JLayeredPane();
				game_screen.setBounds(0, 0, 450, 300);
				game_screen.setBackground(new Color(1,1,1));
				game_screen.setLayout(null);
				game_screen.setVisible(true);
				game_screen.setBackground(new Color(1,5,1));
				game_screen.setPosition(game_screen, 0);
				frame.getContentPane().add(game_screen);
				teamMVP.setVisible(false);
				moneyStats.setVisible(false);
				btnAddPlayer.setVisible(!true);
				btnAddGoalie.setVisible(!true);
				btnRemove.setVisible(!true);
				showGraph.setVisible(false);
				if(chart_created) {
				chart_panel.setVisible(false);
				}
				

				JButton addGoal = new JButton("Add Goal");
				addGoal.setBounds(20, 20, 100, 20);
				game_screen.add(addGoal);
				addGoal.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JComboBox<String> j = new JComboBox<String>();
						try {
							j = getPlayerJComboBox();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						Object[] message = {"Select Player", j , "Number of Goals"};
						int goals = Integer.parseInt(JOptionPane.showInputDialog(null,message));
						if(goals!=0) {
							JLabel templbl = new JLabel(j.getSelectedItem() +" "+ goals);
							templbl.setBounds(20,lbl_y,100,20);
							templbl.setHorizontalAlignment(templbl.CENTER);
							game_screen.add(templbl);
							lbl_y+=20;
						}
						

						int totGoals = getPlayersGoals((String)j.getSelectedItem())+goals;

						String query = "update PlayerInfo set Goals = '"+totGoals+"' WHERE Name = '"+(String)j.getSelectedItem()+"'";
						db_manager.executeQuery(query);

					}

				});

				JButton addAssist = new JButton("Add Assist");
				addAssist.setBounds(120, 20, 100, 20);
				game_screen.add(addAssist);
				addAssist.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JComboBox<String> j = new JComboBox<String>();
						try {
							j = getPlayerJComboBox();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						Object[] message = {"Select Player", j , "Number of Assists"};
						int assists = Integer.parseInt(JOptionPane.showInputDialog(null,message));

						if(assists!=0) {
							JLabel templbl = new JLabel(j.getSelectedItem() +" "+ assists);
							templbl.setHorizontalAlignment(templbl.CENTER);
							templbl.setBounds(120,lbl_y1,100,20);
							game_screen.add(templbl);
							lbl_y1+=20;
						}
						
						int totAssists = getPlayersAssists((String)j.getSelectedItem())+assists;

						String query = "update PlayerInfo set Assists = '"+totAssists+"' WHERE Name = '"+(String)j.getSelectedItem()+"'";
						db_manager.executeQuery(query);
					}
				});

				JButton addShotsGA = new JButton("Add Shots and Goals Against");
				addShotsGA.setBounds(220,20,200,20);
				game_screen.add(addShotsGA);
				addShotsGA.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							JComboBox<String> goalies = getGoalieJComboBox();
							JTextField shots = new JTextField();
							Object[] message = {"Select Starter", goalies, "Shots Faced",shots,"Goals Against"};
							int goals_against = Integer.parseInt(JOptionPane.showInputDialog(null,message));
							int shotsFaced = Integer.parseInt(shots.getText());
							int totShots = getGoaliesShots((String)goalies.getSelectedItem())+Integer.parseInt(shots.getText());
							int totGA = getGoaliesGA((String)goalies.getSelectedItem())+goals_against;
							String query = "UPDATE PlayerInfo set Shots = '"+totShots+"' WHERE Name = '"+(String)goalies.getSelectedItem()+"'";
							String query1 = "UPDATE PlayerInfo set Goals_Against = '"+totGA+"' WHERE Name = '"+(String)goalies.getSelectedItem()+"'";
							db_manager.executeQuery(query);
							db_manager.executeQuery(query1);
							if(goals_against!=0 && shotsFaced !=0) {
								JLabel templbl = new JLabel(goalies.getSelectedItem() +" "+ (shotsFaced-goals_against)+" Saves");
								templbl.setHorizontalAlignment(templbl.CENTER);
								templbl.setBounds(260,lbl_y2,100,20);
								game_screen.add(templbl);
								lbl_y2+=20;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});

				JButton backButton = new JButton("Exit/Save");
				backButton.setBounds(330, 180, 90, 50);
				game_screen.add(backButton);
				backButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						games_played++;
						frame.remove(game_screen);
						teamMVP.setVisible(!false);
						moneyStats.setVisible(!false);
						calcAndShowMVP();
						menuBar.setVisible(true);
						btnAddPlayer.setVisible(true);
						btnAddGoalie.setVisible(true);
						btnRemove.setVisible(true);
						showGraph.setVisible(true);
						if(chart_created) {
						chart_panel.setVisible(true);
						if(showGraph.isVisible()){
							chart_panel.setVisible(false);
						}
						}
					}

				});

				game_screen.validate();
				game_screen.repaint();
				frame.repaint();

			}
		});
		mnGame.add(mntmNewGame);

		JMenu moneyMenu = new JMenu("Money");
		menuBar.add(moneyMenu);

		JMenuItem newPayment = new JMenuItem("New Payment");
		moneyMenu.add(newPayment);
		newPayment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<String> j = getAllJComboBox();
				Object[] message = {"Select player",j,"Amount Paid"};
				double money = Double .parseDouble(JOptionPane.showInputDialog(null,message));
				db_manager.updateMoney((String)j.getSelectedItem(), money+db_manager.getOriginalMoney((String)j.getSelectedItem()));
			}
		});
	}

	private void calcAndShowMVP() {
		String mvp = null;
		ResultSet rs = null;
		try {
			rs = getResultSet();

			ArrayList<String> players = new ArrayList<>();
			ArrayList<Integer> goals = new ArrayList<>();
			ArrayList<Integer> assists = new ArrayList<>();
			ArrayList<Integer> saves = new ArrayList<>();
			ArrayList<Integer> isGoalie = new ArrayList<>();

			try {
				while(rs.next()) {
					players.add(rs.getString(1));
					goals.add(rs.getInt(5));
					assists.add(rs.getInt(4));
					saves.add(rs.getInt(6) - rs.getInt(7));
					isGoalie.add(rs.getInt(8));
					}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(players.isEmpty()) {
				mvp = "Add Players for MVP";
			} else {
				int max = 0;
				for(String player:players){
					int index = players.indexOf(player);
					int points = 0;
					if(isGoalie.get(index) == 2) {
					points = goals.get(index) + assists.get(index);
					} else {
					points = saves.get(index) / 10;
					}
					if(points > max) {
						max = points;
						mvp = player;
					}
				}
			} 
		}catch(NullPointerException e) {
			mvp = "Add Players for MVP";
		}
		
		if(mvp == null) {
			mvp = "Add Players for MVP";
		}

		mvpDisplay.setText(mvp+ "!");
		mvpDisplay.setHorizontalAlignment(mvpDisplay.CENTER);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public void refresh() {

	}


	public void addPlayerToTable(String name, String number, String money) {
		String query = "INSERT INTO PlayerInfo(Name, Number, Money_Owed,Goals,Assists) VALUES ('" + name+ "','" + Integer.parseInt(number)+ "','" + Double.parseDouble(money)+"' ,'"+0+"','"+0+"')";
		db_manager.executeQuery(query);
	}

	public ArrayList<String> getNameList() {
		String query = "SELECT * FROM PlayerInfo";
		return db_manager.executeQueryWithArrayListName(query);
	}

	public ArrayList<Double> getMoneyList() {
		String query = "SELECT * FROM PlayerInfo";
		return db_manager.executeQueryWithArrayListMoney(query);
	}
	public ArrayList<Integer> getAssistList() {
		String query = "SELECT * FROM PlayerInfo";
		return db_manager.executeQueryWithArrayListAssists(query);
	}
	public ArrayList<Integer> getGoalList() {
		String query = "SELECT * FROM PlayerInfo";
		return db_manager.executeQueryWithArrayListGoals(query);
	}

	public ResultSet getResultSet() {
		String query = "SELECT * FROM PlayerInfo";
		return db_manager.executeQueryWithResults(query);
	}

	public JComboBox<String> getAllJComboBox() {
		String query = "SELECT * FROM PlayerInfo";
		try {
			return db_manager.executeQueryWithComboBox(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JComboBox<String> getPlayerJComboBox() throws SQLException {
		String query = "SELECT * FROM PlayerInfo WHERE isGoalie = 2";
		return db_manager.executeQueryWithComboBox(query);
	}

	public JComboBox<String> getGoalieJComboBox() throws SQLException {
		String query = "SELECT * FROM PlayerInfo WHERE isGoalie = 1";
		return db_manager.executeQueryWithComboBox(query);
	}

	public ResultSet getPlayersRow(String name) {
		String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
		return db_manager.executeQueryWithResults(query);
	}

	public int getPlayersGoals(String name) {
		String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
		return db_manager.executeQueryPointStats(query, "goal");
	}

	public int getPlayersAssists(String name) {
		String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
		return db_manager.executeQueryPointStats(query, "assist");
	}
	public int getGoaliesShots(String name) {
		String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
		return db_manager.executeQueryPointStats(query,"shots");
	}
	public int getGoaliesGA(String name) {
		String query = "SELECT * FROM PlayerInfo WHERE Name = '"+name+"'";
		return db_manager.executeQueryPointStats(query,"ga");
	}
	public void updatePlayer(String name,int number, double money, int goals, int assists) {
		db_manager.updatePlayer(name, number, money, goals, assists);
	}
	public void updateGoalie(String name,int number, double money, int goals, int assists, int shots, int ga) {
		db_manager.updateGoalie(name, number, money, goals, assists,shots,ga);
	}
	
	public void print(String string) {
		System.out.println(string);
	}
}

