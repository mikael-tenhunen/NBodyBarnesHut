package nbodybarneshut;

public class QuadTree {
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
    private final CenterOfMass centerOfMass;
    private Body body;

    public QuadTree(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minX = minY;
        this.maxY = maxY;
        xMiddle = ((maxX - minX) / 2) + minX;
        yMiddle = ((maxY - minY) / 2) + minY;
        NW = null;
        NE = null;
        SE = null;
        SW = null;
        centerOfMass = new CenterOfMass();
        body = null;
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

    /**
     * insertBody is used when building the tree.
     * @param bodyToInsert
     */
    public void insertBody(Body bodyToInsert) {
        centerOfMass.update(bodyToInsert);
        if (centerOfMass.getNrOfBodies() == 1) {
            //base case
//            System.out.println("base case reached by body id " + bodyToInsert);
            this.body = bodyToInsert;
        }
        else {
            //propagate the body that already was here first
            if (null != this.body) {
                //this was a leaf node, but should not be anymore
//                System.out.println("kicking body " + this.body + " the fuck out");
                propagateBody(this.body);
                this.body = null;
            }
            //propagate the new body
//            System.out.println("propagating body " + bodyToInsert);
            propagateBody(bodyToInsert);
        }
    }

    private void propagateBody(Body body) {
            //check which quadrant to insert into
            double x = body.getPosition().getX();
            double y = body.getPosition().getY();
            
            if ((y <= minY || y > maxY || x <= minX || x > maxX)) {
                //body is outside of the root, ignore it this time step!
                return;
            }
            else if (y <= yMiddle && x <= xMiddle) {
                //NW
//                System.out.println(body + " put in NW");
                if (null == NW) {
                    NW = new QuadTree(minX, xMiddle, minY, yMiddle);
                }
                NW.insertBody(body);
            }
            else if (y <= yMiddle && x > xMiddle) {
                //NE
//                System.out.println(body + " put in NE");
                if (null == NE) {
                    NE = new QuadTree(xMiddle, maxX, minY, yMiddle);
                }
                NE.insertBody(body);                
            }
            else if (y > yMiddle && x > xMiddle) {
                //SE
//                System.out.println(body + " put in SE");
                if (null == SE) {
                    SE = new QuadTree(xMiddle, maxX, yMiddle, maxY);
                }
                SE.insertBody(body);
            }
            else if (y > yMiddle && x <= xMiddle) {
                //SW
//                System.out.println(body + " put in SW");
                if (null == SW) {
                    SW = new QuadTree(minX, xMiddle, yMiddle, maxY);
                }
                SW.insertBody(body);
            }
    }
}
