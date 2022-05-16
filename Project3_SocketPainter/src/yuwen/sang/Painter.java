/**
 * Yuwen Sang
 * March 13th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Painter extends JFrame implements MouseListener, MouseMotionListener, WindowListener{
	//variables
	private JFrame jf;
	private JPanel colorP;
	private JButton redButton, greenButton, blueButton;
	private JPanel shapeP;
	private JButton line, circle;
	private PaintingPanel centerCanvas;
	private JPanel textBoard;
	private JTextArea textArea;
	private JPanel inputBoard;
	private JTextArea input;
	private JButton send;
	private JButton sendIMG;
	
	private Color color = null;
	private String clr = null;
	private String shape = null;
	private Point start = null, end = null, temporaryPoint = null;
	private boolean judge = false;
	
	PaintingPrimitive oldPrimitive = null;
	PaintingPrimitive newPrimitive = null;
	PaintingPrimitive newPrimitive2 = null;
	Socket s;
	private String name;
	private Account a = new Account();//painter's local account for oos & ois
	private boolean flag= true;//to continually communicate with hub
	private boolean whetherSend = false;
	private String receivedStr;
	int windowEvent = WindowEvent.WINDOW_ACTIVATED;
	
	private static int FRAME_SIZE_X = 700;
	private static int FRAME_SIZE_Y = 600;
	
	//*******MAIN METHOD******//
	public static void main(String[] args) throws NotSerializableException {
		Painter painter = new Painter();
	}//****MAIN METHOD END*****//
	
	public Painter() {//PainterClass Constructor
		try {
			s = new Socket("localhost", 19999);
			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			a.setOOS(oos);
			a.setOIS(ois);

			//get the current painting progress from Hub
			
			ArrayList<PaintingPrimitive> fromHubPntpmts = (ArrayList<PaintingPrimitive>)a.getOIS().readObject();
			ArrayList<String> fromHubString = (ArrayList<String>)a.getOIS().readObject();
			name = JOptionPane.showInputDialog("Enter your name");
			a.getOOS().writeObject(name);//send painter's name to hub
			a.setName(name);
			System.out.println("Welcome," + name + ".");
			//Build overall Holder
			jf = new JFrame(name);
			jf.setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
			jf.setVisible(true);
			jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			//[MyActionListner] is a private class under this Painter class implementing ActionListener
			MyActionListener myListener = new MyActionListener();//5 buttons all go to this actionPerformed handler
		
			//Color Selector Buttons
			colorP = new JPanel();
			colorP.setLayout(new GridLayout(3,1));
//			colorP.setSize(100, 500);
			redButton = new JButton();
			redButton.setBackground(Color.RED);
			redButton.setOpaque(true);
			redButton.setBorderPainted(false);
			redButton.setActionCommand("r");
			redButton.addActionListener(myListener);
			colorP.add(redButton);
		
			greenButton = new JButton();
			greenButton.setBackground(Color.GREEN);
			greenButton.setOpaque(true);
			greenButton.setBorderPainted(false);
			greenButton.setActionCommand("g");
			greenButton.addActionListener(myListener);
			colorP.add(greenButton);
		
			blueButton = new JButton();
			blueButton.setBackground(Color.BLUE);
			blueButton.setOpaque(true);
			blueButton.setBorderPainted(false);
			blueButton.setActionCommand("b");
			blueButton.addActionListener(myListener);
			colorP.add(blueButton);
		
			jf.add(colorP, BorderLayout.WEST);
			//****Color Selector Button end***//
		
			//Shape Selector Buttons
			shapeP = new JPanel();
			shapeP.setLayout(new GridLayout(1,2));
			line = new JButton ("Line");
			shapeP.add(line);
			line.setActionCommand("l");
			line.addActionListener(myListener);
			circle = new JButton("Circle");
			shapeP.add(circle);
			circle.setActionCommand("c");
			circle.addActionListener(myListener);
			jf.add(shapeP, BorderLayout.NORTH);
			//****Shape Selector Button end****//
			
			//Create Center Paint on Canvas//
			centerCanvas = new PaintingPanel(fromHubPntpmts);
			jf.add(centerCanvas, BorderLayout.CENTER);
			centerCanvas.addMouseListener(this);
			centerCanvas.addMouseMotionListener(this);
			//******Create Center Paint on Canvas end******//
			
			//*****Add text chatting ****//
			textBoard = new JPanel();

			textArea = new JTextArea(50,20);
			textArea.setBackground(Color.PINK);
			textArea.setEditable(false);
			for(int i = 0; i< fromHubString.size(); i++) {
				String history = fromHubString.get(i);
				textArea.append(history);
			}
			if(fromHubString.size()!=0) {
				textArea.append("------ above are history ------\n");
			}
			textArea.append("Welcome," + name + ".\n");
			textBoard.add(textArea);
			textBoard.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			inputBoard = new JPanel(new GridLayout(1,3));
			inputBoard.setLayout(new BorderLayout());
			input = new JTextArea(5,30);//height, width
			input.setBackground(Color.LIGHT_GRAY);
			input.setText(null);
			inputBoard.add(input);
			
			send = new JButton("Send Message");
			send.setBackground(Color.YELLOW);
			send.setActionCommand("sendMSG");
			send.addActionListener(myListener);
			inputBoard.add(send);
			
			sendIMG = new JButton("Send Image");
			sendIMG.setBackground(Color.YELLOW);
			sendIMG.setActionCommand("sendIMG");
			sendIMG.addActionListener(myListener);
			inputBoard.add(sendIMG);
			
			inputBoard.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			jf.add(textBoard,BorderLayout.EAST);
			jf.add(inputBoard,BorderLayout.SOUTH);
			
			jf.addWindowListener(new WindowAdapter() {
				
				public void windowClosing (WindowEvent e) {
					try {
						windowEvent = e.getID();
						flag = false;
						String exit = "s4524gda";
						a.getOOS().writeObject(exit);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			Receiver rth = new Receiver(s,a);
			Thread th = new Thread(rth);
			th.start();	
		}catch (IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}//PainterClass Constructor end

	//Private Thread Class - this thread used to receive message/image from hub
	private class Receiver implements Runnable{//private class implements thread
		private Socket socket;
		private Account account;
		
		public Receiver(Socket socket, Account a) {
			this.socket = socket;
			this.account = a;
		}
		@Override
		public void run() {
			synchronized(this) {//race condition
				while(flag) {
					try {
						if(windowEvent == WindowEvent.WINDOW_CLOSING) {
								flag = false;
								break;
						}else{
							Object obj= account.getOIS().readObject();
							if (obj instanceof PaintingPrimitive) {
								newPrimitive2 = (PaintingPrimitive)obj;
								if(newPrimitive2 != null && !centerCanvas.getPrimitives().contains(newPrimitive2)) {
									centerCanvas.addPrimitive(newPrimitive2);
									centerCanvas.repaint();
								}
							}else if(obj instanceof String) {
								receivedStr = (String)obj;
								if(receivedStr != null) {
									textArea.append(receivedStr);
								}
							}
						}
					}catch(IOException e){
						System.out.println("You've succefully exit. Thanks for using Painter service, " + name + ".");
					}catch(ClassNotFoundException e) {
						e.printStackTrace();
					}
				
				}//while end
				//close ois, oos, and the socket
				try {
					a.getOIS().close();
					a.getOOS().close();
					s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}//synchronized
		}//run() end
	}
	
	private class MyActionListener implements ActionListener{//Private class implements ActionListener
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()){
			case "r":
				color = Color.RED;
				clr = "red ";
				break;
			case "g":
				color = Color.GREEN;
				clr = "green ";
				break;
			case "b":
				color = Color.BLUE;
				clr = "blue ";
				break;
			case "c":
				shape = "circle";
				break;
			case "l":
				shape = "line";
				break;
			case "sendMSG":
				whetherSend = true;
				String s = name + ": " + input.getText() + "\n";
				try {
					a.getOOS().writeObject(s);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				textArea.append(name + ": " + input.getText() + "\n");
				input.setText(null);
				break;
			case "sendIMG":
				if(oldPrimitive != newPrimitive) {
					try {
						String s2 = name + " drew a " + clr + shape + ".\n";
						textArea.append(s2);
						a.getOOS().writeObject(newPrimitive);
						a.getOOS().writeObject(s2);
						oldPrimitive = newPrimitive;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				break;
			}
		}
	}//private class MyActionListener end
	
	@Override
	public void mouseDragged(MouseEvent e) {//******************Bonus Point: Make painter more realistic
		if(color != null && shape != null && oldPrimitive == newPrimitive) {//protect painter without crashing//the last shape was sent
			temporaryPoint = new Point(e.getX(), e.getY());
			if(shape.equals("circle")) {
				newPrimitive = new Circle(color, start, temporaryPoint);
				if(judge == false) {
					centerCanvas.addPrimitive(newPrimitive);
					judge = true;
				}else {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				}
				centerCanvas.repaint();
			}else if(shape.equals("line")) {
				newPrimitive = new Line(color, start, temporaryPoint);
				if(judge == false) {
					centerCanvas.addPrimitive(newPrimitive);
					judge = true;
				}else {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				}
				centerCanvas.repaint();
			}
		}else if(color != null && shape != null && oldPrimitive != newPrimitive) {//the last primitive did not send to hub
			temporaryPoint = new Point(e.getX(), e.getY());
			if(shape.equals("circle")) {
				newPrimitive = new Circle(color, start, temporaryPoint);
				if(judge == false) {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
					judge = true;
				}else {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				}
				centerCanvas.repaint();
			}else if(shape.equals("line")) {
				newPrimitive = new Line(color, start, temporaryPoint);
				if(judge == false) {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
					judge = true;
				}else {
					centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				}
				centerCanvas.repaint();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(color != null && shape != null) {
			start = new Point(e.getX(), e.getY());
		}else if (color != null && shape == null){
			System.out.println("Select a shape please!!");
		}else if(color == null && shape != null) {
			System.out.println("Select a color please!!");
		}else {
			System.out.println("Select a color and a shape please!!");
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(color != null && shape != null) {//protect painter without crashing
			end = new Point(e.getX(), e.getY());
			if(shape.equals("circle")) {
				newPrimitive = new Circle(color, start, end);
				centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				judge = false;
				centerCanvas.repaint();
			}else if(shape.equals("line")) {
				newPrimitive = new Line(color, start, end);
				centerCanvas.setPrimitive(centerCanvas.getArrayListSize()-1,newPrimitive);
				judge = false;
				centerCanvas.repaint();
			}		
		}//if null end
		
	}
	

	//Unused methods
	@Override
	public void mouseMoved(MouseEvent e) {}//MouseActionListener
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	//Window Listener
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
