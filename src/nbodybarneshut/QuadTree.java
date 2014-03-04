package nbodybarneshut;

public class QuadTree {

    int nrOfBodies;
    double minX;
    double maxX;
    double minY;
    double maxY;
    double xMiddle;
    double yMiddle;
    private QuadTree SE;
    private QuadTree NW;
    private QuadTree NE;
    private QuadTree SW;
    private CenterOfMass centerOfMass;

    public QuadTree(double minX, double maxX, double minY, double maxY) {
        nrOfBodies = 0;
        this.minX = minX;
        this.maxX = maxX;
        this.minX = minY;
        this.maxY = maxY;
        xMiddle = (maxX - minX) / 2;
        yMiddle = (maxY - minY) / 2;
        NW = null;
        NE = null;
        SE = null;
        SW = null;
        centerOfMass = new CenterOfMass();
    }

    public QuadTree getNW() {
        return NW;
    }

    public void setNW(QuadTree NW) {
        this.NW = NW;
    }

    public QuadTree getNE() {
        return NE;
    }

    public void setNE(QuadTree NE) {
        this.NE = NE;
    }

    public QuadTree getSE() {
        return SE;
    }

    public void setSE(QuadTree SE) {
        this.SE = SE;
    }

    public QuadTree getSW() {
        return SW;
    }

    public void setSW(QuadTree SW) {
        this.SW = SW;
    }

    public CenterOfMass getCenterOfMass() {
        return centerOfMass;
    }

    public void setCenterOfMass(CenterOfMass centerOfMass) {
        this.centerOfMass = centerOfMass;
    }

    /**
     * insertBody is used when building the tree.
     * @param body
     */
    public void insertBody(Body body) {
        if (nrOfBodies < 1) {
            //base case
            centerOfMass.setAsBody(body);
        }
        else {
            //check which quadrant to insert into
            double x = body.getPosition().getX();
            double y = body.getPosition().getY();
            if (y <= yMiddle && x <= xMiddle) {
                //NW
                if (null == NW) {
                    NW = new QuadTree(minX, xMiddle, minY, yMiddle);
                }
                NW.insertBody(body);
            }
            else if (y <= yMiddle && x > xMiddle) {
                //NE
                if (null == NE) {
                    NE = new QuadTree(xMiddle, maxX, minY, yMiddle);
                }
                NE.insertBody(body);                
            }
            else if (y > yMiddle && x > xMiddle) {
                //SE
                if (null == SE) {
                    SE = new QuadTree(xMiddle, maxX, yMiddle, maxY);
                }
                SE.insertBody(body);
            }
            else if (y > yMiddle && x <= xMiddle) {
                //SW
                if (null == SW) {
                    SW = new QuadTree(minX, xMiddle, yMiddle, maxY);
                }
                SW.insertBody(body);
            }
        }
        nrOfBodies++;
    }

    /**
     * Recursive calculation of center of mass
     *
     * @return center of mass of this node
     */
    public CenterOfMass calculateCenterOfMass() {
        CenterOfMass center;
        double posX = 0;
        double posY = 0;
        double mass = 0;
        double children = 0;
        if (nrOfBodies == 1) {
            return centerOfMass;
        }
        else {
            if (null != NW) {
                children++;
                center = NW.calculateCenterOfMass();
                posX += center.getPosition().getX();
                posY += center.getPosition().getY();
                mass += center.getMass();
            }
            if (null != NE) {
                children++;
                center = NE.calculateCenterOfMass();
                posX += center.getPosition().getX();
                posY += center.getPosition().getY();
                mass += center.getMass();
            }
            if (null != SE) {
                children++;
                center = SE.calculateCenterOfMass();
                posX += center.getPosition().getX();
                posY += center.getPosition().getY();
                mass += center.getMass();
            }
            if (null != SW) {
                children++;
                center = SW.calculateCenterOfMass();
                posX += center.getPosition().getX();
                posY += center.getPosition().getY();
                mass += center.getMass();
            }
            posX /= children;
            posY /= children;
            centerOfMass.getPosition().setLocation(posX, posY);
            centerOfMass.setMass(mass);
            return centerOfMass;
        }
    }
}
