package frc.lib.faults;

import java.util.HashMap;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.units.Units;
import frc.lib.devices.SparkMaxWrapper;
import frc.lib.eventLoops.EventLoops;

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

        Fault.autoUpdating("SparkMax:" + sparkMax.getName() + "MoterTempToHigh", () -> {
            return sparkMax.getTemperature().gt(Units.Celsius.of(65));
        });
    }
}
