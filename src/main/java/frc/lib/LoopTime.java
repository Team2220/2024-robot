package frc.lib;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.Timer;


/*
 * This class works the same way as WPILib's Tracer class:
 * https://github.com/wpilibsuite/allwpilib/blob/e64c20346dfe3252098f0efe51a93bb766881b82/wpilibj/src/main/java/edu/wpi/first/wpilibj/Tracer.java
 */

public class LoopTime {
    public LoopTime(String name) {
    }

    public Measure<Time> measure(Runnable runable) {
        Timer.getFPGATimestamp();
        return Units.Seconds.of(Timer.getFPGATimestamp());
    }

}
