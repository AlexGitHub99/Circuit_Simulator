import java.awt.Font;
import java.awt.Graphics;

public class Resistor extends CircuitElement {
	
	double resistance;
	
	public Resistor(double newResistance, int newRotation) {
		type = "resistor";
		rotation = newRotation;
		resistance = newResistance;
	}
	
	public Resistor() {
		type = "resistor";
	}
	
	public void setResistance(double newResistance) {
		resistance = newResistance;
	}
	
	public double getResistance() {
		return resistance;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		if(rotation == UP) {
			int[] xPoints = {x + size*3/6, x + size*4/6, x + size*2/6, x + size*4/6, x + size*2/6, x + size*4/6, x + size*2/6, x + size*3/6};
			int[] yPoints = {y + size*7/42, y + size*11/42, y + size*15/42, y + size*19/42, y + size*23/42, y + size*27/42, y + size*31/42, y + size*35/42};
			g.drawPolyline(xPoints, yPoints, xPoints.length);
			g.drawLine(x + size/2, y, x + size/2, y + size/6);
			g.drawLine(x + size/2, y + size*5/6, x + size/2, y + size);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(resistance * 100) / 100), x + size*2/3 + 5, y + size*3/5);
			g.setFont(prevFont);
		}
		if(rotation == RIGHT) {
			int[] xPoints = {x + size*7/42, x + size*11/42, x + size*15/42, x + size*19/42, x + size*23/42, x + size*27/42, x + size*31/42, x + size*35/42};
			int[] yPoints = {y + size*3/6, y + size*2/6, y + size*4/6, y + size*2/6, y + size*4/6, y + size*2/6, y + size*4/6, y + size*3/6};
			g.drawPolyline(xPoints, yPoints, xPoints.length);
			g.drawLine(x, y + size/2, x + size/6, y + size/2);
			g.drawLine(x + size*5/6, y + size/2, x + size, y + size/2);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(resistance * 100) / 100), x + size*2/5, y + size*4/5 + 8);
			g.setFont(prevFont);
		}
		if(rotation == DOWN) {
			int[] xPoints = {x + size*3/6, x + size*4/6, x + size*2/6, x + size*4/6, x + size*2/6, x + size*4/6, x + size*2/6, x + size*3/6};
			int[] yPoints = {y + size*7/42, y + size*11/42, y + size*15/42, y + size*19/42, y + size*23/42, y + size*27/42, y + size*31/42, y + size*35/42};
			g.drawPolyline(xPoints, yPoints, xPoints.length);
			g.drawLine(x + size/2, y, x + size/2, y + size/6);
			g.drawLine(x + size/2, y + size*5/6, x + size/2, y + size);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(resistance * 100) / 100), x + size*2/3 + 5, y + size*3/5);
			g.setFont(prevFont);
		}
		if(rotation == LEFT) {
			int[] xPoints = {x + size*7/42, x + size*11/42, x + size*15/42, x + size*19/42, x + size*23/42, x + size*27/42, x + size*31/42, x + size*35/42};
			int[] yPoints = {y + size*3/6, y + size*2/6, y + size*4/6, y + size*2/6, y + size*4/6, y + size*2/6, y + size*4/6, y + size*3/6};
			g.drawPolyline(xPoints, yPoints, xPoints.length);
			g.drawLine(x, y + size/2, x + size/6, y + size/2);
			g.drawLine(x + size*5/6, y + size/2, x + size, y + size/2);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(resistance * 100) / 100), x + size*2/5, y + size*4/5 + 8);
			g.setFont(prevFont);
		}
	}
}
