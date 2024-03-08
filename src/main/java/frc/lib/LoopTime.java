package frc.lib;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.Timer;

public class LoopTime {
    private String name;

    public LoopTime(String name) {
        this.name = name;
    }

    public Measure<Time> measure(Runnable runable) {
        Timer.getFPGATimestamp();
        return Units.Seconds.of(Timer.getFPGATimestamp());
    }

}
