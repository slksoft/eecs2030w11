import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class W11 extends JFrame implements ActionListener, KeyListener {
	
	public static final String title = "W11";
	public static final String pCommand = "+";
	public static final String nCommand = "-";
	public static final int top = 200;
	public static final int left = 400;
	public static final int width = 1600;
	public static final int height = 800;
	public static final int gameWidth = 1541;
	public static final int gameHeight = 701;
	public static final int tick = 25;
	public static final int ballSize = 10;
	public static final int ballCount = 200;
	public static final double friction = 0.9;
	
	private double gForce = 0;
	private int time = 0;
	private W11Panel w11panel;
	private ArrayList<W11Ball> w11balls = new ArrayList<W11Ball>();
	
	public static void main(String[] args) {
		new W11();
	}
	
	public W11() {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(width, height));
		setLayout(new BorderLayout());
		setUI();
		addActors();
		pack();
		this.setLocation(new Point(left, top));
		setVisible(true);		
	}
	
	public void setUI(){
		w11panel = new W11Panel(); 
		JPanel panel = new JPanel();
		JButton button1 = new JButton("-");
		button1.setActionCommand(nCommand);
		button1.addActionListener(this);
		JButton button2 = new JButton("+");
		button2.setActionCommand(pCommand);
		button2.addActionListener(this);
		panel.add(button1);
		panel.add(button2);
		addKeyListener(this);
		button1.addKeyListener(this);
		button2.addKeyListener(this);
		add(BorderLayout.SOUTH, panel);
		add(BorderLayout.CENTER, w11panel);
	}

	public void addActors() {
		Runnable ball1 = new Runnable() {		
			@Override
			public void run() {
				while(true) {
					time++;
					collisions();
					update();
					try {
						Thread.sleep(tick);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread1 = new Thread(ball1);
        thread1.start();
        for(int n = 0; n < ballCount; n++) {
        	W11Ball w11ball = new W11Ball(n * 2 * ballSize, n * 2 * ballSize);
        	Thread thread2 = new Thread(w11ball);
        	w11balls.add(w11ball);
        	thread2.start();
        }
	}

	public void collisions() {
        for(int n1 = 0; n1 < w11balls.size(); n1++) {
            W11Ball ball1 = w11balls.get(n1);
            for(int n2 = n1 + 1; n2 < w11balls.size(); n2++) {
                W11Ball ball2 = w11balls.get(n2);
            	int xDiff = (int) ball1.x - (int) ball2.x;
            	int yDiff = (int) ball1.y - (int) ball2.y;
            	if (xDiff > -ballSize && xDiff < ballSize && yDiff > -ballSize && yDiff < ballSize) {
                	if (ball1.xSpeed < 0 && ball2.xSpeed > 0 || ball1.xSpeed > 0 && ball2.xSpeed < 0) {
                		ball1.xSpeed = - ball1.xSpeed;
                		ball2.xSpeed = - ball2.xSpeed;
                	}
                	if (ball1.ySpeed < 0 && ball2.ySpeed > 0 || ball1.ySpeed > 0 && ball2.ySpeed < 0) {
                		ball1.ySpeed = - ball1.ySpeed;
                		ball2.ySpeed = - ball2.ySpeed;
                	}
            	}
            }        	
        }	
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals(nCommand)) {
			gForce -= 0.1;
			update();
			
		}
		else if (command.equals(pCommand)) {
			gForce += 0.1;
			update();
		}
	}

	public void update() {
		setTitle(title + " G=" + String.format("%3.1f", gForce) + " T=" + time);
		repaint();		
	}
	
	@Override
	public void keyPressed(KeyEvent event) { }

	@Override
	public void keyReleased(KeyEvent event) { }

	@Override
	public void keyTyped(KeyEvent event) {
       char c = event.getKeyChar();
		if (c == nCommand.charAt(0)) {
			gForce -= 0.1;
			update();
		}
		else if (c == pCommand.charAt(0)) {
			gForce += 0.1;
			update();
		}        
	}
	
	public class W11Panel extends JPanel {
		public void paintComponent(Graphics g) {
	        g.clearRect(0, 0, getWidth(), getHeight());
	        g.drawRect(0, 0, gameWidth + ballSize, gameHeight + ballSize);
	        for(W11Ball ball : w11balls) {
	        	g.drawRect((int) ball.x, (int) ball.y, ballSize, ballSize);
	        }
	        //g.drawString("the key that pressed is " + gForce, 250, 250);
	    }		
	}

	public class W11Ball implements Runnable {		
		public double x = 100;
		public double y = 100;
		public double xSpeed = 2;
		public double ySpeed = 2;
		
		public W11Ball(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void run() {
			while(true) {
				ySpeed += gForce;
				x += xSpeed;
				if (x > gameWidth) {
					xSpeed = - xSpeed;
					ySpeed = friction * ySpeed;
					x = gameWidth - (x - gameWidth);
				}
				if (x < 0) {
					xSpeed = -xSpeed;
					ySpeed = friction * ySpeed;
					x = -x;
				}
				y += ySpeed;
				if (y > gameHeight) {
					ySpeed = -friction * ySpeed;
					y = gameHeight - (y - gameHeight);
				}
				if (y < 0) {
					ySpeed = -friction * ySpeed;
					y = -y;
				}
				try {
					Thread.sleep(tick);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

}
