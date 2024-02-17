package frc.lib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;

import frc.lib.faults.SparkMaxLogPowerFaults;
import frc.lib.tunables.TunableDouble;

public class SparkMaxWrapper {
    public String name;
    public CANSparkMax sparkMax;
    public SparkPIDController pidController;

    public SparkMaxWrapper(int id, String name, double P, double I, double D, double maxAcceleration,
            double maxVelocity, double allowedErr) {
        this.name = name;

        sparkMax = new CANSparkMax(id, MotorType.kBrushless);
        sparkMax.restoreFactoryDefaults();
        pidController = sparkMax.getPIDController();

        new TunableDouble("P", P, getName(), value -> {
            pidController.setP(value);
        });

        new TunableDouble("I", I, getName(), value -> {
            pidController.setI(value);
        });

        new TunableDouble("D", D, getName(), value -> {
            pidController.setD(value);
        });

        new TunableDouble("maxAcceleration", maxAcceleration, getName(), value -> {
            pidController.setSmartMotionMaxAccel(value, 0);
        });

        new TunableDouble("maxVelocity", maxVelocity, getName(), value -> {
            pidController.setSmartMotionMaxVelocity(value, 0);
        });

        new TunableDouble("allowedErr", allowedErr, getName(), value -> {
            pidController.setSmartMotionAllowedClosedLoopError(value, 0);
        });

        SparkMaxLogPowerFaults.setupCheck(sparkMax);
    }

    public SparkMaxWrapper(int id, String name) {
        this(id, name, 0, 0, 0, 0, 0, 0);
    }

    public void setInverted(boolean inverted) {
        sparkMax.setInverted(inverted);
    }

    public String getName() {
        return name + " (" + sparkMax.getDeviceId() + ")";
    }

    public void set(double speed) {
        sparkMax.set(speed);
    }

    public double getVelocity() {
        return sparkMax.getEncoder().getVelocity();
    }

    public void setReference(double speed) {
        pidController.setReference(speed, CANSparkMax.ControlType.kSmartVelocity);
    }
}
