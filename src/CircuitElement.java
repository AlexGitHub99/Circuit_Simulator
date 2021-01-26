import java.awt.Graphics;

public class CircuitElement {
	int rotation;
	String type;
	
	int UP = 0;
	int RIGHT = 1;
	int DOWN = 2;
	int LEFT = 3;
	
	public CircuitElement() {
		
	}

	public CircuitElement(int newRotation) {
		rotation = newRotation;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		
	}
	
	public void setRotation(int newRotation) {
		rotation = newRotation;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public String getType() {
		return type;
	}
}
