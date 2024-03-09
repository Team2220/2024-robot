package frc.lib.selfCheck;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import frc.lib.SparkMaxWrapper;
import frc.lib.units.UnitsUtil;

public class SparkMAXSpinCheck extends CheckCommand {
    SparkMaxWrapper spark;
    Measure<Angle> position;

    public SparkMAXSpinCheck(SparkMaxWrapper spark) {
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
        spark.set(0.25);
    }

    @Override
    public String getDescription() {
        return "spin " + spark.getName() + "forward";
    }
}
