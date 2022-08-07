package test;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

import java.util.ArrayList; // import the ArrayList class
import java.util.Comparator;
import java.util.Scanner;
import javax.swing.*;

public class Manager


implements KeyListener{
	//setup
	private final Graphics graphics;
	private final JFrame window;
	public static final int width = 100;
	public static final int height = 30;
	public static final int dimension = 20;

	//creates an array the size of the screen (should convert to Array List)
	public ArrayList<Point> vectors = new ArrayList<>();
	public ArrayList<Point> data = new ArrayList<>();
	public ArrayList<Double> equationVariables = new ArrayList<>();
	public ArrayList<DoubleRect> areaUnderCurveRecs = new ArrayList<>();

	//for color coding
	public double aveSlope;

	//input seen in the start
	public Scanner inputReader;

	//min variables
	public double minX = 0;
	public double minY = 0;
	public Point thelargestMin = new Point(minX,minY);

	//max variables
	public double maxX = 0;
	public double maxY = 0;
	public Point thelargestMax = new Point(maxX,maxY);

	//stores a min
	public Point accurateMin = new Point(0,0);
	//stores a max
	public Point accurateMax = new Point(0,0);


	//CAMERA AND CAMERA MOVEMENT
	//makes zooming smoother
	private  double scaleVelocity = 0;
	public   double scale = 1;

	//used to make smooth graph movement
	public float dx;
	public float dy;

	//sets the offsets so that we start in the center of the screen
	public float xOffset = -width*dimension/2;
	public float yOffset = height*dimension/2;

	//mouse input values
	public boolean wPressed;
	public boolean aPressed;
	public boolean sPressed;
	public boolean dPressed;
	public boolean upPressed;
	public boolean downPressed;
	public boolean equationSet = false;

	//mouse location
	public Point mouseLocation = new Point(0,0);

//CONSTRUCTOR
	public Manager() {
		//setup
		window = new JFrame();
		graphics = new Graphics(this);
		minX = 0;
		minY = -10000000;
		window.add(graphics);
		window.setTitle("The Final Grapher");
		window.setSize(width * dimension, height * dimension); //600x600
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputReader = new Scanner(System.in);

		//first function calls
		requestInput();
		initiateGrapher();
	}

//BASE GRAPHER FUNCTIONALITY
	//initates the grapher based on user prefrences
	private void initiateGrapher(){
		double widthOfRectangles = 1;
		double minX = 0;
		double maxX = 10;

		areaUnderCurve(minX,maxX,widthOfRectangles);
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
		for(int x = 0; x < equationVariables.size(); x++) {
			output += equationVariables.get(x) * Math.pow(input,x);
		}
		return (output);
	}

	//returns value given a location on the graph takes parameters of input for function and i the location of the vector in world space
	public double calculateWorldSpaceValues(double input, int i){
		//input is scaled to improve accuracy and ability to zoom in
		input = input/10;
		double equation = rawEquation(input);
		equation = equation * 10;

		//checks if the new answer returned in actual equation is smaller than previous answers
		if(equation/10 < minY){
			minX = (i-width*dimension/2)/10;
			thelargestMin.x = minX;
			minY = equation/10;
			thelargestMin.y = minY;
		}
		if(equation/10 > maxY){
			maxX = (i-width*dimension/2)/10;
			thelargestMax.x = maxX;
			maxY = equation/10;
			thelargestMax.y = maxY;
		}

		//flips it because of how coordinate space works
		return (equation*-1);
	}
//END OF BASE GRAPHER FUNCTIONALITY

//EXTENSIONS OF BASE GRAPHER
	//calculates the area under a curve
	public void areaUnderCurve(double minX, double maxX, double xInterval){
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

		System.out.println("Area Under Curve: " + area );
	}

	//returns avrge error for a given linear best fit line compared to the data arraylist
	public double getQualityOfBestFit (ArrayList <Double> variables) {
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
	public void getBestFitLine () {
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

		public void generateData ( int size, double variability, double m, double b, double range){
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

	//used for color coding in graphics
	public void getAveSlope(){
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


	//Slopes between every integer point
	public void getIntSlopes() {
		for (int i = 1; i < vectors.size(); i++) {
// Regular Slope used to get vertical asymtote
			double x1, y1, x2, y2, x3, y3, x4, y4;
			x1 = vectors.get(i).x;
			y1 = vectors.get(i).y;

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
//END OF EXTENSIONS TO BASE GRAPHER




//GRAPHICS STUFF
	public Point offsets(){
		return(new Point(xOffset,yOffset));
	}

	public ArrayList<Point> genVectors() {
		//for the full width of the screen
		vectors.clear();

		for(int i = 0; i < width*dimension; i++){
			vectors.add(new Point(i, (calculateWorldSpaceValues((i/scale+xOffset), i)*scale+yOffset)));
		}
		return (vectors);
	}
	public ArrayList<Point> getVectors() {

		return(genVectors());
	}
	//called when a zooming in or out occurs
	public void scale(double scale){
		//checks if we have passed the limits of scale value
		if(this.scale > 0.1f){
			scaleVelocity += scale;
			for(int k = 0; k < 20; k++){
				if(this.scale > k*10){
					scaleVelocity += scale;
				}
			}
		}
	}

	//physics based movments for easy glide graph motions
	public void checkMovement(){
		if(wPressed){
			dy += 0.1f/scale + 0.01f;
		}
		if(aPressed){
			dx -= 0.2f/scale + 0.01f;
		}
		if(sPressed){
			dy -= 0.1f/scale + 0.01f;
		}
		if(dPressed){
			dx += 0.2f/scale + 0.01f;
		}
		if(upPressed){
			scale(0.001f);
			//System.out.println("scale = " + scale);
		}
		if(downPressed){
			scale(-0.001f);
			//System.out.println("scale = " + scale);
		}
		xOffset += dx;
		yOffset += dy;

		if(scale < 0.3f) {
			scaleVelocity = 0;
			scale = 0.3000001f;
		}else {
			scale += scaleVelocity;
			scaleVelocity *= 0.95;
		}

		//mouse stuff
		PointerInfo a = MouseInfo.getPointerInfo();
		java.awt.Point b = a.getLocation();
		mouseLocation.x = b.getX();
		mouseLocation.y = b.getY();

		dx *= 0.9;
		dy *= 0.9;
	}

	@Override
	public void keyTyped(KeyEvent e) {	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

			if(keyCode == KeyEvent.VK_SPACE){
				System.out.println("Space");
				genVectors();
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

	public JFrame getWindow() {
		return window;
	}
}
