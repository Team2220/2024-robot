package frc.lib.selfCheck;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import frc.lib.devices.SparkMaxWrapper;
import frc.lib.units.UnitsUtil;

public class SparkMAXSpinCheck extends CheckCommand {
    SparkMaxWrapper spark;
    Measure<Angle> position;
    boolean isForward;

    public SparkMAXSpinCheck(SparkMaxWrapper spark, boolean isForward) {
        this.spark = spark;
    }

    @Override
    public void initialize() {
        position = spark.getPosition();
    }

    @Override
    public boolean isFinished() {
        return UnitsUtil.abs(position.minus(spark.getPosition())).gt(Units.Rotations.of(30));
    }

    @Override
    public double getTimeoutSeconds() {
        return 5;
    }

    @Override
    public void end(boolean interrupted) {
        spark.set(0);
    }

    @Override
    public void execute() {
        spark.set(isForward ? 0.25 : -0.25);
    }

    @Override
    public String getDescription() {
        return "spin " + spark.getName() + (isForward ? "forward" : "backward");
    }
}
