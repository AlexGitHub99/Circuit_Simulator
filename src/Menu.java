import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Menu extends JPanel {
	int width;
	int height;
	int x;
	int y;
	int UP = 0;
	int X_DIMENSION = 3;
	int Y_DIMENSION = 2;
	
	CircuitElement[][] grid = new CircuitElement[X_DIMENSION][Y_DIMENSION];
	
	public Menu(int newY, int newXMiddle, int newHeight) {
		height = newHeight;
		width = height/Y_DIMENSION*X_DIMENSION;
		x = newXMiddle - width/2;
		y = newY;
		grid[0][0] = new Wire("line", UP);
		grid[1][0] = new Wire("L", UP);
		grid[2][0] = new Wire("T", UP);
		grid[0][1] = new Battery(UP);
	}
	
	public void customPaint(Graphics g) {
		g.setColor(Color.getHSBColor(0, 0, (float) 0.95));
		g.fillRect(x, y, width, height);
		
		g.setColor(Color.getHSBColor(0, 0, (float) 0.40));
		for(int i = 0; i < grid.length; i++) {
			g.drawLine(x + i*(width/grid.length), y, x + i*(width/grid.length), y + height);
		}
		g.drawLine(x + width, y, x+ width, y + height);
		
		for(int i = 0; i < grid[0].length; i++) {
			g.drawLine(x, y + i*(height/grid[0].length), x + width, y + i*(height/grid[0].length));
		}
		g.drawLine(x, y + height, x + width, y + height);
		
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] != null) {
					g.setColor(Color.BLACK);
					grid[i][j].customPaint(g, x + i*(width/grid.length), y + j*(height/grid[0].length), width/grid.length);
				}
			}
		}
	}
	
	public CircuitElement[][] getGrid() {
		return grid;
	}
	
	public void setGrid(CircuitElement[][] newGrid) {
		grid = newGrid;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
