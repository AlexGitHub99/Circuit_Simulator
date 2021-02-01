import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Main implements MouseListener, KeyListener, ActionListener {
	
	private static final int UP = 0;
	private static final int RIGHT = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	private static final int MENU_MIDDLE_X = WIDTH/2;
	private static final int MENU_HEIGHT = 200;
	private static final int INFO_WIDTH = 200;
	private static final int VARIABLE_HEIGHT = 50;
	private static final int GRID_WIDTH = 100;
	private static final int GRID_HEIGHT = 100;
	private static final int ZOOM = 10;
	JFrame frame = new JFrame("Circuit Board");
	Screen screen = new Screen(WIDTH, HEIGHT, MENU_MIDDLE_X, MENU_HEIGHT);
	Container info = new Container();
	Container variableEntry = new Container();
	JTextField textField = new JTextField();
	JLabel variable = new JLabel();
	JButton setValueButton = new JButton();
	CircuitElement selected;
	CircuitElement editing;
	CircuitElement[][] grid = new CircuitElement[GRID_WIDTH][GRID_HEIGHT]; //will store wires and circuit components
	
	public Main() {
		frame.setLayout(new BorderLayout());
		frame.addKeyListener(this);
		screen.addMouseListener(this);
		screen.setZoom(ZOOM);
		info.setLayout(new GridLayout(2, 1));
		info.setPreferredSize(new Dimension(INFO_WIDTH, VARIABLE_HEIGHT));
		variableEntry.setLayout(null);
		textField.setBounds(INFO_WIDTH/2, 0, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		variable.setBounds(0, 0, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		setValueButton.setBounds(INFO_WIDTH/2, VARIABLE_HEIGHT/2, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		variableEntry.add(textField);
		variableEntry.add(variable);
		variableEntry.add(setValueButton);
		info.add(variableEntry);
		frame.add(screen, BorderLayout.CENTER);
		frame.add(info, BorderLayout.EAST);
		variable.setText("Test");
		setValueButton.setText("Set Value");
		setValueButton.addActionListener(this);
		variableEntry.setVisible(false);
		frame.setSize(WIDTH + 50 + INFO_WIDTH, HEIGHT + MENU_HEIGHT + 50);
		frame.setVisible(true);
		
		screen.setGrid(grid);
		screen.repaint();
//		run();
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	public void run() {
		while(true) {
		}
	}
	
	public void updateCircuits() {
		ArrayList<int[]> batteries = findBatteries();
		for(int i = 0; i < batteries.size(); i++) {
			updateBattery(batteries.get(i)[0], batteries.get(i)[1]);
		}
	}
	
	public ArrayList<int[]> findBatteries() {
		ArrayList<int[]> batteries = new ArrayList<int[]>();
		for(int i = 0; i < GRID_WIDTH; i++ ) {
			for(int j = 0; j < GRID_HEIGHT; j++ ) {
				if(grid[i][j] != null) {
					if(grid[i][j].getType() == "battery") {
						int[] pos = {i, j};
						batteries.add(pos);
					}
				}
			}
		}
		return batteries;
	}
	
	public void updateBattery(int x, int y) {
		if(grid[x][y].getType() != "battery") {
			return;
		}
		Battery battery = (Battery)grid[x][y];
		if(battery.getRotation() == UP || battery.getRotation() == DOWN) {
			updateAmmeters(x, y, x, y, UP, DOWN, battery.getVoltage());
		} else { //rotation is RIGHT or LEFT
			updateAmmeters(x, y, x, y, RIGHT, LEFT, battery.getVoltage());
		}
	}
	
	public void updateAmmeters(int startX, int startY, int endX, int endY, int startDirection, int endDirection, double voltage) {
		double current = -1;
		double resistance = calcResistance(startX, startY, endX, endY, startDirection, endDirection);
		if(resistance == -1) {
			return;
		}
		current = voltage/resistance;
		if(grid[startX][startY].getType() == "ammeter") {
			((Ammeter)grid[startX][startY]).setCurrent(current);
		}
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		while(true) {
			if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
				break;
			}
			
			if(grid[xy[0]][xy[1]].getType() == "ammeter") {
				((Ammeter)grid[xy[0]][xy[1]]).setCurrent(current);
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			if(grid[xy[0]][xy[1]].getType() == "wire") {
				if(((Wire)grid[xy[0]][xy[1]]).getShape() == "T") { //found junction
					int[] junctionExits = getTExits(xy[0], xy[1], reverseDir(direction));
					
					int[] endJunction = findJunction(endX, endY, endDirection);
					if(endJunction == null) {
						return;
					}
					
					int[] endJunctionExits = getTExits(endJunction[0], endJunction[1], endJunction[2]);
					if(endJunctionExits == null) {//open exits
						return;
					}
					double firstSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0], endJunctionExits[1]);
					double secondSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1], endJunctionExits[0]);
					if(firstSec == -1 || secondSec == -1) {
						return;
					}
					resistance = 1/(1/firstSec + 1/secondSec);
					voltage = current*resistance;
					updateAmmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0], endJunctionExits[1], voltage);
					updateAmmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1], endJunctionExits[0], voltage);
					xy = grid[endJunction[0]][endJunction[1]].getConnections()[endJunction[2]];
					if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
						break;
					}
					connections = grid[xy[0]][xy[1]].getConnections();
					direction = endJunction[2];
				}
			}
			
			for(int i = 0; i < 4; i++) {
				if(connections[i] != null) {
					if(i != reverseDir(direction)) {
						xy = connections[i];
						direction = i;
						break;
					} 
				}
				if(i == 3) { //no where left to go
					return;
				}
			}
		}
	}
	
	public double calcResistance(int startX, int startY, int endX, int endY, int startDirection, int endDirection) {
		double resistance = 0;
		
		if(grid[startX][startY] == null || grid[endX][endY] == null) {
			return -1;
		}
		if(grid[startX][startY].getConnections()[startDirection] == null) {
			return -1;
		}
		if(grid[endX][endY].getConnections()[endDirection] == null) {
			return -1;
		}
		
		
		if(grid[startX][startY].getType() == "resistor") {
			resistance += ((Resistor)grid[startX][startY]).getResistance();
		}
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		while(true) {
			if(grid[xy[0]][xy[1]].getType() == "resistor") { //add resistance
				resistance += ((Resistor)grid[xy[0]][xy[1]]).getResistance();
			}
			
			if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
				break;
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			if(grid[xy[0]][xy[1]].getType() == "wire") {
				if(((Wire)grid[xy[0]][xy[1]]).getShape() == "T") { //found junction
					int[] junctionExits = getTExits(xy[0], xy[1], reverseDir(direction));
					
					int[] endJunction = findJunction(endX, endY, endDirection);
					if(endJunction == null) {
						return -1;
					}
					
					int[] endJunctionExits = getTExits(endJunction[0], endJunction[1], endJunction[2]);
					if(endJunctionExits == null) { //open exits
						return -1;
					}
					double firstSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0], endJunctionExits[1]);
					double secondSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1], endJunctionExits[0]);
					if(firstSec == -1 || secondSec == -1) {
						return -1;
					}
					resistance += 1/(1/firstSec + 1/secondSec);
					xy = grid[endJunction[0]][endJunction[1]].getConnections()[endJunction[2]];
					if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
						break;
					}
					connections = grid[xy[0]][xy[1]].getConnections();
					direction = endJunction[2];
				}
			}
			
			for(int i = 0; i < 4; i++) {
				if(connections[i] != null) {
					if(i != reverseDir(direction)) {
						xy = connections[i];
						direction = i;
						break;
					} 
				}
				if(i == 3) { //no where left to go
					return -1;
				}
			}
		}
		
		return resistance;
	}
	
	public int[] findJunction(int startX, int startY, int startDirection) {
		if(grid[startX][startY] == null) {
			return null;
		}
		if(grid[startX][startY].getConnections()[startDirection] == null) {
			return null;
		}
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		while(true) {
			if(grid[xy[0]][xy[1]].getType() == "wire") {
				if(((Wire)grid[xy[0]][xy[1]]).getShape() == "T") {
					int[] xyd = {xy[0], xy[1], reverseDir(direction)};
					return xyd;
				}
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			for(int i = 0; i < 4; i++) {
				if(connections[i] != null) {
					if(i != reverseDir(direction)) {
						xy = connections[i];
						direction = i;
						break;
					} 
				}
				if(i == 3) {
					return null;
				}
			}
		}
	}
	
	public int[] getTExits(int x, int y, int prevDirection) {
		if(grid[x][y].getType() != "wire") {
			return null;
		} else {
			if(((Wire)grid[x][y]).getShape() != "T") {
				return null;
			}
		}
		int[][] connections = grid[x][y].getConnections();
		int[] exits = {-1, -1}; //exits go from less clockwise to more clockwise
		
		int i = (grid[x][y].getRotation() + 2) % 4;
		while(i != (grid[x][y].getRotation() + 1) % 4) {
			if(connections[i] != null) {
				if(i != prevDirection) {
					if(exits[0] == -1) {
					exits[0] = i;
					} else {
						exits[1] = i;
						break;
					}
				} 
			}
			i++;
			if(i == 4) {
				i = 0;
			}
		}
		if(exits[0] == -1 || exits[1] == -1) {
			return null;
		}
		return exits;
	}
	
	public boolean compareArrays(int[] array1, int[] array2) {
		if(array2.length != array1.length) {
			return false;
		}
		for(int i = 0; i < array1.length; i++) {
			if(array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int reverseDir(int direction) {
		return (direction + 2) % 4;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		frame.requestFocus();
		if(e.getX() > WIDTH - 10) { //temporary
			ArrayList<int[]> batteries = findBatteries();
			for(int i = 0; i < batteries.size(); i++) {
				updateBattery(batteries.get(i)[0], batteries.get(i)[1]);
			}
		} else if(e.getY() > HEIGHT) {
			Menu menu = screen.getMenu();
			int menuX = e.getX() - MENU_MIDDLE_X + menu.getWidth()/2;
			int menuY = e.getY() - HEIGHT;
			if(menuX > 0 && menuX < menu.getWidth() && menuY > 0 && menuY < menu.getHeight()) {
				int gridX = menuX/(menu.getWidth()/menu.getGrid().length);
				int gridY = menuY/(menu.getHeight()/menu.getGrid()[0].length);
				selected = menu.getGrid()[gridX][gridY];
				menu.setSelected(gridX, gridY);
			}
			if(e.getButton() == MouseEvent.BUTTON1) {
				variableEntry.setVisible(false);
				editing = null;
			} else if(e.getButton() == MouseEvent.BUTTON3) {
				if(selected != null) {
					if(selected.getType() == "battery") {
						variableEntry.setVisible(true);
						editing = selected;
						variable.setText("Voltage");
						textField.setText(String.valueOf(((Battery)selected).getVoltage()));
					} else if(selected.getType() == "resistor") {
						variableEntry.setVisible(true);
						editing = selected;
						variable.setText("Resistance");
						textField.setText(String.valueOf(((Resistor)selected).getResistance()));
					}
				}
			}
		} else if (e.getY() < HEIGHT) {
			int gridX = e.getX()/(WIDTH/screen.getZoom());
			int gridY = (int)((double)e.getY()/((double)HEIGHT/screen.getYSize()));
			if(e.getButton() == MouseEvent.BUTTON1) {
				variableEntry.setVisible(false);
				editing = null;
				if(selected != null) {
					if(selected.getType() == "wire") {
						Wire selectedWire = (Wire)selected;
						Wire wire = new Wire(selectedWire.getShape(), selectedWire.getRotation());
						grid[gridX][gridY] = wire;
					} else if(selected.getType() == "battery") {
						Battery selectedBattery = (Battery)selected;
						Battery battery = new Battery(selectedBattery.getVoltage(), selectedBattery.getRotation());
						grid[gridX][gridY] = battery;
						setConnections(gridX, gridY);
					} else if(selected.getType() == "resistor") {
						Resistor selectedResistor = (Resistor)selected;
						Resistor resistor = new Resistor(selectedResistor.getResistance(), selectedResistor.getRotation());
						grid[gridX][gridY] = resistor;
					} else if(selected.getType() == "ammeter") {
						Ammeter selectedAmmeter = (Ammeter)selected;
						Ammeter ammeter = new Ammeter(selectedAmmeter.getRotation());
						grid[gridX][gridY] = ammeter;
					}
				} else {
					grid[gridX][gridY] = null;
				}
				setConnections(gridX, gridY);
				updateCircuits();
			} else if(e.getButton() == MouseEvent.BUTTON3) {
				if(grid[gridX][gridY] != null) {
					if(grid[gridX][gridY].getType() == "battery") {
						variableEntry.setVisible(true);
						editing = grid[gridX][gridY];
						variable.setText("Voltage");
						textField.setText(String.valueOf(((Battery)grid[gridX][gridY]).getVoltage()));
					} else if(grid[gridX][gridY].getType() == "resistor") {
						variableEntry.setVisible(true);
						editing = grid[gridX][gridY];
						variable.setText("Resistance");
						textField.setText(String.valueOf(((Resistor)grid[gridX][gridY]).getResistance()));
					}
				}
			}
		}
		screen.repaint();
	}

	public void setConnections(int x, int y) {
		int[] xy = {x, y};
		if(grid[x][y] == null) {
			if(y > 0) {
				if(grid[x][y - 1] != null) {
					grid[x][y - 1].setConnection(DOWN, null);
				}
			}
			if(x < GRID_WIDTH - 1) {
				if(grid[x + 1][y] != null) {
					grid[x + 1][y].setConnection(LEFT, null);
				}
			}
			if(y < GRID_HEIGHT - 1) {
				if(grid[x][y + 1] != null) {
					grid[x][y + 1].setConnection(UP, null);
				}
			}
			if(x > 0) {
				if(grid[x - 1][y] != null) {
					grid[x - 1][y].setConnection(RIGHT, null);
				}
			}
		}
		if(testDirection(UP, x, y) == true && testDirection(DOWN, x, y - 1) == true) {
			int[] xy1 = {x, y - 1};
			grid[x][y].setConnection(UP, xy1);
			grid[x][y - 1].setConnection(DOWN, xy);
		}
		if(testDirection(RIGHT, x, y) == true && testDirection(LEFT, x + 1, y) == true) {
			int[] xy1 = {x + 1, y};
			grid[x][y].setConnection(RIGHT, xy1);
			grid[x + 1][y].setConnection(LEFT, xy);
		}
		if(testDirection(DOWN, x, y) == true && testDirection(UP, x, y + 1) == true) {
			int[] xy1 = {x, y + 1};
			grid[x][y].setConnection(DOWN, xy1);
			grid[x][y + 1].setConnection(UP, xy);
		}
		if(testDirection(LEFT, x, y) == true && testDirection(RIGHT, x - 1, y) == true) {
			int[] xy1 = {x - 1, y};
			grid[x][y].setConnection(LEFT, xy1);
			grid[x - 1][y].setConnection(RIGHT, xy);
		}
	}
	
	public boolean testDirection(int direction, int x, int y) { 
		if(x < 0 || y < 0) {
			return false;
		}
		if(grid[x][y] == null) {
			return false;
		}
		
		int possibleDirection;
		
		//test front connect
		if(grid[x][y].getRotation() == direction) {
			return true;
		} else { //test side connect (L and T wires)
			if(grid[x][y].getType() == "wire") {
				Wire wire = (Wire)grid[x][y];
				if(wire.getShape() == "L" || wire.getShape() == "T") {
					possibleDirection = (direction + 1) % 4;
					if(wire.getRotation() == possibleDirection) {
						return true;
					} else if(wire.getShape() == "L") {
						return false;
					}
				}
			}
		}
		
		//test back connect (all except L wires)
		possibleDirection = (direction + 2) % 4;
		if(grid[x][y].getRotation() == possibleDirection) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R) {
			if(selected != null) {
				int newRotation;
				int oldRotation = selected.getRotation();
				if(oldRotation == 3) {
					newRotation = 0;
				} else {
					newRotation = oldRotation + 1;
				}
				selected.setRotation(newRotation);
				screen.repaint();
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == setValueButton) {
			boolean failed = false;
			if(editing.getType() == "resistor") {
				try {
				((Resistor)editing).setResistance(Double.parseDouble(textField.getText()));
				} catch(NumberFormatException error) {
					failed = true;
				}
			} else if(editing.getType() == "battery") {
				try {
				((Battery)editing).setVoltage(Double.parseDouble(textField.getText()));
				} catch(NumberFormatException error) {
					failed = true;
				}
			}
			if(failed == false) {
				variableEntry.setVisible(false);
				editing = null;
			}
			updateCircuits();
			screen.repaint();
			frame.requestFocus();
		}
	}
}