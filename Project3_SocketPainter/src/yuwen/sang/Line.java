/**
 * Yuwen Sang
 * March 13th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class Line extends PaintingPrimitive {

	private Point start, end;

	
	public Line(Color color, Point start, Point end) {
		super(color);
		this.start = start;
		this.end = end;
	}

	public Point getStart() {
		return start;
	}
	public Point getEnd() {
		return end;
	}
	@Override
	public void drawGeometry(Graphics g) {
//		System.out.println("drawn line");
		g.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);
		
	}

}
