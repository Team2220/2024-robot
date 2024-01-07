// package frc.twilight;

// import java.util.ArrayList;

// import com.ctre.phoenix.motorcontrol.StickyFaults;
// import com.ctre.phoenix.motorcontrol.can.TalonFX;

// import edu.wpi.first.wpilibj.DataLogManager;

// public class TalonFXLogPowerFaults {
//     private static boolean firstCheckTalon = true;
//     private static ArrayList<TalonFX> talonFxs = new ArrayList<>();

//     public static void add(TalonFX talonFX) {
//         try {
//             talonFxs.add(talonFX);
//         } catch (Exception e) {
//             System.out.println(e);
//         }
//     }

//     public static void checkTalons() {
//         try {

//             for (TalonFX num : talonFxs) {
//                 String outputTalonFx = checkTalonFX(num, !firstCheckTalon);

//                 if (outputTalonFx != null) {
//                     DataLogManager.log(outputTalonFx);
//                 }

//             }
//             firstCheckTalon = false;

//         } catch (Exception expection) {
//             System.out.println(expection);
//         }
//     }

//     private static String checkTalonFX(TalonFX talon, boolean emptyOnNone) {
//         try {
//             StickyFaults faults = new StickyFaults();
//             talon.getStickyFaults(faults);
//             talon.clearStickyFaults();
//             String out = "";

//             if (faults.UnderVoltage)
//                 out += " - UnderVolyage\n";

//             if (faults.ForwardLimitSwitch)
//                 out += " - ForwardLimitSwitch\n";

//             if (faults.ReverseLimitSwitch)
//                 out += " - ReverseLimitSwitch\n";

//             if (faults.ForwardSoftLimit)
//                 out += " - ForwardSoftLimit\n";

//             if (faults.ReverseSoftLimit)
//                 out += " - ReverseSoftLimit\n";

//             if (faults.ResetDuringEn)
//                 out += " - ResetDuringEn\n";

//             if (faults.SensorOverflow)
//                 out += " - SensorOverflow\n";

//             if (faults.SensorOutOfPhase)
//                 out += " - SensorOutOfPhase\n";

//             if (faults.HardwareESDReset)
//                 out += " - HardwareESDReset\n";

//             if (faults.RemoteLossOfSignal)
//                 out += " - RemoteLossOfSignal\n";

//             if (faults.APIError)
//                 out += " - APIError\n";

//             if (faults.SupplyOverV)
//                 out += " - SupplyOverV\n";

//             if (faults.SupplyUnstable)
//                 out += " - SupplyUnstable\n";

//             if (out == "") {
//                 if (emptyOnNone) {
//                     return null;
//                 }

//                 else {
//                     out = " - No TalonFX " + talon.getDeviceID() + " sticky faults on startup :)";
//                 }
//             }
//             return "TalonFX" + talon.getDeviceID() + "faults:\n" + out;
//         } catch (Exception e) {
//             return e.toString();
//         }
//     }

// }