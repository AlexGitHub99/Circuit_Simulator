import java.awt.Graphics;

public class Battery extends CircuitElement {
	
	double voltage;
	
	public Battery(double newVoltage, int newRotation) {
		type = "battery";
		rotation = newRotation;
		voltage = newVoltage;
	}
	
	public Battery() {
		type = "battery";
	}
	
	public void setVoltage(int newVoltage) {
		voltage = newVoltage;
	}
	
	public double getVoltage() {
		return voltage;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		if(rotation == UP) {
			g.drawLine(x + size/2, y + size, x + size/2, y + size*3/5);
			g.drawLine(x + size/2, y + size*2/5, x + size/2, y);
			g.drawLine(x + size*2/5, y + size*3/5, x + size*3/5, y + size*3/5);
			g.drawLine(x + size/3, y + size*2/5, x + size*2/3, y + size*2/5);
		}
		if(rotation == RIGHT) {
			g.drawLine(x , y + size/2, x + size*2/5, y + size/2);
			g.drawLine(x + size*3/5, y + size/2, x + size, y + size/2);
			g.drawLine(x + size*2/5, y + size*2/5, x + size*2/5, y + size*3/5);
			g.drawLine(x + size*3/5, y + size/3, x + size*3/5, y + size*2/3);
		}
		if(rotation == DOWN) {
			g.drawLine(x + size/2, y + size, x + size/2, y + size*3/5);
			g.drawLine(x + size/2, y + size*2/5, x + size/2, y);
			g.drawLine(x + size*2/5, y + size*2/5, x + size*3/5, y + size*2/5);
			g.drawLine(x + size/3, y + size*3/5, x + size*2/3, y + size*3/5);
		}
		if(rotation == LEFT) {
			g.drawLine(x , y + size/2, x + size*2/5, y + size/2);
			g.drawLine(x + size*3/5, y + size/2, x + size, y + size/2);
			g.drawLine(x + size*2/5, y + size/3, x + size*2/5, y + size*2/3);
			g.drawLine(x + size*3/5, y + size*2/5, x + size*3/5, y + size*3/5);
		}
	}
}
