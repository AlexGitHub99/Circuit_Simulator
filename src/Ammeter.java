import java.awt.Color;
import java.awt.Graphics;
import java.text.AttributedCharacterIterator;

import javax.xml.soap.Text;

public class Ammeter extends CircuitElement {
	
	double current;
	
	public Ammeter(int newRotation) {
		current = 0;
		type = "ammeter";
		rotation = newRotation;
	}
	
	public Ammeter() {
		type = "resistor";
	}
	
	public void setCurrent(double newCurrent) {
		current = newCurrent;
	}
	
	public double getCurrent() {
		return current;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		if(rotation == UP) {
			g.drawLine(x + size/2, y, x + size/2, y + size/4);
			g.drawLine(x + size/2, y + size*3/4, x + size/2, y + size);
		}
		if(rotation == RIGHT) {
			g.drawLine(x, y + size/2, x + size/4, y + size/2);
			g.drawLine(x + size*3/4, y + size/2, x + size, y + size/2);
		}
		if(rotation == DOWN) {
			g.drawLine(x + size/2, y, x + size/2, y + size/4);
			g.drawLine(x + size/2, y + size*3/4, x + size/2, y + size);
		}
		if(rotation == LEFT) {
			g.drawLine(x, y + size/2, x + size/4, y + size/2);
			g.drawLine(x + size*3/4, y + size/2, x + size, y + size/2);
		}
		String currentString;
		if(Double.isNaN(current) || Double.isInfinite(current)) {
			currentString = "short";
		} else {
			currentString = String.valueOf((double)Math.round(current * 100) / 100);
		}
		g.drawString(currentString, x + size/2 - 10, y + size/2);
		g.drawString("A", x + size/2 - 3, y + size*2/3);
		g.drawOval(x + size/4, y + size/4, size/2, size/2);
	}
}
