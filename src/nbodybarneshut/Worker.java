package nbodybarneshut;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 */
public class Worker implements Runnable {
    private final int workerNr;
    private final int timeSteps;
    private final NBodyBarnesHut problem;
    private final CyclicBarrier barrier;
    NBodyGraphics graphics;

    public Worker(int workerNr, NBodyBarnesHut problem, CyclicBarrier barrier, int timeSteps, NBodyGraphics graphics) {
        this.workerNr = workerNr;
        this.problem = problem;
        this.barrier = barrier;
        this.timeSteps = timeSteps;
        if (workerNr == 0) {
            this.graphics = graphics;
        }
        else {
            graphics = null;
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < timeSteps; i++) {
                problem.populateTree(workerNr);              
                barrier.await();
                problem.calculateForces(workerNr);
                barrier.await();
                problem.setQuadTreeFresh(false);
                problem.moveBodies(workerNr);
                barrier.await();
                if (workerNr == 0 && graphics != null) {
                    graphics.repaint();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (BrokenBarrierException ex) {
            ex.printStackTrace();
        }
    }
}
