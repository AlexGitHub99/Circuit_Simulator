import java.awt.Font;
import java.awt.Graphics;
import java.text.AttributedString;

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
	
	public void setVoltage(double newVoltage) {
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
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 + 1));
			g.drawString("+", x + size/2 + 5, y + size/2 - 10);
			g.drawString("-", x + size/2 + 5, y + size*2/3 + 10);
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(voltage * 100) / 100), x + size*2/3 + 5, y + size*3/5);
			g.setFont(prevFont);
		}
		if(rotation == RIGHT) {
			g.drawLine(x , y + size/2, x + size*2/5, y + size/2);
			g.drawLine(x + size*3/5, y + size/2, x + size, y + size/2);
			g.drawLine(x + size*2/5, y + size*2/5, x + size*2/5, y + size*3/5);
			g.drawLine(x + size*3/5, y + size/3, x + size*3/5, y + size*2/3);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 + 1));
			g.drawString("+", x + size/2 + 13, y + size/2 + 15);
			g.drawString("-", x + size/3 - 5, y + size/2 + 12);
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(voltage * 100) / 100), x + size*2/5, y + size*4/5 + 8);
			g.setFont(prevFont);
		}
		if(rotation == DOWN) {
			g.drawLine(x + size/2, y + size, x + size/2, y + size*3/5);
			g.drawLine(x + size/2, y + size*2/5, x + size/2, y);
			g.drawLine(x + size*2/5, y + size*2/5, x + size*3/5, y + size*2/5);
			g.drawLine(x + size/3, y + size*3/5, x + size*2/3, y + size*3/5);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 + 1));
			g.drawString("-", x + size/2 + 5, y + size/2 - 10);
			g.drawString("+", x + size/2 + 5, y + size*2/3 + 10);
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(voltage * 100) / 100), x + size*2/3 + 5, y + size*3/5);
			g.setFont(prevFont);
		}
		if(rotation == LEFT) {
			g.drawLine(x , y + size/2, x + size*2/5, y + size/2);
			g.drawLine(x + size*3/5, y + size/2, x + size, y + size/2);
			g.drawLine(x + size*2/5, y + size/3, x + size*2/5, y + size*2/3);
			g.drawLine(x + size*3/5, y + size*2/5, x + size*3/5, y + size*3/5);
			Font prevFont = g.getFont();
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 + 1));
			g.drawString("-", x + size/2 + 15, y + size/2 + 12);
			g.drawString("+", x + size/3 - 5, y + size/2 + 15);
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, size/6 - 1));
			g.drawString(String.valueOf((double)Math.round(voltage * 100) / 100), x + size*2/5, y + size*4/5 + 8);
			g.setFont(prevFont);
		}
	}
}
