import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Main implements MouseListener {
	
	private static final int UP = 0;
	private static final int RIGHT = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	private static final int MENU_MIDDLE_X = WIDTH/2;
	private static final int MENU_HEIGHT = 200;
	private static final int GRID_WIDTH = 100;
	private static final int GRID_HEIGHT = 100;
	private static final int ZOOM = 10;
	JFrame frame = new JFrame("Circuit Board");
	Screen screen = new Screen(WIDTH, HEIGHT, MENU_MIDDLE_X, MENU_HEIGHT);
	
	CircuitElement selected;
	CircuitElement[][] grid = new CircuitElement[GRID_WIDTH][GRID_HEIGHT]; //will store wires and circuit components
	
	public Main() {
		frame.setLayout(new BorderLayout());
		frame.add(screen, BorderLayout.CENTER);
		screen.addMouseListener(this);
		screen.setZoom(ZOOM);
		frame.setSize(WIDTH, HEIGHT + MENU_HEIGHT);
		frame.setVisible(true);
		
		grid[1][1] = new Wire("L", UP);
		grid[1][2] = new Wire("line", RIGHT);
		grid[2][3] = new Wire("T", UP);
		grid[2][4] = new Wire("T", RIGHT);
		grid[2][5] = new Wire("T", DOWN);
		grid[2][6] = new Wire("T", LEFT);
		screen.setGrid(grid);
		screen.repaint();
//		run();
	}
	
	public void run() {
		while(true) {
		}
	}
	
	public static void main(String[] args) {
		new Main();
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
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getY() > HEIGHT) {
				Menu menu = screen.getMenu();
				int menuX = e.getX() - MENU_MIDDLE_X + menu.getWidth()/2;
				int menuY = e.getY() - HEIGHT;
				if(menuX > 0 && menuX < menu.getWidth() && menuY > 0 && menuY < menu.getHeight()) {
					selected = menu.getGrid()[menuX/(menu.getWidth()/menu.getGrid().length)][menuY/(menu.getHeight()/menu.getGrid()[0].length)];
				}
			}
			if(e.getY() < HEIGHT) {
				int gridX = e.getX()/(WIDTH/screen.getZoom());
				int gridY = (int)((double)e.getY()/((double)HEIGHT/screen.getYSize()));
				if(selected != null) {
					if(selected.getType() == "wire") {
						Wire selectedWire = (Wire)selected;
						Wire wire = new Wire(selectedWire.getShape(), selectedWire.getRotation());
						grid[gridX][gridY] = wire;
					} else if(selected.getType() == "battery") {
						Battery selectedBattery = (Battery)selected;
						Battery battery = new Battery(selectedBattery.getRotation());
						grid[gridX][gridY] = battery;
					}
				} else {
					grid[gridX][gridY] = null;
				}
				screen.repaint();
			}
		} else if(e.getButton() == MouseEvent.BUTTON3) {
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
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}