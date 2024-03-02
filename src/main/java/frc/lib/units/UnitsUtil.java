package frc.lib.units;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.derive;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Velocity;

public class UnitsUtil {
    private UnitsUtil() {
        // hi - dont make instances
    }

    public static final Frequency hertz = new Frequency(1, "Hertz", "Hz");
    public static final Frequency megaHertz = derive(hertz).aggregate(1000).named("Mega Hertz").symbol("Mhz")
            .make();
    public static final Frequency gigaHertz = derive(megaHertz).aggregate(1000).named("Giga Hertz").symbol("Ghz")
            .make();

    public static <U extends Unit<U>> Measure<U> abs(Measure<U> measure) {
        double dMeasure = measure.in(measure.unit());
        return measure.unit().of(Math.abs(dMeasure));
    }

    public Measure<Distance> distanceForWheel(Measure<Distance> wheelDiameter, Measure<Angle> rotations) {
        var distance = Math.PI * wheelDiameter.in(Meters) * rotations.in(Rotations);
        return Meters.of(distance);
    }

    public Measure<Velocity<Distance>> velocityForWheel(Measure<Velocity<Distance>> velocity, Measure<Angle> rotation) {
        double vel = velocity.in(MetersPerSecond);
        double ang = rotation.in(Rotations);
        double velForWheel = vel * ang * Math.PI;
        return MetersPerSecond.of(velForWheel);
    }

    public static Measure<Velocity<Velocity<Angle>>> rotationsPerSecSq(double ang) {
        return RotationsPerSecond.per(Seconds).of(ang);
    }

    public static Measure<Velocity<Velocity<Velocity<Angle>>>> rotationsPerSecCubed(double ang) {
        return RotationsPerSecond.per(Seconds).per(Seconds).of(ang);
    }
}
