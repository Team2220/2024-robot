package frc.lib.faults;

import com.revrobotics.CANSparkBase;

import frc.lib.IsolatedEventLoop;
import frc.lib.SparkMaxWrapper;

public class SparkMaxLogPowerFaults {
    static SparkMaxWrapper sparkMaxWrap = new SparkMaxWrapper(1, "");

    private static void checkSparkMax(IsolatedEventLoop oncepersec) {
        oncepersec.bind(() -> {
            for (CANSparkBase.FaultID c : CANSparkBase.FaultID.values()) {
                if (sparkMaxWrap.sparkMax.getStickyFault(c)) {
                    System.out.println(c);
                }
            }

            sparkMaxWrap.sparkMax.clearFaults();
        });
    }
}
