import java.awt.Graphics;

public class CircuitElement {
	int rotation;
	String type;
	
	int UP = 0;
	int RIGHT = 1;
	int DOWN = 2;
	int LEFT = 3;
	
	int[][] connections;
	
	public CircuitElement() {
		connections = new int[4][2];
		for(int i = 0; i < connections.length; i++) {
			connections[i] = null;
		}
	}

	public CircuitElement(int newRotation) {
		rotation = newRotation;
		connections = new int[4][2];
		for(int i = 0; i < connections.length; i++) {
			connections[i] = null;
		}
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		
	}
	
	public void setRotation(int newRotation) {
		rotation = newRotation;
	}
	
	public void setConnection(int direction, int[] xy) {
		connections[direction] = xy;
	}
	
	public int[][] getConnections() {
		return connections;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public String getType() {
		return type;
	}
}
