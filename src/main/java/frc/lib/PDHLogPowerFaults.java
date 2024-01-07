
package frc.lib;

import java.util.ArrayList;

import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.PowerDistribution;

public class PDHLogPowerFaults {
    private static boolean firstCheckPdh = true;
    private static PowerDistribution pdh = null;
    private static ArrayList<Integer> unusedBreakers = new ArrayList<Integer>();

    public static void setPdh(PowerDistribution pdh) {
        PDHLogPowerFaults.pdh = pdh;
    }

    public static void checkPDH() {
        try {
            if (firstCheckPdh) {
                DataLogManager.log(pdhFaultsToString(pdh.getStickyFaults(), false));
                pdh.clearStickyFaults();
                firstCheckPdh = false;
            }

            PowerDistributionStickyFaults faults = pdh.getStickyFaults();
            String pdhFault = pdhFaultsToString(faults, true);
            if (pdhFault != null) {
                DataLogManager.log(pdhFault);
                pdh.clearStickyFaults();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void addBreakerIgnore(int... num) {
        try {
            for (int i : num)
                unusedBreakers.add(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String pdhFaultsToString(PowerDistributionStickyFaults faults, boolean emptyOnNone) {
        try {
            String out = "";

            if (faults.Brownout)
                out += " - Brownout\n";
            if (faults.HasReset)
                out += " - Reset\n";
            if (faults.CanWarning)
                out += " - CAN Warning\n";
            if (faults.CanBusOff)
                out += " - CAN Bus Off\n";
            if (faults.Channel0BreakerFault && !unusedBreakers.contains(0))
                out += " - Channel 0 Breaker Fault\n";
            if (faults.Channel1BreakerFault && !unusedBreakers.contains(1))
                out += " - Channel 1 Breaker Fault\n";
            if (faults.Channel2BreakerFault && !unusedBreakers.contains(2))
                out += " - Channel 2 Breaker Fault\n";
            if (faults.Channel3BreakerFault && !unusedBreakers.contains(3))
                out += " - Channel 3 Breaker Fault\n";
            if (faults.Channel4BreakerFault && !unusedBreakers.contains(4))
                out += " - Channel 4 Breaker Fault\n";
            if (faults.Channel5BreakerFault && !unusedBreakers.contains(5))
                out += " - Channel 5 Breaker Fault\n";
            if (faults.Channel6BreakerFault && !unusedBreakers.contains(6))
                out += " - Channel 6 Breaker Fault\n";
            if (faults.Channel7BreakerFault && !unusedBreakers.contains(7))
                out += " - Channel 7 Breaker Fault\n";
            if (faults.Channel8BreakerFault && !unusedBreakers.contains(8))
                out += " - Channel 8 Breaker Fault\n";
            if (faults.Channel9BreakerFault && !unusedBreakers.contains(9))
                out += " - Channel 9 Breaker Fault\n";
            if (faults.Channel10BreakerFault && !unusedBreakers.contains(10))
                out += " - Channel 10 Breaker Fault\n";
            if (faults.Channel11BreakerFault && !unusedBreakers.contains(11))
                out += " - Channel 11 Breaker Fault\n";
            if (faults.Channel12BreakerFault && !unusedBreakers.contains(12))
                out += " - Channel 12 Breaker Fault\n";
            if (faults.Channel13BreakerFault && !unusedBreakers.contains(13))
                out += " - Channel 13 Breaker Fault\n";
            if (faults.Channel14BreakerFault && !unusedBreakers.contains(14))
                out += " - Channel 14 Breaker Fault\n";
            if (faults.Channel15BreakerFault && !unusedBreakers.contains(15))
                out += " - Channel 15 Breaker Fault\n";
            if (faults.Channel16BreakerFault && !unusedBreakers.contains(16))
                out += " - Channel 16 Breaker Fault\n";
            if (faults.Channel17BreakerFault && !unusedBreakers.contains(17))
                out += " - Channel 17 Breaker Fault\n";
            if (faults.Channel18BreakerFault && !unusedBreakers.contains(18))
                out += " - Channel 18 Breaker Fault\n";
            if (faults.Channel19BreakerFault && !unusedBreakers.contains(19))
                out += " - Channel 19 Breaker Fault\n";
            if (faults.Channel20BreakerFault && !unusedBreakers.contains(20))
                out += " - Channel 20 Breaker Fault\n";
            if (faults.Channel21BreakerFault && !unusedBreakers.contains(21))
                out += " - Channel 21 Breaker Fault\n";
            if (faults.Channel22BreakerFault && !unusedBreakers.contains(22))
                out += " - Channel 22 Breaker Fault\n";
            if (faults.Channel23BreakerFault && !unusedBreakers.contains(23))
                out += " - Channel 23 Breaker Fault\n";

            if (out == "" && !emptyOnNone)
                out = " - No PDH sticky faults on startup :)";
            else if (out == "" && emptyOnNone)
                return null;

            return "PDH faults:\n" + out;
        } catch (Exception e) {
            return e.toString();
        }
    }

}