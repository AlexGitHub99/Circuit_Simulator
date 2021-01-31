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
	
	public void setResistance(int newResistance) {
		resistance = newResistance;
	}
	
	public double getResistance() {
		return resistance;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		if(rotation == UP) {
			g.drawLine(x + size/2, y, x + size/2, y + size/6);
			g.drawLine(x + size/2, y + size*5/6, x + size/2, y + size);
			g.drawRect(x + size*2/5, y + size/6, size/5, size*4/6);
		}
		if(rotation == RIGHT) {
			g.drawLine(x, y + size/2, x + size/6, y + size/2);
			g.drawLine(x + size*5/6, y + size/2, x + size, y + size/2);
			g.drawRect(x + size/6, y + size*2/5, size*4/6, size/5);
		}
		if(rotation == DOWN) {
			g.drawLine(x + size/2, y, x + size/2, y + size/6);
			g.drawLine(x + size/2, y + size*5/6, x + size/2, y + size);
			g.drawRect(x + size*2/5, y + size/6, size/5, size*4/6);
		}
		if(rotation == LEFT) {
			g.drawLine(x, y + size/2, x + size/6, y + size/2);
			g.drawLine(x + size*5/6, y + size/2, x + size, y + size/2);
			g.drawRect(x + size/6, y + size*2/5, size*4/6, size/5);
		}
	}
}
