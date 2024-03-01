package frc.lib.selfCheck;

import frc.lib.SparkMaxWrapper;

public class SparkMAXSpinCheck extends CheckCommand {
    SparkMaxWrapper spark;
    double position;

    public SparkMAXSpinCheck(SparkMaxWrapper spark) {
        this.spark = spark;

    }

    @Override
    public void initialize() {
        position = spark.getPosition();

    }

    @Override
    public boolean isFinished() {
        return Math.abs(position - spark.getPosition()) > 30;
    }


    

     @Override
    public
    double getTimeoutSeconds() {
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
}
