package nbodybarneshut;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class NBodyGraphics extends JPanel {
    NBodyBarnesHut problem;
    Body[] bodies;
    double maxDimension;
    double width;
    double height;
    double invertedMaxMass;
    ArrayList<Line2D.Double> lines;
    
    public NBodyGraphics(NBodyBarnesHut problem, double maxDimension, double width, 
            double height, double maxMass) {
        super();
        this.problem = problem;
        this.maxDimension = maxDimension;
        this.width = width;
        this.height = height;
        invertedMaxMass = 1 / maxMass;
        bodies = problem.getBodies();
        lines = new ArrayList();
    }
    
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        g.setColor(Color.white);
        bodies = problem.getBodies();
        int size;
        int halfSize;
        for (Body body : bodies) {
            size = convertMass(body.getMass());
            halfSize = size / 2;
            g.drawOval(convertXCoord(body.getPosition().getX()) - halfSize, 
                   convertYCoord(body.getPosition().getY()) - halfSize, 
                   size, size);
//            System.out.println("x: " + convertXCoord(body.getPosition().getX()) + 
//                    "\ny: " + convertYCoord(body.getPosition().getY()));
//            System.out.println("x: " + body.getPosition().getX() + 
//                    "\ny: " + body.getPosition().getY());
        }
        g.setColor(Color.green);
        for (int i = 0; i < lines.size(); i++) {
            g.draw(lines.get(i));
        }
    }  
    
    public int convertXCoord(double coord) {
        return (int) ((coord / maxDimension) * width);
    }
    
    public int convertYCoord(double coord) {
        return (int) ((coord / maxDimension) * height);
    }    
    
    public int convertMass(double mass) {
        return (int) (mass * 10 * invertedMaxMass) + 1;
    }
    
    public void addLines(double minX, double maxX, double minY, double maxY) {
        minX = convertDoubleXCoord(minX);
        maxX = convertDoubleXCoord(maxX);
        minY = convertDoubleYCoord(minY);
        maxY = convertDoubleYCoord(maxY);
        lines.add(new Line2D.Double(minX, minY, maxX, minY));
        lines.add(new Line2D.Double(minX, minY, minX, maxY));
        lines.add(new Line2D.Double(minX, maxY, maxX, maxY));
        lines.add(new Line2D.Double(maxX, minY, maxX, maxY));
    }
    
    public double convertDoubleXCoord(double coord) {
        return ((coord / maxDimension) * width);
    }
    
    public double convertDoubleYCoord(double coord) {
        return ((coord / maxDimension) * height);
    }    
}
