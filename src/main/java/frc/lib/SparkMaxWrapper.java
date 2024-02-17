package frc.lib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;

import frc.lib.faults.SparkMaxLogPowerFaults;
import frc.lib.tunables.TunableDouble;

public class SparkMaxWrapper {
    private final double P;
    private final double I;
    private final double D;
    private int id;
    public String name;
    public CANSparkMax sparkMax;
    public SparkPIDController pidController;

    public SparkMaxWrapper(int id, String name, double P, double I, double D) {
        this.id = id;
        this.name = name;
        this.P = P;
        this.I = I;
        this.D = D;
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

        // SparkMaxLogPowerFaults.setupCheck(sparkMax);
    }

    public SparkMaxWrapper(int id, String name) {
        this(id, name, 0, 0, 0);
    }

    public String getName() {
        return name + " (" + sparkMax.getDeviceId() + ")";
    }

    public void set(double speed) {
        sparkMax.set(speed);
    }

    public void setReference(double speed) {
        pidController.setReference(speed, CANSparkMax.ControlType.kSmartVelocity);
    }
}
