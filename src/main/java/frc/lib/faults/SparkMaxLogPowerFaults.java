package frc.lib.faults;

import java.util.HashMap;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;

import frc.lib.EventLoops;
import frc.lib.SparkMaxWrapper;

public class SparkMaxLogPowerFaults {
    public static void setupCheck(SparkMaxWrapper sparkMax) {
        HashMap<CANSparkMax.FaultID, Fault> map = new HashMap<>();
        for (CANSparkBase.FaultID c : CANSparkBase.FaultID.values()) {
            map.put(c, new Fault(sparkMax.getName() + " " + c.toString()));
        }
        EventLoops.oncePerSec.bind(() -> {
            for (CANSparkBase.FaultID c : CANSparkBase.FaultID.values()) {
                boolean isActive = sparkMax.getStickyFault(c);
                map.get(c).setIsActive(isActive);
            }

            sparkMax.clearFaults();
        });
    }
}
