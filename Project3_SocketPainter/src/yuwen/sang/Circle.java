/**
 * Yuwen Sang
 * March 13th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Circle extends PaintingPrimitive{

	private Point center, radiusPoint;
	private Color color;
	
	public Circle(Color color, Point center, Point radiusPoint) {
		super(color);
		this.center = center;
		this.radiusPoint = radiusPoint;
	}
	
	public Point getCenter() {
		return center;
	}
	
	
	public Point getRadiusPoint() {
		return radiusPoint;
	}

	@Override
	public void drawGeometry(Graphics g) {
//		System.out.println("drawn circle");
		int radius = (int)Math.abs(center.distance(radiusPoint));
		g.drawOval(center.x - radius, center.y-radius, radius*2, radius*2);
		
	}

}
