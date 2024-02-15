package frc.lib.faults;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;

import frc.lib.EventLoops;

public class SparkMaxLogPowerFaults {
    public static void setupCheck(CANSparkMax sparkMax) {
        EventLoops.oncePerSec.bind(() -> {
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
