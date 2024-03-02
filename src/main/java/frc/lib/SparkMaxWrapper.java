package frc.lib;

import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.FaultID;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.units.Velocity;
import frc.lib.faults.SparkMaxLogPowerFaults;
import frc.lib.tunables.TunableDouble;
import frc.lib.tunables.TunableMeasure;

public class SparkMaxWrapper {
    public String name;
    public CANSparkMax sparkMax;
    public SparkPIDController pidController;

    public SparkMaxWrapper(int id, String name, boolean isInverted, double P, double I, double D,
            Measure<Velocity<Velocity<Angle>>> maxAcceleration, Measure<Velocity<Angle>> maxVelocity,
            double allowedErr) {
        this.name = name;

        sparkMax = new CANSparkMax(id, MotorType.kBrushless);
        sparkMax.restoreFactoryDefaults();
        sparkMax.enableVoltageCompensation(10);
        sparkMax.setInverted(isInverted);
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

        new TunableMeasure<>("maxAcceleration", maxAcceleration, getName(), value -> {
            pidController.setSmartMotionMaxAccel(value.in(RPM.per(Seconds)), 0);
        });

        new TunableMeasure<>("maxVelocity", maxVelocity, getName(), value -> {
            pidController.setSmartMotionMaxVelocity(value.in(RPM), 0);
        });

        new TunableDouble("allowedErr", allowedErr, getName(), value -> {
            pidController.setSmartMotionAllowedClosedLoopError(value, 0);
        });

        SparkMaxLogPowerFaults.setupCheck(this);
        sparkMax.burnFlash();
    }

    public SparkMaxWrapper(int id, String name, boolean isInverted) {
        this(id, name, isInverted, 0, 0, 0, RPM.per(Second).of(0), RPM.of(0), 0);
    }

    public boolean getStickyFault(FaultID faultID) {
        return sparkMax.getStickyFault(faultID);
    }

    public void clearFaults() {
        sparkMax.clearFaults();
    }

    public String getName() {
        return name + " (SparkMax" + sparkMax.getDeviceId() + ")";
    }

    public Measure<Temperature> getTemperature() {
        var temp = sparkMax.getMotorTemperature();
        return Celsius.of(temp);
    }

    public void set(double speed) {
        sparkMax.set(speed);
    }

    public double getVelocity() {
        return sparkMax.getEncoder().getVelocity();
    }

    public double getPosition() {
        return sparkMax.getEncoder().getPosition();
    }

    public void setReference(double speed) {
        pidController.setReference(speed, CANSparkBase.ControlType.kVelocity);
    }

    public boolean isAtReference(double speed, double tolerance) {
        double diff = (getVelocity() - speed);
        System.out.println("hedskb :" + diff);
        return Math.abs(diff) <= tolerance;
    }
}
