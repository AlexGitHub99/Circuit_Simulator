import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Screen extends JPanel {
	int zoom;
	int width;
	int height;
	int menuHeight;
	CircuitElement[][] grid;
	Menu menu;
	
	public Screen(int newWidth, int newHeight, int newMenuMiddleX, int newMenuHeight) {
		zoom = 10;
		width = newWidth;
		height = newHeight;
		menuHeight = newMenuHeight;
		menu = new Menu(height, newMenuMiddleX, newMenuHeight);
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		for(int i = 0; i < zoom; i++) {
			for(int j = 0; j < Math.ceil((double)height/(double)width*zoom); j++) {
				if(grid[i][j] != null) {
					grid[i][j].customPaint(g, i*(width/zoom), j*(width/zoom), width/zoom);
				}
			}
		}
		menu.customPaint(g);
	}
	
	public void setGrid(CircuitElement[][] newGrid) {
		grid = newGrid;
	}
	
	public void setZoom(int newZoom) {
		zoom = newZoom;
	}
	
	public int getZoom() {
		return zoom;
	}
	
	public double getYSize() {
		return (double)height/(double)width*(double)zoom;
	}
	public Menu getMenu() {
		return menu;
	}
}
