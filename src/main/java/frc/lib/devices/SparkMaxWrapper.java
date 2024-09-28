package frc.lib.devices;

import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Rotations;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.FaultID;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Temperature;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import frc.lib.faults.SparkMaxLogPowerFaults;
import frc.lib.tunables.TunableDouble;
import frc.lib.tunables.TunableMeasure;
import frc.lib.units.UnitsUtil;

public class SparkMaxWrapper {
    public String name;
    public CANSparkMax sparkMax;
    public SparkPIDController pidController;

    public SparkMaxWrapper(int id, String name, boolean isInverted, double gearRatio, double P, double I, double D,
            Measure<Velocity<Velocity<Angle>>> maxAcceleration, Measure<Velocity<Angle>> maxVelocity,
            double allowedErr, boolean forwardSoftLimitEnable,
            boolean reverseSoftLimitEnable,
            Measure<Angle> forwardSoftLimitTreshold,
            Measure<Angle> reverseSoftLimitThreshold) {
        this.name = name;

        sparkMax = new CANSparkMax(id, MotorType.kBrushless);
        sparkMax.restoreFactoryDefaults();
        sparkMax.enableVoltageCompensation(10);
        sparkMax.setInverted(isInverted);
        sparkMax.getEncoder().setPositionConversionFactor(1.0 / gearRatio);
        sparkMax.getEncoder().setVelocityConversionFactor(1.0 / gearRatio);
        pidController = sparkMax.getPIDController();

        sparkMax.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, reverseSoftLimitEnable);
        sparkMax.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, forwardSoftLimitEnable);
        sparkMax.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse,
                (float) forwardSoftLimitTreshold.in(Units.Rotations));
        sparkMax.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward,
                (float) reverseSoftLimitThreshold.in(Units.Rotations));

        sparkMax.setSmartCurrentLimit(60);

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
        this(id, name, isInverted, 1, 0, 0, 0, RPM.per(Second).of(0), RPM.of(0), 0, false, false, Units.Rotations.of(0),
                Units.Rotations.of(0));
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

    public Measure<Velocity<Angle>> getVelocity() {
        return RPM.of(sparkMax.getEncoder().getVelocity());
    }

    public Measure<Angle> getPosition() {
        return Rotations.of(sparkMax.getEncoder().getPosition());
    }

    public void setVelocity(Measure<Velocity<Angle>> speed) {
        pidController.setReference(speed.in(RPM), CANSparkBase.ControlType.kVelocity);
    }

    public boolean isAtReference(Measure<Velocity<Angle>> speed, Measure<Velocity<Angle>> tolerance) {
        var diff = (getVelocity().minus(speed));
        return UnitsUtil.abs(diff).lte(tolerance);
    }
}
