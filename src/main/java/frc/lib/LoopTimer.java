package frc.lib;

import edu.wpi.first.wpilibj.Timer;
import frc.lib.faults.Fault;

/*
 * This class works the same way as WPILib's Tracer class:
 * https://github.com/wpilibsuite/allwpilib/blob/e64c20346dfe3252098f0efe51a93bb766881b82/wpilibj/src/main/java/edu/wpi/first/wpilibj/Tracer.java
 */

public class LoopTimer {
  // private String name;
  private GraphWrapper graph;
  // private Fault fault;
  // private double maxTimeMilliseconds;

  public LoopTimer(String name, double maxTimeMilliseconds) {
    // this.name = name;
    this.graph = new GraphWrapper(name, "LoopTimers");
    // this.maxTimeMilliseconds = maxTimeMilliseconds;
    // this.fault = new Fault(name + " exceeded " + maxTimeMilliseconds);
  }

  public void measure(Runnable runable) {
    double start = Timer.getFPGATimestamp();
    runable.run();
    double end = Timer.getFPGATimestamp();
    graph.setDouble((end - start) * 1000);
    // boolean isMaxTimeExceeded = (end - start) * 1000 > maxTimeMilliseconds;
    // fault.setIsActive(isMaxTimeExceeded);
  }
}
