/**
 * Yuwen Sang
 * March 15th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Account {

	private String name;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public void setName(String name) {
		this.name = name;
//		System.out.println("SetName to" + name);
	}
	
	public String getName() {
		return name;
	}
	//oos
	public void setOOS(ObjectOutputStream oos) {
		this.oos = oos;
	}
	public ObjectOutputStream getOOS() {
		return oos;
	}
	//ois
	public void setOIS(ObjectInputStream ois) {
		this.ois = ois;
	}
	public ObjectInputStream getOIS() {
		return ois;
	}

}
