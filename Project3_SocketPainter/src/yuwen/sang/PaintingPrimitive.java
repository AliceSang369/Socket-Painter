/**
 * Yuwen Sang
 * March 13th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;


public abstract class PaintingPrimitive implements Serializable {
	
	private Color color;
	
	public PaintingPrimitive(Color color) {
		this.color = color;
	}
	
	public final void draw(Graphics g) {
		g.setColor(color);
		drawGeometry(g);
	}
	
	protected abstract void drawGeometry(Graphics g);
	
	public Color getColor() {
		return color;
	}

}
