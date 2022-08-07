package Grapher;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Graphics
extends JPanel
implements ActionListener{
	private Timer t = new Timer(10, this);

	public Point offset;
	public double scale;
	private  int largerOffset;
	private Manager manager;

	private ArrayList<Point> vecs = new ArrayList<Point>();

	//graphics setup
	public Graphics(Manager g) {
		t.start();
		manager = g;
		//add a keyListener
		this.addKeyListener(g);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
	}

	//updates values for game called each frame
	public  void updateValues(){
		vecs = manager.getVectors();
		offset = manager.offsets();
		scale = manager.scale;
		manager.checkMovement();
	}

	//called each frame
	public void paintComponent(java.awt.Graphics g) {
		//pulls new values from manager
		updateValues();

		//graphics setup
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0- Manager.dimension/2, 0- Manager.dimension/2, manager.getWindow().getWidth(), manager.getWindow().getHeight());



		//draw line to connect each point to the next one
		int lenOfLine = 0;

		//helps further optimize which grey lines are calculated and shown kinda a crappy way to do it ngl)
		g2d.setColor(Color.darkGray);
		if(manager.xOffset + manager.getWindow().getWidth() > manager.yOffset + manager.getWindow().getHeight()){
			largerOffset = (int) manager.xOffset + manager.getWindow().getWidth();
		}else {
			largerOffset = (int) manager.yOffset + manager.getWindow().getHeight();
		}



		//grey grid and numbers
		for(float i = 1; i < largerOffset; i += 1) {
				//sets the size of the grey lines based on scale... similar to desmos
				g2d.setStroke(new BasicStroke((float) scale / 10));

				//gray text and lines going to the right
				if((0 - offset.x + i*10) * scale  > 0 && (0 - offset.x + i*10) * scale  < manager.getWindow().getWidth()){
					g2d.drawLine((int) ((0 - offset.x + i * 10) * scale), 0, (int) ((0 - offset.x + i * 10) * scale), manager.getWindow().getHeight());
					lenOfLine +=1;
					if(i != 0) {
						g2d.setColor(Color.gray);
						if (i % 10 == 0) {
							g2d.drawString("" + i, (int) ((0 - offset.x + i * 10) * scale), (int) ((offset.y - 3)));
						}else
						if (i % 5 == 0 && scale > 2) {
							g2d.drawString("" + i, (int) ((0 - offset.x + i * 10) * scale), (int) ((offset.y - 3)));
						}else
						if (scale > 3) {
							g2d.drawString("" + i, (int) ((0 - offset.x + i * 10) * scale), (int) ((offset.y - 3)));
						}
					}
					g2d.setColor(Color.darkGray);
				}
				//gray text and lines going to the left
				if((0 - offset.x - i*10) * scale  > 0 && (0 - offset.x - i*10) * scale  < manager.getWindow().getWidth()){
					g2d.drawLine((int) ((0 - offset.x - i * 10) * scale), 0, (int) ((0 - offset.x - i * 10) * scale), manager.getWindow().getHeight());
					lenOfLine +=1;
					if(i != 0) {
						g2d.setColor(Color.gray);
						if (i % 10 == 0) {
							g2d.drawString("" + i * -1, (int) ((0 - offset.x + i * -10) * scale), (int) ((offset.y - 3)));
						}else
						if (i % 5 == 0 && scale > 2) {
							g2d.drawString("" + i * -1, (int) ((0 - offset.x + i * -10) * scale), (int) ((offset.y - 3)));
						}else
						if (scale > 3) {
							g2d.drawString("" + i * -1, (int) ((0 - offset.x + i * -10) * scale), (int) ((offset.y - 3)));
						}

						g2d.setColor(Color.darkGray);
					}
				}
				//gray text and lines going up
				if((offset.y + i*10* scale) > 0 && (offset.y + i*10* scale)   < manager.getWindow().getHeight()) {
					g2d.drawLine(0, (int) ((offset.y + i * 10 * scale)), manager.getWindow().getWidth(), (int) ((offset.y + i * 10 * scale)));
					lenOfLine +=1;
					if(i != 0) {
						g2d.setColor(Color.gray);
						if (i % 10 == 0) {
							g2d.drawString("" + i * -1, (int) (offset.x * -1 * scale), (int) (offset.y + (i * 10 * scale)));
						}else
						if (i % 5 == 0 && scale > 2) {
							g2d.drawString("" + i * -1, (int) (offset.x * -1 * scale), (int) (offset.y + (i * 10 * scale)));
						}else
						if (scale > 3) {
							g2d.drawString("" + i * -1, (int) (offset.x * -1 * scale), (int) (offset.y + (i * 10 * scale)));
						}
						g2d.setColor(Color.darkGray);
					}
				}
				//gray text and lines going down
				if((offset.y - i*10* scale) > 0 && (offset.y - i*10* scale)   < manager.getWindow().getHeight()) {
					g2d.drawLine(0, (int) ((offset.y - i * 10 * scale)), manager.getWindow().getWidth(), (int) ((offset.y - i * 10 * scale)));
					lenOfLine +=1;

					if(i != 0){
						g2d.setColor(Color.gray);
						if(i % 10 == 0){
							g2d.drawString("" + i, (int)(offset.x*-1*scale), (int)(offset.y + (i *-1* 10*scale)));
						}else
						if(i % 5 == 0 && scale >2){
							g2d.drawString("" + i, (int)(offset.x*-1*scale), (int)(offset.y + (i *-1* 10*scale)));
						}else
						if(scale > 3){
							g2d.drawString("" + i, (int)(offset.x*-1*scale), (int)(offset.y + (i *-1* 10*scale)));
						}
						g2d.setColor(Color.darkGray);
					}

				}
		}

		//red lines at the orgin
		g2d.setColor(Color.red);
		g2d.setStroke(new BasicStroke(1.2f));
		g2d.drawLine(0, (int) (offset.y), manager.getWindow().getWidth(), (int) (offset.y));
		g2d.drawLine((int) (0-offset.x*scale), 0, (int) (0-offset.x*scale), manager.getWindow().getHeight());


					//get ave slope for color coding
					//		double sum = 0;
					//		for(int i = 0; i < vecs.size()-2; i++) {
					//			double riseOverRun = (Math.abs(vecs.get(i).y-vecs.get(i+1).y))/Math.abs(vecs.get(i).x-vecs.get(i+1).x);
					//			sum += riseOverRun;
					//		}
					//		double aveSlope = sum/vecs.size();

////DRAW AREA UNDER CURVE
		for(int x = 0; x < manager.areaUnderCurveRecs.size(); x++){
			//fill rect
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int)(manager.areaUnderCurveRecs.get(x).x - offset.x*scale),(int)(manager.areaUnderCurveRecs.get(x).y + offset.y*scale),(int)manager.areaUnderCurveRecs.get(x).width,(int)manager.areaUnderCurveRecs.get(x).height);
			//outline rect

			g2d.setColor(Color.blue);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawRect((int)(manager.areaUnderCurveRecs.get(x).x - offset.x*scale),(int)(manager.areaUnderCurveRecs.get(x).y + offset.y*scale),(int)manager.areaUnderCurveRecs.get(x).width,(int)manager.areaUnderCurveRecs.get(x).height);
		}


//draw the actual equation
		//double slopeOfYellow = manager.aveSlope;
		//System.out.println(slopeOfYellow);
		double slopeOfYellow = 5;

		for(int i = 0; i < vecs.size()-2; i++) {
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.pink);

			//calculates slope between two vectors
			double riseOverRun = (Math.abs(vecs.get(i).y-vecs.get(i+1).y))/Math.abs(vecs.get(i).x-vecs.get(i+1).x);

			//color coding/draw equation

				if(riseOverRun <= slopeOfYellow){
					double growing = riseOverRun/slopeOfYellow;
					double shrinking = 1 - growing;
					g2d.setColor(new Color(0,(int)(255 * growing),(int)(255 * shrinking)));

				}else if(riseOverRun <= slopeOfYellow*2) {
					double growing = (riseOverRun-slopeOfYellow)/slopeOfYellow;
					double shrinking = 1 - growing;
					g2d.setColor(new Color((int) (255 * growing), (int) (255 * shrinking), 0));

				}else{
					g2d.setColor(new Color(255,0,0));
				}

				//draw the function
				g2d.setStroke(new BasicStroke(5));
				g2d.drawLine((int) vecs.get(i).x, (int) vecs.get(i).y, (int) vecs.get(i+1).x, (int) vecs.get(i+1).y);
				g2d.setStroke(new BasicStroke(1));
		}





//		draw min point and if mouse hover show text
		int sizeOfPoint = 8;
		g2d.setColor(Color.gray);
		//draw max point
		for(int i=0; i < manager.data.size(); i++) {
			g2d.fillOval((int)((manager.data.get(i).x*10-offset.x)*scale)-sizeOfPoint/2, (int)((manager.data.get(i).y*scale*-10+offset.y))-sizeOfPoint/2,sizeOfPoint,sizeOfPoint);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

}
