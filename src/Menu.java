import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Menu extends JPanel {
	int width;
	int height;
	int x;
	int y;
	int[] selXY;
	int UP = 0;
	int X_DIMENSION = 4;
	int Y_DIMENSION = 2;
	int DELETE_X = 3;
	int DELETE_Y = 1;
	
	CircuitElement[][] grid = new CircuitElement[X_DIMENSION][Y_DIMENSION];
	
	public Menu(int newY, int newXMiddle, int newHeight) {
		height = newHeight;
		width = height/Y_DIMENSION*X_DIMENSION;
		x = newXMiddle - width/2;
		y = newY;
		selXY = null;
		//set menu slots
		grid[0][0] = new Wire("line", UP);
		grid[1][0] = new Wire("L", UP);
		grid[2][0] = new Wire("T", UP);
		grid[3][0] = new Battery(10, UP);
		grid[0][1] = new Resistor(3, UP);
		grid[1][1] = new Ammeter(UP);
		grid[2][1] = new Voltmeter(UP);
	}
	
	public void customPaint(Graphics g) {
		//draw menu color
		g.setColor(Color.getHSBColor(0, 0, (float) 0.95));
		g.fillRect(x, y, width, height);
		
		//draw grid
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
		
		//draw menu componenets, selection, and text
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] != null) {
					if(selXY != null) {
						if(selXY[0] == i && selXY[1] == j) {
							g.setColor(Color.PINK);
							g.fillRect(x + i*(width/grid.length), y + j*(height/grid[0].length), (width/grid.length), (height/grid[0].length));
						}
					}
					g.setColor(Color.BLACK);
					grid[i][j].customPaint(g, x + i*(width/grid.length) + 10, y + j*(height/grid[0].length) + 10, size - 20);
					g.drawString(grid[i][j].getType(), x + i*(width/grid.length) + 3, y + (j + 1)*(height/grid[0].length) - 3);
					if(selXY != null) {
						if(selXY[0] == i && selXY[1] == j) {
							g.setColor(Color.GRAY);
							g.fillOval(x + i*(width/grid.length) + size/12, y + j*(height/grid[0].length) + size/12, size/12, size/12);
						}
					}
				}
			}
		}
		
		//draw delete square, text, and selection
		if(selXY != null) {
			if(selXY[0] == DELETE_X && selXY[1] == DELETE_Y) {
				g.setColor(Color.PINK);
				g.fillRect(x + DELETE_X*(width/grid.length), y + DELETE_Y*(height/grid[0].length), (width/grid.length), (height/grid[0].length));
			}
		}
		Font prevFont = g.getFont();
		g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size));
		g.setColor(Color.RED);
		g.drawString("X", x + DELETE_X*(width/grid.length) + 15, y + DELETE_Y*(height/grid[0].length) + size - 15);
		g.setFont(prevFont);
		g.setColor(Color.BLACK);
		if(selXY != null) {
			if(selXY[0] == DELETE_X && selXY[1] == DELETE_Y) {
				g.setColor(Color.GRAY);
				g.fillOval(x + DELETE_X*(width/grid.length) + size/12, y + DELETE_Y*(height/grid[0].length) + size/12, size/12, size/12);
			}
		}
		g.setColor(Color.BLACK);
		g.drawString("delete", x + DELETE_X*(width/grid.length) + 3, y + (DELETE_Y + 1)*(height/grid[0].length) - 3);
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
