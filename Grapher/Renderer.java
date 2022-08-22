package Grapher;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Renderer
extends JPanel
implements ActionListener{

	private Timer timer = new Timer(10, this);
	private Point offset;
	private double scale;
	private int screenLimit;
	private int sizeOfPinPoints = 8;
	private Manager manager;
	private ArrayList<Point> points = new ArrayList<Point>();
	private double slopeColorScaleFactor = 5;

	//mouse stuff
	private int mouseWidth = 12;
	private int mouseHeight = 36;

//CONSTRUCTOR
	public Renderer(Manager g) {
		timer.start();
		manager = g;
		this.addKeyListener(g);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
	}

////GET VALUES FROM MANAGER
	private void getValuesFromManager(){
		manager.updateCamera();
		points = manager.getPoints();
		offset = manager.getOffsets();
		scale = manager.getScale();
	}

////RENDERS GRAPHICS EVERY FRAME
	public void paintComponent(java.awt.Graphics g) {
	////GET NEW VALUES
		manager.startOfFrame();
		getValuesFromManager();

	////CLEAR SCREEN
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0- Manager.dimension/2, 0- Manager.dimension/2, manager.getWindow().getWidth(), manager.getWindow().getHeight());

	//DRAW GREY LINES
		//TODO: add ability to draw lines to infinite zoom like Desmos (basically making this a for loop depending on scale)
		if(manager.xOffset + manager.getWindow().getWidth() > manager.yOffset + manager.getWindow().getHeight()){
			screenLimit = (int) manager.xOffset + manager.getWindow().getWidth();
		}else { screenLimit = (int) manager.yOffset + manager.getWindow().getHeight(); }
		g2d.setColor(Color.darkGray);
		for(float i = 1; i < screenLimit; i += 1) {
				//sets the size of the grey lines based on scale... similar to desmos
				g2d.setStroke(new BasicStroke((float) scale / 10));

				//gray text and lines going to the right
				if((0 - offset.x + i*10) * scale  > 0 && (0 - offset.x + i*10) * scale  < manager.getWindow().getWidth()){
					g2d.drawLine((int) ((0 - offset.x + i * 10) * scale), 0, (int) ((0 - offset.x + i * 10) * scale), manager.getWindow().getHeight());
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

	////DRAW RECTANGLES UNDER CURVE
		for(int i = 0; i < manager.areaUnderCurveRecs.size(); i++){
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int)((manager.areaUnderCurveRecs.get(i).x-offset.x)*scale), (int)((manager.areaUnderCurveRecs.get(i).y*scale+offset.y)),(int)(manager.areaUnderCurveRecs.get(i).width*scale)+1, (int)(-manager.areaUnderCurveRecs.get(i).y*scale));
		}

	////DRAW RED LINES THROUGH ORIGIN
		g2d.setColor(Color.red);
		g2d.setStroke(new BasicStroke(1.2f));
		g2d.drawLine(0, (int) (offset.y), manager.getWindow().getWidth(), (int) (offset.y));
		g2d.drawLine((int) (0-offset.x*scale), 0, (int) (0-offset.x*scale), manager.getWindow().getHeight());

	////DRAW EQUATION BY CONNECTING POINTS
		for(int i = 0; i < points.size()-2; i++) {
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.pink);

			//calculates slope between two vectors
			double riseOverRun = (Math.abs(points.get(i).y- points.get(i+1).y))/Math.abs(points.get(i).x- points.get(i+1).x);

			//color coding/draw equation

				if(riseOverRun <= slopeColorScaleFactor){
					double growing = riseOverRun/slopeColorScaleFactor;
					double shrinking = 1 - growing;
					g2d.setColor(new Color(0,(int)(255 * growing),(int)(255 * shrinking)));

				}else if(riseOverRun <= slopeColorScaleFactor*2) {
					double growing = (riseOverRun-slopeColorScaleFactor)/slopeColorScaleFactor;
					double shrinking = 1 - growing;
					g2d.setColor(new Color((int) (255 * growing), (int) (255 * shrinking), 0));

				}else{
					g2d.setColor(new Color(255,0,0));
				}

				//draw the function
				g2d.setStroke(new BasicStroke(2));
				g2d.drawLine((int) points.get(i).x, (int) points.get(i).y, (int) points.get(i+1).x, (int) points.get(i+1).y);
				g2d.setStroke(new BasicStroke(1));
		}

	////DRAW MIN AND MAX
		g2d.setColor(Color.gray);
		g2d.fillOval((int)(manager.localMaxX - sizeOfPinPoints /2), (int)(manager.localMaxY - sizeOfPinPoints /2), sizeOfPinPoints, sizeOfPinPoints);
		g2d.fillOval((int)(manager.localMinX- sizeOfPinPoints /2), (int)(manager.localMinY - sizeOfPinPoints /2), sizeOfPinPoints, sizeOfPinPoints);

	//HIGHLIGHT MIN POINT WHEN HOVERED OVER
		//if within x value
		if(manager.mouseLocation.x-mouseWidth-sizeOfPinPoints/2 < (manager.localMaxX - sizeOfPinPoints /2) && manager.mouseLocation.x-mouseWidth+sizeOfPinPoints/2 > (manager.localMaxX - sizeOfPinPoints /2)){
			//if within y value of circle
			if(manager.mouseLocation.y-mouseHeight-sizeOfPinPoints/2 < (manager.localMaxY - sizeOfPinPoints /2) && manager.mouseLocation.y-mouseHeight+sizeOfPinPoints/2 > (manager.localMaxY - sizeOfPinPoints /2)){
				//could be combined with distance equation check (im lazy :P)
				g2d.setColor(Color.white);
				g2d.fillOval((int)(manager.localMaxX - sizeOfPinPoints /2), (int)(manager.localMaxY - sizeOfPinPoints /2), sizeOfPinPoints, sizeOfPinPoints);
				g2d.fillOval((int)(manager.localMinX- sizeOfPinPoints /2), (int)(manager.localMinY - sizeOfPinPoints /2), sizeOfPinPoints, sizeOfPinPoints);
				g2d.setColor(Color.white);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 22));
				g2d.drawString("("+ ((int)((manager.localMaxX+manager.xOffset*scale)*10))/10 +", "+((int)((manager.localMaxY-manager.yOffset)*10))/10 + ")", (int)manager.localMaxX +sizeOfPinPoints, (int)manager.localMaxY -sizeOfPinPoints);
			}
		}

	////DRAW ALL DATA POINTS
		for(int i=0; i < manager.data.size(); i++) {
			g2d.fillOval((int)((manager.data.get(i).x*10-offset.x)*scale)- sizeOfPinPoints /2, (int)((manager.data.get(i).y*scale*-10+offset.y))- sizeOfPinPoints /2, sizeOfPinPoints, sizeOfPinPoints);
		}

	//DRAW scaling marker
//		g2d.setColor(Color.pink);
//		g2d.fillOval((int)((0*10-offset.x)*scale)- sizeOfPinPoints /2, (int)((0*scale*-10+offset.y))- sizeOfPinPoints /2, sizeOfPinPoints, sizeOfPinPoints);
//		g2d.fillOval((int)((0*10-offset.x))- sizeOfPinPoints /2, (int)((0*-10+offset.y))- sizeOfPinPoints /2, sizeOfPinPoints, sizeOfPinPoints);
//		g2d.drawLine((int)((0*10-offset.x)*scale)- sizeOfPinPoints /2, (int)((0*scale*-10+offset.y))- sizeOfPinPoints /2,(int)((0*10-offset.x))- sizeOfPinPoints /2, (int)((0*-10+offset.y))- sizeOfPinPoints /2);
//		g2d.drawLine((int)((0*10-offset.x)*scale)- sizeOfPinPoints /2,(int)((0*10-offset.x))- sizeOfPinPoints /2,(int)((0*scale*-10+offset.y))- sizeOfPinPoints /2, (int)((0*-10+offset.y))- sizeOfPinPoints /2);

		manager.endOfFrame();
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

}
