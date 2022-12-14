package Grapher;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

import java.util.ArrayList; // import the ArrayList class
import java.util.Comparator;
import java.util.Scanner;
import javax.swing.*;

public class Manager


implements KeyListener, MouseListener, MouseWheelListener{
	//todo
	//show mouse location in world space when clicked down (DONE!)
	//display point location in worldspace on hover (ISH)
	//re work number line to change in size and scale infinitly (Oh Boy)
	//re work lines to change in size and scale infinity

	//setup
	private final Renderer renderer;
	private final JFrame window;
	public static final int width = 100;
	public static final int height = 100;
	public static final int dimension = 10;
	private ArrayList<Double> equationVariables = new ArrayList<>();

	public ArrayList<Point> points = new ArrayList<>();
	public ArrayList<Point> data = new ArrayList<>();
	public ArrayList<DoubleRect> areaUnderCurveRecs = new ArrayList<>();

	//for color coding
	private double aveSlope;

	//input seen in the start
	private Scanner inputReader;

	//min variables
	public double localMinX = 0;
	public double localMinY = 0;
	//max variables
	public double localMaxX = 0;
	public double localMaxY = 0;

	public Point equationMin;
	public Point equationMax;

	//CAMERA AND CAMERA MOVEMENT
	//makes zooming smoother
	private  double scaleVelocity = 0;
	private   double scale = 1;

	//used to make smooth graph movement
	private float dx;
	private float dy;

	//sets the offsets so that we start in the center of the screen
	public float xOffset = -width*dimension/2;
	public float yOffset = height*dimension/2;

	//mouse input values
	private boolean wPressed;
	private boolean aPressed;
	private boolean sPressed;
	private boolean dPressed;
	private boolean upPressed;
	private boolean downPressed;
	private boolean mousePressed;
	private boolean equationSet = false;

	//mouse location
	public Point startFrameMouseLocation = new Point(0,0);
	public Point endFrameMouseLocation = new Point(0,0);
	public Point mouseFrameChange = new Point(0,0);
	public Point mouseDistanceFromMid = new Point(0,0);
	public Point mouseLocation = new Point();
	public Point mouseLocationWS = new Point();

	//USER PREFERENCES
	private boolean calculateMaxAndMinPoints = true;
	private boolean calculateAreaUnderCurve = true;
	private boolean userInputOverride = true; //if true no prompt pops up and you can type your own equation into the 'rawEquation' function
	private boolean zoomLimit = true;
	double widthOfRectangles = 0.25;
	double xStartOfRectangles = 0;
	double xEndOfRectangles = 15;

//CONSTRUCTOR
	public Manager() {
		//setup graph and manager
		window = new JFrame();
		renderer = new Renderer(this);
		localMinX = 0;
		localMinY = -10000000;
		window.add(renderer);
		window.setTitle("The Final Grapher");
		window.setSize(width * dimension, height * dimension); //600x600
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.addMouseListener(this);
		window.addMouseWheelListener(this);
		inputReader = new Scanner(System.in);

		initiateGraph();
	}

//BASE GRAPHER FUNCTIONALITY
	//initiates the grapher based on user preferences
	private void initiateGraph(){
		if(!userInputOverride) requestInput();
		if(calculateAreaUnderCurve) areaUnderFunction(xStartOfRectangles,xEndOfRectangles,widthOfRectangles);
		System.out.println();
	}

	//allows the user to define a valid function
	private void requestInput() {
		equationVariables.clear();

	//GET ORDER OF THE POLYNOMIAL
		int order = 0;
		try {
			String orderString = JOptionPane.showInputDialog("Input Order of Equation");
			order = Integer.parseInt(orderString);
		} catch (Exception e) {
			System.out.println("ERROR. NOT A INTEGER. TRY AGAIN.");
			System.out.println(e.getCause());
			String orderString = JOptionPane.showInputDialog("Input Order of Equation");
			order = (int) Double.parseDouble(orderString);
		}
	//SET ARRAYLIST SIZE
		for (int i = 0; i < order + 1; i++) {
			equationVariables.add(0d);
		}

	//SET VALUES ACCORDING TO USER INPUT
		for (int i = 0; i < order + 1; i++) {
			try {
				String coefString = JOptionPane.showInputDialog("Input the " + (order + 1 - i) + "th" + " Coefficient");
				equationVariables.set(order - i, Double.parseDouble(coefString));
			} catch (Exception e) {
				System.out.println("ERROR     NOT A DOUBLE     TRY AGAIN.");
				System.out.println(e.getCause());
				i--;
			}

			equationSet = true;
		}

	//PRINT EQUATION
		System.out.print("Equation: ");
		for (int x = equationVariables.size(); x > 0; x--) {
			if(x != 1){
				System.out.print(equationVariables.get(x-1)+"x^" + (x-1) + " + ");
			}else{
				System.out.print(equationVariables.get(0));
			}
		}
		System.out.println();
	}

	//returns an output given a input for the function
	public double rawEquation(double input){
		double output = 0;
		if(userInputOverride){
			//type your equation here as output = 'input'
			//here are some examples
//			output = input;
			output = Math.sin(input/10)*10;
//			output = input*input;
		}else{
			for(int x = 0; x < equationVariables.size(); x++) {
				output += equationVariables.get(x) * Math.pow(input,x);
			}
		}
		return (output);
	}

	//returns value given a location on the graph takes parameters of input for function and i the location of the vector in world space
	private double calculateWorldSpaceValues(double worldSpaceXval){
		//input is scaled to improve accuracy and ability to zoom in
		worldSpaceXval = worldSpaceXval/10;
		double equation = rawEquation(worldSpaceXval);
		equation = equation * 10;

		//flips it because of how coordinate space works
		return (equation*-1);
	}

	//returns an array of points representing the function
	private void genPoints() {
		//for the full width of the screen
		points.clear();
		localMinY = 1000000000;
		localMaxY = -1000000000;
		for(int localXVal = 0; localXVal < width*dimension; localXVal++){
			//assigns a y value for every x value visible on screen relative to global positioning
			double newY = (calculateWorldSpaceValues((localXVal/scale+xOffset))*scale+yOffset);

			if(calculateMaxAndMinPoints){
				if(localMinY > newY){
					localMinY = newY;
					localMinX = localXVal;
//					equationMin = new Point(localXVal,(calculateWorldSpaceValues((localXVal/scale+xOffset))));
				}
				if(localMaxY < newY){
					localMaxY = newY;
					localMaxX = localXVal;
//					equationMax = new Point(localXVal,(calculateWorldSpaceValues((localXVal/scale+xOffset))));
				}
			}

			points.add(new Point(localXVal, newY));
		}
	}

////END OF BASE FUNCTIONALITY

/* From this point on code is more complex*/


////ADDITIONAL FEATURES
	//zooming in and out
	private void changeZoomVelocity(double scaleDx){
		//checks if we have passed the limits of scale value
		if(this.scale > 0.1f){
			scaleVelocity += scaleDx;
			for(int k = 0; k < 20; k++){
				if(this.scale > k*10){
					scaleVelocity += scaleDx;

				}
			}
		}
		//maksure scale is not negitive

	}

	private void zoomIntoGraph(double zoomSpeed){


		//get mouse lcation
		PointerInfo a = MouseInfo.getPointerInfo();
		java.awt.Point b = a.getLocation();
		mouseLocation = new Point(b.x, b.y);

		//Zoom methode 1.0 zoom straight to where mouse is
		mouseLocationWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);

		////Zoom methode 1.1
		Point mouseBeforeChangeInWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
		Point mouseChangeinWS = new Point(0,0);
		Point mouseDistanceFromCenterOfScreen = new Point(b.x-window.getWidth()/2,b.y-window.getHeight()/2);

		//How much to zoom
		scale += zoomSpeed;

		//Zoom methode 1.0 zoom straight to where mouse is
		xOffset = (float)((mouseLocationWS.x*10-width*dimension/(2*scale)));
		yOffset = (float)(mouseLocationWS.y*scale*10+height*dimension/(2));


		//zoom methode 1.1 takes the location from zoom methode 1.0 and makes it so that your mouse stays stationary when zooming
		Point mouseAfterChangeInWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
		mouseChangeinWS = new Point(mouseAfterChangeInWS.x-mouseBeforeChangeInWS.x, mouseAfterChangeInWS.y-mouseBeforeChangeInWS.y);
		xOffset -= mouseChangeinWS.x*10;
		yOffset -= mouseChangeinWS.y*scale*10;
	}

	//getting min and max
	private void printMinAndMax(){
		System.out.println("min: (" + localMinX + "," + localMinX + ")");
		System.out.println("max: (" + localMaxX + "," + localMaxY + ")");
	}
	private void areaUnderFunction(double minX, double maxX, double xInterval){
		//Clear ArrayList
		areaUnderCurveRecs.clear();
		//Define area
		double area = 0;
		//xValue of current rectangle
		double xValue = minX;

		//repeat until we reach the Max X
		while(xValue < maxX){

	//Math
			//width of rectangle
			double base = xInterval;
			//height = y value of the current x
			double height = rawEquation(xValue);

			//only add if the area is positive
			if(height > 0) {
				area += base * height;
			}

	//Draw
			//if base is less than a tenth, it wont draw. so we make it a tenth
			if(base < 0.1){
				base = 0.1;
			}

			//define rectangle
			DoubleRect rect = new DoubleRect(xValue*10,rawEquation(xValue)*-10,base*10,height*10);

			//Only add rect if it doesn't overlap with the last one
			if(areaUnderCurveRecs.size() >= 1) {
				if (rect.x - areaUnderCurveRecs.get(areaUnderCurveRecs.size() - 1).x >= 1) {
					areaUnderCurveRecs.add(rect);
				}
			}else{
				areaUnderCurveRecs.add(rect);
			}

			//Step to the right
			xValue += xInterval;
		}

		//round
		area *= 1/xInterval;
		area = Math.round(area);
		area *= xInterval;

		System.out.println("Area Under Function: " + area + "between" + xStartOfRectangles + " and " + xEndOfRectangles + "on the x-axis");
	}
	private double getQualityOfBestFit (ArrayList <Double> variables) {
		//returns avrge error for a given linear best fit line compared to the data arraylist
			// y = mx+b
			double y = 0;

			double error = 0;
			for (Point datum : data) {
				for (int x = 0; x < variables.size(); x++) {
					y += Math.pow(variables.get(x), x);
				}
				error += Math.abs(y - datum.y);
			}

			double aveError = error / data.size();
			return aveError;
		}

	//iteratively finds best M and B for linear best fit line by testing quality of every move,
	// making the best one, and reducing distance moved when the best move is to not move.
	private void getBestFitLine () {
		ArrayList<Double> variables = new ArrayList<Double>();
		double interval = 1;
		double sigFig = 1000;
		int powerOfTrend = 2;

		for (int pow = 0; pow <= powerOfTrend; pow++) {
			variables.add(1d);
		}

		double oldError = Double.POSITIVE_INFINITY;
		double error = getQualityOfBestFit(variables);
		double newError = 0;

		// y = ax3 + bx2 + cx +d
		while (interval >= 1 / sigFig) {
			break;
		}

		for (int x = 0; x < variables.size(); x++) {
			System.out.println(variables.get(x));
		}

		equationVariables = variables;
	}
	private void generateData ( int size, double variability, double m, double b, double range){
		for (int i = 0; i < size; i++) {
			double sigFig = 100;
			double minX = -range / 2;
			double maxX = range / 2;

			double x = Math.random() * (maxX - minX) + minX;
			//Rounds to 100ths
			x *= sigFig;
			x = Math.round(x);
			x /= sigFig;

			//equation
			//double y = (m*x+b);
			double y = 2 * x * x + 5 * x + 7;

			y += ((Math.random() * 2) - 1) * variability;

			//Rounds to 100ths
			y *= sigFig;
			y = Math.round(y);
			y /= sigFig;

			data.add(new Point(x, y));
		}
		data.sort(Comparator.comparing(Point::getX));
	}

	//color code the line
	private void getAveSlope(){
		if(equationSet) {
			ArrayList<Double> slopes = new ArrayList<>();
			for (int i = -(width*dimension)/2; i < (width*dimension)/2; i++) {
				double x1, y1, x2, y2;
				x1 = i;
				y1 = rawEquation(x1);

				x2 = x1 + 1;
				y2 = rawEquation(x2);

				if(y1 < height*dimension && y1 > 0)
				slopes.add((y1 - y2) / (x1 - x2));
			}
			double totalSlope = 0;
			for (int i = 1; i < slopes.size(); i++) {
				totalSlope += slopes.get(i);
			}
			aveSlope = totalSlope / (slopes.size()-1);

		}else{
			aveSlope = 0;
		}

	}
	private void getIntSlopes() {
		for (int i = 1; i < points.size(); i++) {
// Regular Slope used to get vertical asymtote
			double x1, y1, x2, y2, x3, y3, x4, y4;
			x1 = points.get(i).x;
			y1 = points.get(i).y;

			x2 = x1 + 1;
			y2 = rawEquation(x2);

			x3 = x2 + 1;
			y3 = rawEquation(x3);

			x4 = x3 + 1;
			y4 = rawEquation(x4);

			double slope1 = (y1 - y2) / (x1 - x2);
			double slope2 = (y2 - y3) / (x2 - x3);
			double slope3 = (y3 - y4) / (x3 - x4);

			if ((Math.signum(slope1) == -1 && Math.signum(slope2) == 1 && Math.signum(slope3) == -1) || (Math.signum(slope1) == 1 && Math.signum(slope2) == -1 && Math.signum(slope3) == 1)) {
				double signIfAsymtote = Math.signum(slope2);
				System.out.println("Asymtote around " + y2);
				//deepAsymtoteSearch(x2,x3,signIfAsymtote);
			}
		}
	}
//END OF EXTENSIONS


////GET FUNCTIONS (all information passed to the renderer)
	public Point getOffsets(){
		return(new Point(xOffset,yOffset));
	}
	public ArrayList<Point> getPoints() {
		genPoints();
		return(points);
	}
	public double getScale() {return scale;}
	public void updateCameraTransform(){
		if(wPressed){
			dy += (0.1f/scale + 0.01f)*scale;
		}
		if(aPressed){
			dx -= 0.2f/scale + 0.01f;
		}
		if(sPressed){
			dy -= (0.1f/scale + 0.01f)*scale;
		}
		if(dPressed){
			dx += 0.2f/scale + 0.01f;
		}
		if(upPressed){
			changeZoomVelocity(0.001f);
			//System.out.println("scale = " + scale);
		}
		if(downPressed){
			changeZoomVelocity(-0.001f);
			//System.out.println("scale = " + scale);
		}
		xOffset += dx;
		yOffset += dy;

		if(scale < 0.2f && zoomLimit) {
			scaleVelocity *= -0.001;
			scale = 0.2000001;
		}else {
			zoomIntoGraph(scaleVelocity);
			scaleVelocity *= 0.93;
		}
		dx *= 0.9;
		dy *= 0.9;

		//zooming should really be done with matrix math as shown here:
		// DEMO: https://jsfiddle.net/7ekqg8cb/


		//this is the less complicated less accurate way to do it
		//if the mouse shifts suddenly while zooming we lerp the zoom velocity to zero
		scaleVelocity *= 1/(Math.abs((mouseFrameChange.x/50)*scale/100)+1);
		scaleVelocity *= 1/(Math.abs((mouseFrameChange.y/50)*scale/100)+1);
	}
	public void startOfFrame(){
		PointerInfo a = MouseInfo.getPointerInfo();
		java.awt.Point b = a.getLocation();
		startFrameMouseLocation = new Point(b.x, b.y);

		if(mousePressed){
//			startFrameMouseLocation = new Point(b.x, b.y);
		}
	}
	public void endOfFrame(){
		PointerInfo a = MouseInfo.getPointerInfo();
		java.awt.Point b = a.getLocation();
		endFrameMouseLocation = new Point(b.x, b.y);
		mouseFrameChange = new Point(startFrameMouseLocation.x-endFrameMouseLocation.x, startFrameMouseLocation.y-endFrameMouseLocation.y);
		if(mousePressed){
//			endFrameMouseLocation = new Point(b.x, b.y);
//			System.out.println("change in mouse location per frame: (" + (mouseFrameChange.x) + "," + (mouseFrameChange.y) + ")");
			dx += mouseFrameChange.x/scale;
			dy -= mouseFrameChange.y;
		}
	}
	public JFrame getWindow() {
		return window;
	}

	//key events
	@Override
	public void keyTyped(KeyEvent e) {	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		//this is virtually the same code as the 'zoomIntoGraph' function
			if(keyCode == KeyEvent.VK_SPACE){
				//get mouse info
				PointerInfo a = MouseInfo.getPointerInfo();
				java.awt.Point b = a.getLocation();
				mouseLocation = new Point(b.x, b.y);

				//my vars
				mouseLocationWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
				Point mouseBeforeChangeInWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
				Point mouseChangeinWS = new Point(0,0);
				Point mouseDistanceFromCenterOfScreen = new Point(b.x-window.getWidth()/2,b.y-window.getHeight()/2);

				//How much to zoom
				scale *= 2;

				//Zoom methode 1.0 zoom straight to where mouse is
				xOffset = (float)((mouseLocationWS.x*10-width*dimension/(2*scale)));
				yOffset = (float)(mouseLocationWS.y*scale*10+height*dimension/(2));

				//uncomment for zoom methode 1.1 feature
//				Point mouseAfterChangeInWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
//				mouseChangeinWS = new Point(mouseAfterChangeInWS.x-mouseBeforeChangeInWS.x, mouseAfterChangeInWS.y-mouseBeforeChangeInWS.y);
//
//				xOffset -= mouseChangeinWS.x*10;
//				yOffset -= mouseChangeinWS.y*scale*10;


				//debug
//				System.out.println("Mouse change in (WS):" + mouseChangeinWS.getString());
//				System.out.println("mouse distance from center screen (LS):" + mouseDistanceFromCenterOfScreen.getString());
//				System.out.println("xoffset:" + xOffset + "-- y offset:" + yOffset );
			}


			if(keyCode == KeyEvent.VK_W) {
				wPressed = true;

			}
		
			if(keyCode == KeyEvent.VK_S) {
				sPressed = true;
			}
		
			if(keyCode == KeyEvent.VK_A) {
				aPressed = true;
			}
		
			if(keyCode == KeyEvent.VK_D) {
				dPressed = true;
			}
			if(keyCode == KeyEvent.VK_UP) {
				upPressed = true;
			}
			if(keyCode == KeyEvent.VK_DOWN) {
				downPressed = true;
			}
		if(keyCode == KeyEvent.VK_M) {
			printMinAndMax();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if(keyCode == KeyEvent.VK_W) {
			wPressed = false;
		}

		if(keyCode == KeyEvent.VK_S) {
			sPressed = false;
		}

		if(keyCode == KeyEvent.VK_A) {
			aPressed = false;
		}

		if(keyCode == KeyEvent.VK_D) {
			dPressed = false;
		}
		if(keyCode == KeyEvent.VK_UP) {
			upPressed = false;
		}
		if(keyCode == KeyEvent.VK_DOWN) {
			downPressed = false;
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		PointerInfo a = MouseInfo.getPointerInfo();
		java.awt.Point b = a.getLocation();
		mouseLocation = new Point(b.x, b.y);
		mouseLocationWS = new Point((b.x/10+xOffset*scale/10)/scale, -((b.y-26)/10-yOffset/10)/scale);
		mouseDistanceFromMid = new Point(b.x-window.getWidth()/2,b.y-window.getHeight()/2);

		System.out.println("mousePressed" + "-- worldspace location:" + mouseLocationWS.getString());
		System.out.println("mousePressed" + "distance from mid of window:" + mouseDistanceFromMid.getString());
		System.out.println("xoffset:" + xOffset + "-- y offset:" + yOffset );
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		System.out.println("mouseReleased");
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		changeZoomVelocity(notches*-0.01);
	}
}
