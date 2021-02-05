import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main implements MouseListener, KeyListener, ActionListener{
	
	private static final int UP = 0;
	private static final int RIGHT = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	private static final int MENU_MIDDLE_X = WIDTH/2;
	private static final int MENU_HEIGHT = 200;
	private static final int INFO_WIDTH = 300;
	private static final int TEXT_AREA_HEIGHT = 500;
	private static final int VARIABLE_HEIGHT = 50;
	private static final int GRID_WIDTH = 100;
	private static final int GRID_HEIGHT = 100;
	private static final int ZOOM = 10;
	JFrame frame = new JFrame("Circuit Board");
	Screen screen = new Screen(WIDTH, HEIGHT, MENU_MIDDLE_X, MENU_HEIGHT);
	JPanel info = new JPanel();
	JPanel variableEntry = new JPanel();
	JTextField textField = new JTextField();
	JLabel variable = new JLabel();
	JButton setValueButton = new JButton();
	JButton saveButton = new JButton();
	JButton loadButton = new JButton();
	JTextArea textArea = new JTextArea();
	CircuitElement selected;
	CircuitElement editing;
	CircuitElement[][] grid = new CircuitElement[GRID_WIDTH][GRID_HEIGHT]; //will store wires and circuit components
	
	public Main() {
		//GUI setup
		frame.setLayout(new BorderLayout());
		frame.addKeyListener(this);
		screen.addMouseListener(this);
		screen.setZoom(ZOOM);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		variableEntry.setLayout(null);
		variableEntry.setBorder(BorderFactory.createLineBorder(Color.RED));
		variableEntry.setBackground(Color.PINK);
		variableEntry.setMaximumSize(new Dimension(INFO_WIDTH, VARIABLE_HEIGHT + 20));
		textField.setBounds(INFO_WIDTH/2, 10, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		variable.setBounds(0, 10, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		setValueButton.setBounds(INFO_WIDTH/2, VARIABLE_HEIGHT/2 + 10, INFO_WIDTH/2, VARIABLE_HEIGHT/2);
		String text = "";
		try {
			File textFile = new File("info.txt");
			Scanner scanner = new Scanner(textFile);
			while(scanner.hasNextLine()) {
				text += scanner.nextLine() + '\n';
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textArea.setText(text);
		textArea.setEditable(false);
		textArea.setMaximumSize(new Dimension(INFO_WIDTH, TEXT_AREA_HEIGHT));
		variableEntry.add(textField);
		variableEntry.add(variable);
		variableEntry.add(setValueButton);
		info.add(textArea);
		info.add(saveButton);
		info.add(loadButton);
		info.add(variableEntry);
		
		frame.add(screen, BorderLayout.CENTER);
		frame.add(info, BorderLayout.EAST);
		variable.setText("Test");
		setValueButton.setText("Set Value");
		setValueButton.addActionListener(this);
		variableEntry.setVisible(false);
		saveButton.setText("Save Circuit");
		saveButton.addActionListener(this);
		loadButton.addActionListener(this);
		loadButton.setText("Load Circuit");
		frame.setSize(WIDTH + 50 + INFO_WIDTH, HEIGHT + MENU_HEIGHT + 50);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setGrid(grid);
		screen.repaint();
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	public void run() {
		while(true) {
		}
	}
	
	public void updateCircuits() { //find all batteries and update each of their circuits with updateBattery
		ArrayList<int[]> batteries = findBatteries();
		for(int i = 0; i < batteries.size(); i++) {
			updateBattery(batteries.get(i)[0], batteries.get(i)[1]);
		}
	}
	
	public ArrayList<int[]> findBatteries() { //find all batteries and return them in an array list
		ArrayList<int[]> batteries = new ArrayList<int[]>();
		for(int i = 0; i < GRID_WIDTH; i++ ) {
			for(int j = 0; j < GRID_HEIGHT; j++ ) {
				if(grid[i][j] != null) {
					if(grid[i][j].getType().equals("battery")) {
						int[] pos = {i, j};
						batteries.add(pos);
					}
				}
			}
		}
		return batteries;
	}
	
	public void updateBattery(int x, int y) { //update the circuit this battery is connected to
		if(!grid[x][y].getType().equals("battery")) {
			return;
		}
		Battery battery = (Battery)grid[x][y];
		if(battery.getRotation() == UP || battery.getRotation() == DOWN) { //verticle
			double resistance = calcResistance(x, y, x, y, UP);
			if(resistance == -1) {
				return;
			}
			double current = battery.getVoltage()/resistance; //calc starting current
			
			updateAmmeters(x, y, x, y, UP, current);
			updateVoltmeters(x, y, x, y, UP, battery.getVoltage(), current);
		} else { //rotation is RIGHT or LEFT (horizontal)
			double resistance = calcResistance(x, y, x, y, RIGHT);
			if(resistance == -1) {
				return;
			}
			double current = battery.getVoltage()/resistance; //calc starting current
			updateAmmeters(x, y, x, y, RIGHT, current);
			updateVoltmeters(x, y, x, y, RIGHT, battery.getVoltage(), current);
		}
	}
	
	//update voltmeters recursively within a section of wire denoted by a start and end position, and direction to start
	public void updateVoltmeters(int startX, int startY, int endX, int endY, int startDirection, double voltage, double current) {
		if(grid[startX][startY].getType().equals("voltmeter")) { //set voltmeter to current voltage
			((Voltmeter)grid[startX][startY]).setVoltage(voltage);
		}
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		//main iteration loop
		while(true) {
			if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
				break;
			}
			
			if(grid[xy[0]][xy[1]].getType().equals("voltmeter")) { //set voltmeter to current voltage
				((Voltmeter)grid[xy[0]][xy[1]]).setVoltage(voltage);
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			boolean findNext = true;
			if(grid[xy[0]][xy[1]].getType().equals("wire")) {
				if(((Wire)grid[xy[0]][xy[1]]).getShape().equals("T")) { //found junction
					int[] junctionExits = getTExits(xy[0], xy[1], reverseDir(direction));
					
					if(junctionExits == null) {
						return;
					}
					
					//find end junction
					int[] endJunction = findJunction(null, xy[0], xy[1], xy[0], xy[1], endX, endY, junctionExits[0]);
					if(endJunction == null) {
						return;
					}
					
					//calculate resistances of both sections
					double firstSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0]);
					double secondSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1]);
					if(firstSec == -1 || secondSec == -1) {
						return;
					}
					
					double nextVoltage = voltage;
					double resistance;
					double firstSecCurrent;
					double secondSecCurrent;
					if(firstSec == 0 || secondSec == 0) { //direct path, current will be the same and voltage will be 0
						firstSecCurrent = current;
						secondSecCurrent = current;
						nextVoltage = 0;
					} else {
						if(firstSec == Double.POSITIVE_INFINITY && secondSec == Double.POSITIVE_INFINITY) { //if resistance is infinite current is 0
							firstSecCurrent = 0;
							secondSecCurrent = 0;
						} else { //find voltage and current based on resistance and current current
							resistance = 1/(1/firstSec + 1/secondSec);
							nextVoltage = current*resistance;
							firstSecCurrent = nextVoltage/firstSec;
							secondSecCurrent = nextVoltage/secondSec;
						}
						
					}
					//recurse on each section
					updateVoltmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0], nextVoltage, firstSecCurrent);
					updateVoltmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1], nextVoltage, secondSecCurrent);
					//skip to end junction
					xy = grid[endJunction[0]][endJunction[1]].getConnections()[endJunction[2]];
					findNext = false;
					
					direction = endJunction[2];
				}
			}
			
			//get next connection and go there
			if(findNext == true) {
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
	}
	
	//update ammeters recursively within a section of wire denoted by a start and end position, and direction to start
	public void updateAmmeters(int startX, int startY, int endX, int endY, int startDirection, double current) {
		if(grid[startX][startY].getType().equals("ammeter")) { //set ammeter to current current
			((Ammeter)grid[startX][startY]).setCurrent(current);
		}
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		//main iteration loop
		while(true) {
			if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
				break;
			}
			
			if(grid[xy[0]][xy[1]].getType().equals("ammeter")) { //set ammeter to current current
				((Ammeter)grid[xy[0]][xy[1]]).setCurrent(current);
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			boolean findNext = true;
			if(grid[xy[0]][xy[1]].getType().equals("wire")) {
				if(((Wire)grid[xy[0]][xy[1]]).getShape().equals("T")) { //found junction
					int[] junctionExits = getTExits(xy[0], xy[1], reverseDir(direction));
					
					if(junctionExits == null) {
						return;
					}
					//find matching junction
					int[] endJunction = findJunction(null, xy[0], xy[1], xy[0], xy[1], endX, endY, junctionExits[0]);
					if(endJunction == null) {
						return;
					}
					
					//get resistance for both sections
					double firstSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0]);
					double secondSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1]);
					if(firstSec == -1 || secondSec == -1) {
						return;
					}
					
					double resistance;
					double firstSecCurrent;
					double secondSecCurrent;
					if(firstSec == 0 || secondSec == 0) { //keep current current
						firstSecCurrent = current;
						secondSecCurrent = current;
					} else { //calculate new current based on current current and resistance/voltage
						resistance = 1/(1/firstSec + 1/secondSec);
						double voltage = current*resistance;
						firstSecCurrent = voltage/firstSec;
						secondSecCurrent = voltage/secondSec;
					}
					//recursively call on each section
					updateAmmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0], firstSecCurrent);
					updateAmmeters(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1], secondSecCurrent);
					xy = grid[endJunction[0]][endJunction[1]].getConnections()[endJunction[2]];
					findNext = false;
					
					direction = endJunction[2];
				}
			}
			
			//get next connection and go there
			if(findNext == true) {
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
	}
	
	//find resistance recursively in a section (parallel or series) denoted by start and end positions and start direction
	public double calcResistance(int startX, int startY, int endX, int endY, int startDirection) {
		double resistance = 0;
		
		if(grid[startX][startY] == null || grid[endX][endY] == null) {
			return -1;
		}
		if(grid[startX][startY].getConnections()[startDirection] == null) {
			return -1;
		}
		
		//in case the start and end and not the same, add resistor at the start if there is one
		if((startX == endX && startY == endY) == false) { 
			if(grid[startX][startY].getType().equals("resistor")) { //add resistance of this resistor
				resistance += ((Resistor)grid[startX][startY]).getResistance();
			}
		}
		
		if(grid[startX][startY].getType().equals("voltmeter")) { //voltmeters have infinite resistance (ideally)
			resistance = Double.POSITIVE_INFINITY;
		}
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		
		int direction = startDirection;
		//main iteration loop
		while(true) {
			if(grid[xy[0]][xy[1]].getType().equals("resistor")) { //add resistance
				resistance += ((Resistor)grid[xy[0]][xy[1]]).getResistance();
			}
			
			if(grid[xy[0]][xy[1]].getType().equals("voltmeter")) {
				resistance = Double.POSITIVE_INFINITY;
			}
			
			if(xy[0] == endX && xy[1] == endY) { //reached end coordinates
				break;
			}
			
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			boolean findNext = true;
			if(grid[xy[0]][xy[1]].getType().equals("wire")) {
				if(((Wire)grid[xy[0]][xy[1]]).getShape().equals("T")) { //found junction
					int[] junctionExits = getTExits(xy[0], xy[1], reverseDir(direction));
					
					if(junctionExits == null) {
						return -1;
					}
					//find matching junction
					int[] endJunction = findJunction(null, xy[0], xy[1], xy[0], xy[1], endX, endY, junctionExits[0]);
					if(endJunction == null) {
						return -1;
					}
					//end Junction should have a 3rd array value that gives exit direction, if not, findJuntion failed
					if(endJunction.length != 3) { 
						return -1;
					}
					
					//calculate resistance recursively for both sections
					double firstSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[0]);
					double secondSec = calcResistance(xy[0], xy[1], endJunction[0], endJunction[1], junctionExits[1]);
					if(firstSec == -1 || secondSec == -1) {
						return -1;
					}
					//if either is 0 there's a direct path and so the total resistance is effectively 0
					if(firstSec == 0 || secondSec == 0) { 
						resistance += 0;
					} else if(firstSec == Double.POSITIVE_INFINITY && secondSec == Double.POSITIVE_INFINITY) {
						resistance = Double.POSITIVE_INFINITY;
					} else {
						resistance += 1/(1/firstSec + 1/secondSec); //if one sections i infinity 1/section will be 0
					}
					xy = grid[endJunction[0]][endJunction[1]].getConnections()[endJunction[2]]; //skip to end junction
					findNext = false;
					direction = endJunction[2];
				}
			}
				
			//get connected component and go to it
			if(findNext == true) {
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
		}
		
		return resistance;
	}
	
	//Finds the matching junction in a parallel circuit (wheatstone-bridge type circuits won't work)
	public int[] findJunction(ArrayList<int[]> prevJunctions, int junctionX, int junctionY, int startX, int startY, int endX, int endY, int startDirection) {
		if(prevJunctions == null) { //true if on first function call
			prevJunctions = new ArrayList<int[]>();
		}
		if(grid[startX][startY] == null) {
			return null;
		}
		
		if(grid[startX][startY].getConnections()[startDirection] == null) {
			return null;
		}
		
		
		int[] xy = grid[startX][startY].getConnections()[startDirection];
		int direction = startDirection;
		
		//main loop that steps through each tile of the circuit
		while(true) {
			int[][] connections = grid[xy[0]][xy[1]].getConnections();
			
			if(xy[0] == endX && xy[1] == endY) {//reached end coords
				return xy;
			}
			
			if(grid[xy[0]][xy[1]].getType().equals("wire")) {
				if(((Wire)grid[xy[0]][xy[1]]).getShape().equals("T")) { //at junction
					if(searchArrayList(prevJunctions, xy) == true) { //already been at this junction
						return null;
					}
					if(xy[0] == junctionX && xy[1] == junctionY) { //looped back to original junction
						return xy;
					}
					//get junction connections
					int counter = -1;
					int clockwise = -1;
					for(int i = 0; i < 4; i++) {
						if(connections[i] != null) {
							if(i != reverseDir(direction)) {
								if(counter == -1) {
									counter = i;
								} else {
									clockwise = i;
									break;
								}
							} 
						}
						if(i == 3) {
							return null;
						}
					}
					
					prevJunctions.add(xy);
					//find matching junction for this junction
					int[] branch1 = findJunction(prevJunctions, junctionX, junctionY, xy[0], xy[1], endX, endY, counter);
					int[] branch2 = findJunction(prevJunctions, junctionX, junctionY, xy[0], xy[1], endX, endY, clockwise);
					if(branch1 != null && branch2 != null) {
						if(branch1[0] == junctionX && branch1[1] == junctionY) {
							if(branch2[0] == endX && branch2[1] == endY) { 
								//junction found!
								int[] xyd = {xy[0], xy[1], clockwise};
								return xyd;
							}
						} else if(branch2[0] == junctionX && branch2[1] == junctionY) {
							if(branch1[0] == endX && branch1[1] == endY) { 
								//junction found!
								int[] xyd = {xy[0], xy[1], counter};
								return xyd;
							}
						}
					} else if(branch1 != null) { //either found the end or the junction
						return branch1;
					} else if(branch2 != null) { //either found the end or the junction
						return branch2;
					}
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
				if(i == 3) {
					return null;
				}
			}
		}
	}
	
	public int[] getTExits(int x, int y, int prevDirection) { //return TWO t exits excluding the given direction
		if(!grid[x][y].getType().equals("wire")) {  //must be wire
			return null;
		} else {
			if(!((Wire)grid[x][y]).getShape().equals("T")) { //must be T wire
				return null;
			}
		}
		int[][] connections = grid[x][y].getConnections();
		int[] exits = {-1, -1}; //exits go from less clockwise to more clockwise
		
		//iterate through directions and check if each is a valid exit
		int i = (grid[x][y].getRotation() + 2) % 4;
		while(i != (grid[x][y].getRotation() + 1) % 4) {
			if(connections[i] != null) {
				if(i != prevDirection) { //exit must not be the previous direction
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
		if(exits[0] == -1 || exits[1] == -1) { //missing connection
			return null;
		}
		return exits;
	}
	
	public boolean searchArrayList(ArrayList<int[]> arrayList, int[] values) { //search in an arraylist for a given value (.contains doesn't work ?)
		for(int i = 0; i < arrayList.size(); i++) {
			if(compareArrays(arrayList.get(i), values) == true) {
				return true;
			}
		}
		return false;
		
	}
	
	public boolean compareArrays(int[] array1, int[] array2) { //compare 2 int arrays of same size and return true if identical
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
		if(e.getY() > HEIGHT) { //mouse in menu area
			//select componenet in menu for placing ong rid
			Menu menu = screen.getMenu();
			int menuX = e.getX() - MENU_MIDDLE_X + menu.getWidth()/2;
			int menuY = e.getY() - HEIGHT;
			if(menuX > 0 && menuX < menu.getWidth() && menuY > 0 && menuY < menu.getHeight()) {
				int gridX = menuX/(menu.getWidth()/menu.getGrid().length);
				int gridY = menuY/(menu.getHeight()/menu.getGrid()[0].length);
				selected = menu.getGrid()[gridX][gridY];
				menu.setSelected(gridX, gridY);
			}
			if(e.getButton() == MouseEvent.BUTTON1) { //left mouse button
				variableEntry.setVisible(false);
				editing = null;
			} else if(e.getButton() == MouseEvent.BUTTON3) { //right mouse button
				if(selected != null) {
					if(selected.getType().equals("battery")) { //edit battery in menu
						variableEntry.setVisible(true);
						editing = selected;
						variable.setText("Voltage");
						textField.setText(String.valueOf(((Battery)selected).getVoltage()));
					} else if(selected.getType().equals("resistor")) { //select resistor in menu
						variableEntry.setVisible(true);
						editing = selected;
						variable.setText("Resistance");
						textField.setText(String.valueOf(((Resistor)selected).getResistance()));
					}
				}
			}
		} else if (e.getY() < HEIGHT) { //mouse in circuit grid area
			int gridX = e.getX()/(WIDTH/screen.getZoom());
			int gridY = (int)((double)e.getY()/((double)HEIGHT/screen.getYSize()));
			if(e.getButton() == MouseEvent.BUTTON1) { //left mouse button
				variableEntry.setVisible(false);
				editing = null;
				if(selected != null) {
					if(selected.getType().equals("wire")) { //place wire
						Wire selectedWire = (Wire)selected;
						Wire wire = new Wire(selectedWire.getShape(), selectedWire.getRotation());
						grid[gridX][gridY] = wire;
					} else if(selected.getType().equals("battery")) { //place battery
						Battery selectedBattery = (Battery)selected;
						Battery battery = new Battery(selectedBattery.getVoltage(), selectedBattery.getRotation());
						grid[gridX][gridY] = battery;
						setConnections(gridX, gridY);
					} else if(selected.getType().equals("resistor")) { //place resistor
						Resistor selectedResistor = (Resistor)selected;
						Resistor resistor = new Resistor(selectedResistor.getResistance(), selectedResistor.getRotation());
						grid[gridX][gridY] = resistor;
					} else if(selected.getType().equals("ammeter")) { //place ammeter
						Ammeter selectedAmmeter = (Ammeter)selected;
						Ammeter ammeter = new Ammeter(selectedAmmeter.getRotation());
						grid[gridX][gridY] = ammeter;
					} else if(selected.getType().equals("voltmeter")) { //place voltmeter
						Voltmeter selectedVoltmeter = (Voltmeter)selected;
						Voltmeter voltmeter = new Voltmeter(selectedVoltmeter.getRotation());
						grid[gridX][gridY] = voltmeter;
					}
				} else {
					grid[gridX][gridY] = null;
				}
				setConnections(gridX, gridY);
				updateCircuits();
			} else if(e.getButton() == MouseEvent.BUTTON3) { //right mousde button
				if(grid[gridX][gridY] != null) {
					if(grid[gridX][gridY].getType().equals("battery")) { //select battery
						variableEntry.setVisible(true);
						editing = grid[gridX][gridY];
						variable.setText("Voltage");
						textField.setText(String.valueOf(((Battery)grid[gridX][gridY]).getVoltage()));
					} else if(grid[gridX][gridY].getType().equals("resistor")) { //select resistor
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

	//update connections for given component at x,y AND all components it's connected to
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
	
	public boolean testDirection(int direction, int x, int y) { //test if component has a connection in this direction
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
			if(grid[x][y].getType().equals("wire")) {
				Wire wire = (Wire)grid[x][y];
				if(wire.getShape().equals("L") || wire.getShape().equals("T")) {
					possibleDirection = (direction + 1) % 4;
					if(wire.getRotation() == possibleDirection) {
						return true;
					} else if(wire.getShape().equals("L")) {
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
		if(e.getKeyCode() == KeyEvent.VK_R) { //rotate
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
		} else if(e.getKeyCode() == KeyEvent.VK_EQUALS) { //zoom in
			screen.setZoom(screen.getZoom() - 1);
			screen.repaint();
		} else if(e.getKeyCode() == KeyEvent.VK_MINUS) { //zoom out
			screen.setZoom(screen.getZoom() + 1);
			screen.repaint();
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == setValueButton) { //set value of component stored by "editing" variable
			boolean failed = false;
			if(editing.getType().equals("resistor")) {
				try {
				((Resistor)editing).setResistance(Double.parseDouble(textField.getText()));
				} catch(NumberFormatException error) {
					failed = true;
				}
			} else if(editing.getType().equals("battery")) {
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
		} else if(e.getSource() == saveButton) { //save file
			try {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int val = fileChooser.showSaveDialog(frame);
				if(val == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					FileOutputStream fileOut = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(grid);
					out.close();
					fileOut.close();
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			frame.requestFocus();
		} else if(e.getSource() == loadButton) { //load file
			try {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int val = fileChooser.showOpenDialog(frame);
				if(val == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					FileInputStream fileIn = new FileInputStream(file);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					grid = null;
					grid = (CircuitElement[][]) in.readObject();
					screen.setGrid(grid);
					int edgeX = 0;
					int edgeY = 0;
					for(int i = 0; i < grid.length; i++) {
						for(int j = 0; j < grid[0].length; j++) {
							if(grid[i][j] != null) {
								if(i > edgeX) {
									edgeX = i;
								}
								if(j > edgeY) {
									edgeY = j;
								}
							}
						}
					}
					//set zoom to be larger than circuit edges
					if(edgeX > ZOOM && edgeX > edgeY) {
						screen.setZoom(edgeX + 1);
					} else if(edgeY > (Math.ceil((double)HEIGHT/(double)WIDTH*ZOOM)) && edgeY > edgeX) {
						screen.setZoom((int)((double)edgeY*(double)WIDTH/(double)HEIGHT) + 2);
					}
					in.close();
					fileIn.close();
				}
				
			} catch (IOException | ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			screen.repaint();
			frame.requestFocus();
		}
	}
}