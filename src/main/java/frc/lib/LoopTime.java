package frc.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/*
 * This class works the same way as WPILib's Tracer class:
 * https://github.com/wpilibsuite/allwpilib/blob/e64c20346dfe3252098f0efe51a93bb766881b82/wpilibj/src/main/java/edu/wpi/first/wpilibj/Tracer.java
 */

public class LoopTime {
    private String name;
    private GenericEntry graph;

    public LoopTime(String name) {
        this.name = name;
        this.graph = Shuffleboard.getTab("LoopTime").add(name, 0).withWidget(BuiltInWidgets.kGraph).getEntry();
    }

    public void measure(Runnable runable) {
        double start = Timer.getFPGATimestamp();
        runable.run();
        double end = Timer.getFPGATimestamp();
        graph.setDouble((end - start) * 1000);
    }
}
