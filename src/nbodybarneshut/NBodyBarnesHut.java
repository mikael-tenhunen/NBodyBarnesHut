package nbodybarneshut;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 * Optional command-line arguments: 1. number of bodies 2. number of time steps
 * 3. number of threads 4. min mass of bodies 5. max mass of bodies 6. max
 * starting velocity component of bodies
 */
public class NBodyBarnesHut {

    public static final double G = 6.67384E-11;
    public static final double softening = 3E8;    //to soften forces
    public static final double timeStep = 5E2;
    public final double threshold;
    int n;
    int timeSteps;
    int procs;
    private final double maxDimension;
    private final double aspectRatio;
    Point2D.Double[] forces;
    private final Body[] bodies;
    private QuadTree quadTree;
    private boolean quadTreeFresh;

    public NBodyBarnesHut(int n, int timeSteps, int procs, Body[] bodies, 
            double maxDimension, double aspectRatio, double threshold) {
        this.maxDimension = maxDimension;
        this.aspectRatio = aspectRatio;
        this.n = n;
        this.threshold = threshold;
        this.timeSteps = timeSteps;
        this.procs = procs;
        this.bodies = bodies;
        forces = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            forces[i] = new Point2D.Double(0, 0);
        }
    }
    
    void setQuadTreeFresh(boolean b) {
        quadTreeFresh = b;
    }    

    void populateTree(int workerNr) {
        if(!quadTreeFresh) {
            quadTree = new QuadTree(0, maxDimension * aspectRatio, 0, maxDimension);
            quadTreeFresh = true;
        }
        //each worker populates the tree with bodies from its stripes
        for (int i = workerNr; i < n; i += procs) {
            quadTree.insertBody(bodies[i]);
        }
    }
    
    public void calculateForces(int workerNr) {
        Body body;
        Point2D.Double force;
        double comparisons = 0; //to be able to tell percentage approximations
        for (int i = workerNr; i < n; i += procs) {
            body = bodies[i];
            force = forces[i];
            comparisons = quadTree.calculateForce(body, threshold, force);
//            System.out.println("percentage comparisons: " + (1-(comparisons/n)));
        }
    }

    void moveBodies(int workerNr) {
        Point2D.Double deltav = new Point2D.Double();
        Point2D.Double deltap = new Point2D.Double();
        Point2D.Double velocity;
        Point2D.Double position;
        Body currBody;
        double timeStepByMass;

        for (int i = workerNr; i < n; i += procs) {
            //move bodies
            currBody = bodies[i];
            //Strength reduction with timeStep/Mass
            timeStepByMass = timeStep / currBody.getMass();
            deltav.setLocation(forces[i].getX() * timeStepByMass,
                    forces[i].getY() * timeStepByMass);    
            //Strength reduction "*0.5" instead of division by 2
            deltap.setLocation((currBody.getVelocity().getX() + deltav.getX() * 0.5) * timeStep,
                    (currBody.getVelocity().getY() + deltav.getY() * 0.5) * timeStep);
            velocity = currBody.getVelocity();
            velocity.setLocation(velocity.getX() + deltav.getX(),
                    velocity.getY() + deltav.getY());
            position = currBody.getPosition();
            position.setLocation(position.getX() + deltap.getX(),
                    position.getY() + deltap.getY());
            forces[i].setLocation(0, 0);
        }
    }

    public Body[] getBodies() {
        return bodies;
    }

    /**
     * @param args the command line arguments 1. number of bodies 2. number of
     * time steps 3. threshold value for Barnes-Hut 4. number of threads 
     * 5. whether graphics should be displayed
     */
    public static void main(String[] args) throws InterruptedException {
        int n = 120;
        int timeSteps = 150000;
//        int timeSteps = 1;
        int procs = 1;
        double minMass = 1E5;
        double maxMass = 1E8;
        double maxStartVelComponent = 0.00;
        double maxDimension = 100000;
        double initAreaFactor = 1;
        double threshold = 1.5;
        //height is screen height for graphical interface
        double height = 800;
        double aspectRatio = 1;
        long startTime;
        long endTime;
        boolean graphicalInterface = false;
        //read command-line arguments
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            timeSteps = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            threshold = Double.parseDouble(args[2]);
        }
        if (args.length > 3) {
            procs = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            if (args[4].equals("yes") || args[4].equals("y"))
                graphicalInterface = true;
        }        
        
        //initialize bodies
        Body[] bodies = new Body[n];
        double posX, posY, velX, velY, mass;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            posX = (random.nextDouble() * maxDimension * aspectRatio * initAreaFactor)
                    + (maxDimension - maxDimension * initAreaFactor) * 0.5;
            posY = (random.nextDouble() * maxDimension * initAreaFactor) 
                    + (maxDimension - maxDimension * initAreaFactor) * 0.5;
            velX = random.nextDouble() * maxStartVelComponent;
            velX -= maxStartVelComponent * 0.5;
            velY = random.nextDouble() * maxStartVelComponent;
            velY -= maxStartVelComponent * 0.5;
            mass = random.nextDouble() * (maxMass - minMass) + minMass;
            bodies[i] = new Body(new Point2D.Double(posX, posY),
                    new Point2D.Double(velX, velY), mass);
        }
        //initialize object representing n-body problem
        NBodyBarnesHut nBodyProblem = new NBodyBarnesHut(n, timeSteps, procs, bodies, 
        maxDimension, aspectRatio, threshold);
        //show parameters
        System.out.println("n: " + n);
        System.out.println("ticks (at " + NBodyBarnesHut.timeStep + "): " + timeSteps);
        System.out.println("workers: " + procs);
        System.out.println("far: " + threshold);
        //initiate graphics
        NBodyGraphics graphics = null;
        if (graphicalInterface) {
            double width = height * aspectRatio;
            JFrame frame = new JFrame();
            graphics = new NBodyGraphics(nBodyProblem, maxDimension,
                    width, height, maxMass);
            frame.setPreferredSize(new Dimension((int) width, (int) height));
            frame.setSize(new Dimension((int) width, (int) height));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            graphics.setBackground(Color.black);
            frame.add(graphics, BorderLayout.CENTER);
            frame.setVisible(true);
            graphics.repaint();
        }
        //thread control
        CyclicBarrier barrier = new CyclicBarrier(procs);
        ExecutorService executor = Executors.newFixedThreadPool(procs);
        //start simulation
        startTime = System.nanoTime();
        for (int i = 0; i < procs; i++) {
            executor.execute(new Worker(i, nBodyProblem, barrier, timeSteps, graphics));
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.DAYS);
        endTime = System.nanoTime();
        System.out.println("Time: " + (endTime - startTime) * 10E-10 + " seconds");
    }
}
