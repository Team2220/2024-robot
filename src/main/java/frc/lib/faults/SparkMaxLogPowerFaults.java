package frc.lib.faults;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;

import frc.lib.IsolatedEventLoop;
import frc.lib.SparkMaxWrapper;

public class SparkMaxLogPowerFaults {

    private static void check(CANSparkMax sparkMax, IsolatedEventLoop oncepersec) {
        oncepersec.bind(() -> {
            for (CANSparkBase.FaultID c : CANSparkBase.FaultID.values()) {
                if (sparkMax.getStickyFault(c)) {
                    System.out.println(c);
                    Fault fault = new Fault(c.toString());
                    fault.setIsActive(true);
                }
            }

            sparkMax.clearFaults();
        });
    }
}
