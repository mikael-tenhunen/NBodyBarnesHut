package nbodybarneshut;

import java.awt.geom.Point2D;

/**
 *
 */
public class Body {
    private Point2D.Double position;
    private Point2D.Double velocity;
    private double mass;

    public Body(Point2D.Double position, Point2D.Double velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
//        this.force = new Point2D.Double(0, 0);
        this.mass = mass;
    }

    
    
    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public Point2D.Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D.Double velocity) {
        this.velocity = velocity;
    }

//    public Point2D.Double getForce() {
//        return force;
//    }
//
//    public void setForce(Point2D.Double force) {
//        this.force = force;
//    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}
