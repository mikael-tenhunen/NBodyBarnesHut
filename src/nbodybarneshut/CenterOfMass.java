package nbodybarneshut;

import java.awt.geom.Point2D;

/**
 *
 */
public class CenterOfMass {
    private Point2D.Double position;
    private double mass;

    public CenterOfMass() {
        position = new Point2D.Double();
        mass = 0;
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
    
    public void setAsBody(Body body) {
        position.setLocation(body.getPosition().getX(), body.getPosition().getY());
        mass = body.getMass();
    }
}
