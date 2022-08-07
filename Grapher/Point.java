package Grapher;

public class Point {
    // Members
    public double x;
    public double y;

    // Constructors
    public Point() {
        this.x = 0.0;
        this.y = 0.0;
    }

    //vector
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
}
