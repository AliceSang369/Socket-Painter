/**
 * Yuwen Sang
 * March 13th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

public class PaintingPanel extends JPanel{// implements MouseListener{
	
	private ArrayList<PaintingPrimitive> pntpmts;
	
	public PaintingPanel() {//PaintingPanel Constructor
		pntpmts = new ArrayList<PaintingPrimitive>(); 
		setBackground(Color.WHITE);//default background color
	}
	
	public PaintingPanel(ArrayList<PaintingPrimitive> pntpmts) {
		this.pntpmts = pntpmts;
		setBackground(Color.WHITE);//default background color
	}
	
	public void paintComponent(Graphics g) {//override paintComponent from JPanel
		super.paintComponent(g);
		for(int i = 0; i < pntpmts.size(); i++) {
			PaintingPrimitive obj = pntpmts.get(i);
			obj.draw(g);
		}
	}

	public void addPrimitive(PaintingPrimitive obj) {//add PaintingPrimitive object
		pntpmts.add(obj);
	}

	public void setPrimitive(int index, PaintingPrimitive obj) {
		pntpmts.set(index,  obj);
	}
	public int getArrayListSize() {
		return pntpmts.size();
	}
	public PaintingPrimitive getPrimitive(int index) {
		return pntpmts.get(index);
	}
	
	public ArrayList<PaintingPrimitive> getPrimitives(){//Get current primitive ArrayList
		return pntpmts;
	}

	
}
