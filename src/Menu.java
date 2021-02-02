import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Menu extends JPanel {
	int width;
	int height;
	int x;
	int y;
	int[] selXY;
	int UP = 0;
	int X_DIMENSION = 4;
	int Y_DIMENSION = 2;
	
	CircuitElement[][] grid = new CircuitElement[X_DIMENSION][Y_DIMENSION];
	
	public Menu(int newY, int newXMiddle, int newHeight) {
		height = newHeight;
		width = height/Y_DIMENSION*X_DIMENSION;
		x = newXMiddle - width/2;
		y = newY;
		selXY = null;
		grid[0][0] = new Wire("line", UP);
		grid[1][0] = new Wire("L", UP);
		grid[2][0] = new Wire("T", UP);
		grid[3][0] = new Battery(10, UP);
		grid[0][1] = new Resistor(3, UP);
		grid[1][1] = new Ammeter(UP);
	}
	
	public void customPaint(Graphics g) {
		g.setColor(Color.getHSBColor(0, 0, (float) 0.95));
		g.fillRect(x, y, width, height);
		
		g.setColor(Color.RED);
		for(int i = 0; i < grid.length; i++) {
			g.drawLine(x + i*(width/grid.length), y, x + i*(width/grid.length), y + height);
		}
		g.drawLine(x + width, y, x+ width, y + height);
		
		for(int i = 0; i < grid[0].length; i++) {
			g.drawLine(x, y + i*(height/grid[0].length), x + width, y + i*(height/grid[0].length));
		}
		g.drawLine(x, y + height, x + width, y + height);
		
		int size = width/grid.length;
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] != null) {
					g.setColor(Color.BLACK);
					grid[i][j].customPaint(g, x + i*(width/grid.length) + 10, y + j*(height/grid[0].length) + 10, size - 20);
					if(selXY != null) {
						if(selXY[0] == i && selXY[1] == j) {
							g.setColor(Color.GRAY);
							g.fillOval(x + i*(width/grid.length) + size/12, y + j*(height/grid[0].length) + size/12, size/12, size/12);
						}
					}
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
	
	public void setSelected(int selX, int selY) {
		if(selXY == null) {
			selXY = new int[2];
		}
		selXY[0] = selX;
		selXY[1] = selY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
