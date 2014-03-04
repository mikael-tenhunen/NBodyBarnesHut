package nbodybarneshut;

import java.awt.geom.Point2D;

/**
 *
 */
public class CenterOfMass {
    private Point2D.Double position;
    private double mass;
    private int nrOfBodies;

    public CenterOfMass() {
        position = new Point2D.Double();
        mass = 0;
        nrOfBodies = 0;
    }

    public CenterOfMass(Point2D.Double position, double mass) {
        this.position = position;
        this.mass = mass;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
    
    public void update(Body body) {
        nrOfBodies++;
        double weightedXOld = position.getX() * mass;
        double weightedYOld = position.getY() * mass;
        double weightedIncrementX = body.getPosition().getX() * body.getMass();
        double weightedIncrementY = body.getPosition().getY() * body.getMass();
        mass += body.getMass();
        double invertedMass = 1 / mass;
        position.setLocation((weightedXOld + weightedIncrementX) * invertedMass, 
                (weightedYOld + weightedIncrementY) * invertedMass);
    }

    public int getNrOfBodies() {
        return nrOfBodies;
    }
}
