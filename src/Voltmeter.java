import java.awt.Graphics;
import java.io.Serializable;

public class Voltmeter extends CircuitElement implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1227812265912760735L;
	double voltage;
	
	public Voltmeter(int newRotation) {
		voltage = 0;
		type = "voltmeter";
		rotation = newRotation;
	}
	
	public Voltmeter() {
		type = "voltmeter";
	}
	
	public void setVoltage(double newVoltage) {
		voltage = newVoltage;
	}
	
	public double getVoltage() {
		return voltage;
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
		g.drawString(String.valueOf((double)Math.round(voltage * 100) / 100), x + size/2 - 10, y + size/2);
		g.drawString("V", x + size/2 - 3, y + size*2/3);
		g.drawOval(x + size/4, y + size/4, size/2, size/2);
	}
}
