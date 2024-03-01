package frc.lib;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;

public class UnitsUtil<U extends Unit<U>> {
    private UnitsUtil() {
        // hi - dont make instances
    }

    public Measure<U> abs(Measure<U> measure) {
        double dMeasure = measure.in(measure.unit());
        return measure.unit().of(Math.abs(dMeasure));
    }

    public Measure<Distance> distanceForWheel(Measure<Distance> wheelDiameter, Measure<Angle> rotations) {
        var distance = Math.PI * wheelDiameter.in(Units.Meters) * rotations.in(Units.Rotations);
        return Units.Meters.of(distance);
    }

    public Measure<Velocity<Distance>> velocityForWheel(Measure<Velocity<Distance>> velocity, Measure<Angle> rotation) {
        double vel = velocity.in(Units.MetersPerSecond);
        double ang = rotation.in(Units.Rotations);
        double velForWheel = vel * ang * Math.PI;
        return Units.MetersPerSecond.of(velForWheel);
    }
}
