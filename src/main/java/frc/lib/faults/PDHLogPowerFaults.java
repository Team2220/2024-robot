
package frc.lib.faults;

import java.util.ArrayList;

import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import frc.lib.eventLoops.EventLoops;
import frc.lib.eventLoops.IsolatedEventLoop;

public class PDHLogPowerFaults {
    private static boolean firstCheckPdh = true;
    private static PowerDistribution pdh = null;
    private static ArrayList<Integer> unusedBreakers = new ArrayList<Integer>();

    public static void setPdh(PowerDistribution pdh, int... unpluggedBreakers) {
        try {
            if (PDHLogPowerFaults.pdh != null || pdh == null) {
                return;
            }
            PDHLogPowerFaults.pdh = pdh;
            addBreakerIgnore(unpluggedBreakers);
            checkPDH(EventLoops.oncePerSec);
        } catch (Exception e) {
            DriverStation.reportError("PDH ERROR", e.getStackTrace());
        }
    }

    private static void checkPDH(IsolatedEventLoop oncepersec) {
        oncepersec.bind(() -> {
            if (firstCheckPdh) {
                logFaults(pdh.getStickyFaults());
                pdh.clearStickyFaults();
                firstCheckPdh = false;
            }

            PowerDistributionStickyFaults faults = pdh.getStickyFaults();
            updateFaults(faults);
            pdh.clearStickyFaults();
        });

    }

    private static void addBreakerIgnore(int... unpluggedBreakers) {
        try {
            for (int i : unpluggedBreakers)
                unusedBreakers.add(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Fault brownout = new Fault("pdh brownout");
    private static Fault hasReset = new Fault("pdh hasReset");
    private static Fault canWarning = new Fault("pdh canWarning");
    private static Fault canBusOff = new Fault("pdh canBusOff");
    private static Fault channel0BreakerFault = new Fault("pdh channel0BreakerFault");
    private static Fault channel1BreakerFault = new Fault("pdh channel1BreakerFault");
    private static Fault channel2BreakerFault = new Fault("pdh channel2BreakerFault");
    private static Fault channel3BreakerFault = new Fault("pdh channel3BreakerFault");
    private static Fault channel4BreakerFault = new Fault("pdh channel4BreakerFault");
    private static Fault channel5BreakerFault = new Fault("pdh channel5BreakerFault");
    private static Fault channel6BreakerFault = new Fault("pdh channel6BreakerFault");
    private static Fault channel7BreakerFault = new Fault("pdh channel7BreakerFault");
    private static Fault channel8BreakerFault = new Fault("pdh channel8BreakerFault");
    private static Fault channel9BreakerFault = new Fault("pdh channel9BreakerFault");
    private static Fault channel10BreakerFault = new Fault("pdh channel10BreakerFault");
    private static Fault channel11BreakerFault = new Fault("pdh channel11BreakerFault");
    private static Fault channel12BreakerFault = new Fault("pdh channel12BreakerFault");
    private static Fault channel13BreakerFault = new Fault("pdh channel13BreakerFault");
    private static Fault channel14BreakerFault = new Fault("pdh channel14BreakerFault");
    private static Fault channel15BreakerFault = new Fault("pdh channel15BreakerFault");
    private static Fault channel16BreakerFault = new Fault("pdh channel16BreakerFault");
    private static Fault channel17BreakerFault = new Fault("pdh channel17BreakerFault");
    private static Fault channel18BreakerFault = new Fault("pdh channel18BreakerFault");
    private static Fault channel19BreakerFault = new Fault("pdh channel19BreakerFault");
    private static Fault channel20BreakerFault = new Fault("pdh channel20BreakerFault");
    private static Fault channel21BreakerFault = new Fault("pdh channel21BreakerFault");
    private static Fault channel22BreakerFault = new Fault("pdh channel22BreakerFault");
    private static Fault channel23BreakerFault = new Fault("pdh channel23BreakerFault");

    private static void updateFaults(PowerDistributionStickyFaults faults) {
        brownout.setIsActive(faults.Brownout);
        hasReset.setIsActive(faults.HasReset);
        canWarning.setIsActive(faults.CanWarning);
        canBusOff.setIsActive(faults.CanBusOff);
        channel0BreakerFault.setIsActive(faults.Channel0BreakerFault && !unusedBreakers.contains(0));
        channel1BreakerFault.setIsActive(faults.Channel1BreakerFault && !unusedBreakers.contains(1));
        channel2BreakerFault.setIsActive(faults.Channel2BreakerFault && !unusedBreakers.contains(2));
        channel3BreakerFault.setIsActive(faults.Channel3BreakerFault && !unusedBreakers.contains(3));
        channel4BreakerFault.setIsActive(faults.Channel4BreakerFault && !unusedBreakers.contains(4));
        channel5BreakerFault.setIsActive(faults.Channel5BreakerFault && !unusedBreakers.contains(5));
        channel6BreakerFault.setIsActive(faults.Channel6BreakerFault && !unusedBreakers.contains(6));
        channel7BreakerFault.setIsActive(faults.Channel7BreakerFault && !unusedBreakers.contains(7));
        channel8BreakerFault.setIsActive(faults.Channel8BreakerFault && !unusedBreakers.contains(8));
        channel9BreakerFault.setIsActive(faults.Channel9BreakerFault && !unusedBreakers.contains(9));
       // channel10BreakerFault.setIsActive(faults.Channel10BreakerFault && !unusedBreakers.contains(10));
        channel11BreakerFault.setIsActive(faults.Channel11BreakerFault && !unusedBreakers.contains(11));
        channel12BreakerFault.setIsActive(faults.Channel12BreakerFault && !unusedBreakers.contains(12));
        channel13BreakerFault.setIsActive(faults.Channel13BreakerFault && !unusedBreakers.contains(13));
        channel14BreakerFault.setIsActive(faults.Channel14BreakerFault && !unusedBreakers.contains(14));
        channel15BreakerFault.setIsActive(faults.Channel15BreakerFault && !unusedBreakers.contains(15));
        channel16BreakerFault.setIsActive(faults.Channel16BreakerFault && !unusedBreakers.contains(16));
        channel17BreakerFault.setIsActive(faults.Channel17BreakerFault && !unusedBreakers.contains(17));
        channel18BreakerFault.setIsActive(faults.Channel18BreakerFault && !unusedBreakers.contains(18));
        channel19BreakerFault.setIsActive(faults.Channel19BreakerFault && !unusedBreakers.contains(19));
        channel20BreakerFault.setIsActive(faults.Channel20BreakerFault && !unusedBreakers.contains(20));
        channel21BreakerFault.setIsActive(faults.Channel21BreakerFault && !unusedBreakers.contains(21));
        channel22BreakerFault.setIsActive(faults.Channel22BreakerFault && !unusedBreakers.contains(22));
        channel23BreakerFault.setIsActive(faults.Channel23BreakerFault && !unusedBreakers.contains(23));
    }

    private static void logFaults(PowerDistributionStickyFaults faults) {
        if (faults.Brownout) {
            DataLogManager.log("PDH Brownout Fault");
        }
        if (faults.HasReset) {
            DataLogManager.log("PDH HasReset Fault");
        }
        if (faults.CanWarning) {
            DataLogManager.log("PDH CanWarning Fault");
        }
        if (faults.CanBusOff) {
            DataLogManager.log("PDH CanBusOff Fault");
        }
        if (faults.Channel0BreakerFault && !unusedBreakers.contains(0)) {
            DataLogManager.log("PDH Channel0BreakerFault Fault");
        }
        if (faults.Channel1BreakerFault && !unusedBreakers.contains(1)) {
            DataLogManager.log("PDH Channel1BreakerFault Fault");
        }
        if (faults.Channel2BreakerFault && !unusedBreakers.contains(2)) {
            DataLogManager.log("PDH Channel2BreakerFault Fault");
        }
        if (faults.Channel3BreakerFault && !unusedBreakers.contains(3)) {
            DataLogManager.log("PDH Channel3BreakerFault Fault");
        }
        if (faults.Channel4BreakerFault && !unusedBreakers.contains(4)) {
            DataLogManager.log("PDH Channel4BreakerFault Fault");
        }
        if (faults.Channel5BreakerFault && !unusedBreakers.contains(5)) {
            DataLogManager.log("PDH Channel5BreakerFault Fault");
        }
        if (faults.Channel6BreakerFault && !unusedBreakers.contains(6)) {
            DataLogManager.log("PDH Channel6BreakerFault Fault");
        }
        if (faults.Channel7BreakerFault && !unusedBreakers.contains(7)) {
            DataLogManager.log("PDH Channel7BreakerFault Fault");
        }
        if (faults.Channel8BreakerFault && !unusedBreakers.contains(8)) {
            DataLogManager.log("PDH Channel8BreakerFault Fault");
        }
        if (faults.Channel9BreakerFault && !unusedBreakers.contains(9)) {
            DataLogManager.log("PDH Channel9BreakerFault Fault");
        }
        if (faults.Channel10BreakerFault && !unusedBreakers.contains(10)) {
            DataLogManager.log("PDH Channel10BreakerFault Fault");
        }
        if (faults.Channel11BreakerFault && !unusedBreakers.contains(11)) {
            DataLogManager.log("PDH Channel11BreakerFault Fault");
        }
        if (faults.Channel12BreakerFault && !unusedBreakers.contains(12)) {
            DataLogManager.log("PDH Channel2BreakerFault Fault");
        }
        if (faults.Channel13BreakerFault && !unusedBreakers.contains(13)) {
            DataLogManager.log("PDH Channel3BreakerFault Fault");
        }
        if (faults.Channel14BreakerFault && !unusedBreakers.contains(14)) {
            DataLogManager.log("PDH Channel4BreakerFault Fault");
        }
        if (faults.Channel15BreakerFault && !unusedBreakers.contains(15)) {
            DataLogManager.log("PDH Channel5BreakerFault Fault");
        }
        if (faults.Channel16BreakerFault && !unusedBreakers.contains(16)) {
            DataLogManager.log("PDH Channel6BreakerFault Fault");
        }
        if (faults.Channel7BreakerFault && !unusedBreakers.contains(17)) {
            DataLogManager.log("PDH Channel7BreakerFault Fault");
        }
        if (faults.Channel18BreakerFault && !unusedBreakers.contains(18)) {
            DataLogManager.log("PDH Channel8BreakerFault Fault");
        }
        if (faults.Channel9BreakerFault && !unusedBreakers.contains(19)) {
            DataLogManager.log("PDH Channel9BreakerFault Fault");
        }
        if (faults.Channel20BreakerFault && !unusedBreakers.contains(20)) {
            DataLogManager.log("PDH Channel20BreakerFault Fault");
        }
        if (faults.Channel21BreakerFault && !unusedBreakers.contains(21)) {
            DataLogManager.log("PDH Channel21BreakerFault Fault");
        }
        if (faults.Channel22BreakerFault && !unusedBreakers.contains(22)) {
            DataLogManager.log("PDH Channel22BreakerFault Fault");
        }
        if (faults.Channel23BreakerFault && !unusedBreakers.contains(23)) {
            DataLogManager.log("PDH Channel23BreakerFault Fault");
        }
    }
}
