package MVPCalc;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;

public class Gui {

	private JFrame frame;
	private ArrayList<Player> players = new ArrayList<>();
	private JTextField mvpDisplay;
	private ArrayList<JLabel> labels = new ArrayList<>();
	int y = 30;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton addSkater = new JButton("Add Skater");
		addSkater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JTextField nameInput = new JTextField();
				JTextField goalInput = new JTextField();
				Object[] message = {"Name: " , nameInput, "Goals: " , goalInput, "Assists: "};
				String assists = JOptionPane.showInputDialog(message);

				JLabel showName = new JLabel(nameInput.getText());
				labels.add(showName);

				showName.setBounds(337, y, 100, 20);

				if(assists == null) {

				} else {
					y+= 15;
				}
				
				showName.setVisible(true);
				frame.getContentPane().add(showName);
				frame.getContentPane().validate();
				frame.getContentPane().repaint();

				players.add(new Player(nameInput.getText(),Integer.parseInt(goalInput.getText()),Integer.parseInt(assists)));

			}
		});
		addSkater.setBounds(25, 231, 105, 30);
		frame.getContentPane().add(addSkater);

		JButton removePlayer = new JButton("Remove Player");
		removePlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String toRemove = JOptionPane.showInputDialog("Remove Player by Name");
				for(JLabel l : labels) {
					if(l.getText().equalsIgnoreCase(toRemove)) {
						l.setVisible(false);
						players.remove(findPlayerByName(l.getText()));

						for(int i = labels.indexOf(l)+1; i<labels.size(); i++) {
							int y_in = labels.get(i).getY();
							labels.get(i).setBounds(337, y_in-15, 100, 20);
							y-=15;
						}
					}
				}

			}
		});
		removePlayer.setBounds(137, 210, 126, 40);
		frame.getContentPane().add(removePlayer);

		mvpDisplay = new JTextField();
		mvpDisplay.setBounds(146, 54, 117, 20);
		frame.getContentPane().add(mvpDisplay);
		mvpDisplay.setColumns(10);
		mvpDisplay.setEditable(false);

		JLabel lblAndTheMvp = new JLabel("And the MVP is...");
		lblAndTheMvp.setBounds(161, 29, 119, 14);
		frame.getContentPane().add(lblAndTheMvp);

		JButton calculateMvp = new JButton("Calculate MVP");
		calculateMvp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mvpDisplay.setText(getMVP(players).getName());
			}
		});
		calculateMvp.setBounds(13, 44, 117, 73);
		frame.getContentPane().add(calculateMvp);

		JLabel lblRoster = new JLabel("Roster");
		lblRoster.setFont(new Font("Stencil", Font.PLAIN, 13));
		lblRoster.setBounds(329, 11, 61, 20);
		frame.getContentPane().add(lblRoster);

		JButton addGoalie = new JButton("Add Goalie");
		addGoalie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JTextField nameInput = new JTextField();
				JTextField goalInput = new JTextField();
				JTextField assistInput = new JTextField();
				JTextField shotInput = new JTextField();
				Object[] message = {"Name: " , nameInput, "Goals: " , goalInput, "Assists: ", assistInput, "Shots: ", shotInput, "Goals Against: "};
				String ga = JOptionPane.showInputDialog(message);

				JLabel showName = new JLabel("G: " + nameInput.getText());
				labels.add(showName);

				showName.setBounds(337, y, 100, 20);
				
				if(ga == null) {

				} else {
					y+= 15;
				}
				
				showName.setVisible(true);
				frame.getContentPane().add(showName);
				frame.getContentPane().validate();
				frame.getContentPane().repaint();

				players.add(new Goalie(nameInput.getText(),Integer.parseInt(goalInput.getText()),Integer.parseInt(assistInput.getText()), Integer.parseInt(shotInput.getText()), Integer.parseInt(ga)));
			}
		});
		addGoalie.setBounds(25, 198, 105, 30);
		frame.getContentPane().add(addGoalie);

		JLabel label = new JLabel("");
		Image img1 = new ImageIcon(this.getClass().getResource("/hockey.png")).getImage();
		label.setIcon(new ImageIcon(img1));
		label.setBounds(25, 1, 275, 198);
		frame.getContentPane().add(label);

	}

	public Player getMVP(ArrayList<Player> players) {
		double max = 0;
		Player mvp = null;
		for(Player p : players) {
			if(p.getMVPValue() > max) {
				max = p.getMVPValue();
				mvp = p;
			}
		}

		return mvp;
	}

	public Player findPlayerByName(String name) {
		for(Player p : this.players) {
			if(p.getName().equalsIgnoreCase(name)) {
				return p;
			}

		}
		return null;
	}
}
