import java.awt.Graphics;
import java.io.Serializable;

public class Wire extends CircuitElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1989770547772285054L;
	String shape;
	
	public Wire(String newShape, int newRotation) {
		type = "wire";
		rotation = newRotation;
		shape = newShape;
	}
	
	public Wire() {
		type = "wire";
	}
	
	public void setShape(String newShape) {
		shape = newShape;
	}
	
	public String getShape() {
		return shape;
	}
	
	public void customPaint(Graphics g, int x, int y, int size) {
		if(shape.equals("line")) {
			if(rotation == UP) {
				g.drawLine(x + size/2, y, x + size/2, y + size);
			}
			if(rotation == RIGHT) {
				g.drawLine(x, y + size/2, x + size, y + size/2);
			}
			if(rotation == DOWN) {
				g.drawLine(x + size/2, y, x + size/2, y + size);
			}
			if(rotation == LEFT) {
				g.drawLine(x, y + size/2, x + size, y + size/2);
			}
		}
		if(shape.equals("L")) {
			if(rotation == UP) {
				g.drawLine(x, y + size/2, x + size/2, y + size/2);
				g.drawLine(x + size/2, y + size/2, x + size/2, y);
			}
			if(rotation == RIGHT) {
				g.drawLine(x + size/2, y, x + size/2, y + size/2);
				g.drawLine(x + size/2, y + size/2, x + size, y + size/2);
			}
			if(rotation == DOWN) {
				g.drawLine(x + size, y + size/2, x + size/2, y + size/2);
				g.drawLine(x + size/2, y + size/2, x + size/2, y + size);
			}
			if(rotation == LEFT) {
				g.drawLine(x + size/2, y + size, x + size/2, y + size/2);
				g.drawLine(x + size/2, y + size/2, x, y + size/2);
			}
		}
		if(shape.equals("T")) {
			if(rotation == UP) {
				g.drawLine(x + size/2, y, x + size/2, y + size);
				g.drawLine(x + size/2, y + size/2, x, y + size/2);
			}
			if(rotation == RIGHT) {
				g.drawLine(x, y + size/2, x + size, y + size/2);
				g.drawLine(x + size/2, y + size/2, x + size/2, y);
			}
			if(rotation == DOWN) {
				g.drawLine(x + size/2, y, x + size/2, y + size);
				g.drawLine(x + size/2, y + size/2, x + size, y + size/2);
			}
			if(rotation == LEFT) {
				g.drawLine(x, y + size/2, x + size, y + size/2);
				g.drawLine(x + size/2, y + size/2, x + size/2, y + size);
			}
		}
	}
}
