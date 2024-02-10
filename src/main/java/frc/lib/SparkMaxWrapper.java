package frc.lib;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class SparkMaxWrapper {
    private int id;
    public static String name;
    public CANSparkMax sparkMax;

    public SparkMaxWrapper(int id, String name) {
        id = this.id;
        name = this.name;
        sparkMax = new CANSparkMax(id, MotorType.kBrushless);
        sparkMax.restoreFactoryDefaults();
    }

    public void checkFaults() {
        if (sparkMax.getFault(null)) {

        }
    }

    public void set(double speed) {
        sparkMax.set(speed);
    }

    public void reset() {
        sparkMax.restoreFactoryDefaults();
    }
}
