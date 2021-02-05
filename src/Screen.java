import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Screen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		//draw white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		//draw gray background for menu
		g.setColor(Color.GRAY);
		g.fillRect(0, height, width, menuHeight);
		
		//draw grid
		g.setColor(Color.getHSBColor(0.0f, 0.0f, 0.90f));
		for(int i = 0; i < zoom; i++) {
			g.drawLine(i*width/zoom, 0, i*width/zoom, height);
		}
		
		for(int i = 0; i < Math.ceil((double)height/(double)width*zoom); i++) {
			g.drawLine(0, i*width/zoom, width, i*width/zoom);

		}
		
		//draw all componenets
		g.setColor(Color.BLACK);
		for(int i = 0; i < zoom; i++) {
			for(int j = 0; j < Math.ceil((double)height/(double)width*zoom); j++) {
				if(grid[i][j] != null) {
					grid[i][j].customPaint(g, i*(width/zoom), j*(width/zoom), width/zoom);
				}
			}
		}
		
		//call menu paint
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
