package frc.lib;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;

public class TalonFXLogPowerFaults {

    public static void setupChecks(TalonFX talonFX) {
        var bootDuringEnable = talonFX.getStickyFault_BootDuringEnable();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "bootDuringEnable", ()->{
            var value = bootDuringEnable.getValue();
            talonFX.clearStickyFault_BootDuringEnable();
            return value;
        });
        var bridgeBrownout = talonFX.getStickyFault_BridgeBrownout();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "bridgeBrownout", ()->{
            var value = bridgeBrownout.getValue();
            talonFX.clearStickyFault_BridgeBrownout();
            return value;
        });
        var deviceTemp = talonFX.getStickyFault_DeviceTemp();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "deviceTemp", ()->{
            var value = deviceTemp.getValue();
            talonFX.clearStickyFault_DeviceTemp();
            return value;
        });
        var forwardHardLimit = talonFX.getStickyFault_ForwardHardLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "forwardHardLimit", ()->{
            var value = forwardHardLimit.getValue();
            talonFX.clearStickyFault_ForwardHardLimit();
            return value;
        });
        var forwardSoftLimit = talonFX.getStickyFault_ForwardSoftLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "forwardSoftLimit", ()->{
            var value = forwardSoftLimit.getValue();
            talonFX.clearStickyFault_ForwardSoftLimit();
            return value;
        });
        var fusedSensorOutOfSync = talonFX.getStickyFault_FusedSensorOutOfSync();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "fusedSensorOutOfSync", ()->{
            var value = fusedSensorOutOfSync.getValue();
            talonFX.clearStickyFault_FusedSensorOutOfSync();
            return value;
        });
        var hardware = talonFX.getStickyFault_Hardware();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "hardware", ()->{
            var value = hardware.getValue();
            talonFX.clearStickyFault_Hardware();
            return value;
        });
        var missingDifferentialFX = talonFX.getStickyFault_MissingDifferentialFX();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "missingDifferentialFX", ()->{
            var value = missingDifferentialFX.getValue();
            talonFX.clearStickyFault_MissingDifferentialFX();
            return value;
        });
        var overSupplyV = talonFX.getStickyFault_OverSupplyV();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "overSupplyV", ()->{
            var value = overSupplyV.getValue();
            talonFX.clearStickyFault_OverSupplyV();
            return value;
        });
        var procTemp = talonFX.getStickyFault_ProcTemp();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "procTemp", ()->{
            var value = procTemp.getValue();
            talonFX.clearStickyFault_ProcTemp();
            return value;
        });
        var remoteSensorDataInvalid = talonFX.getStickyFault_RemoteSensorDataInvalid();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "remoteSensorDataInvalid", ()->{
            var value = remoteSensorDataInvalid.getValue();
            talonFX.clearStickyFault_RemoteSensorDataInvalid();
            return value;
        });
        var remoteSensorPosOverflow = talonFX.getStickyFault_RemoteSensorPosOverflow();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "remoteSensorPosOverflow", ()->{
            var value = remoteSensorPosOverflow.getValue();
            talonFX.clearStickyFault_RemoteSensorPosOverflow();
            return value;
        });
        var remoteSensorReset = talonFX.getStickyFault_RemoteSensorReset();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "remoteSensorReset", ()->{
            var value = remoteSensorReset.getValue();
            talonFX.clearStickyFault_RemoteSensorReset();
            return value;
        });
        var reverseHardLimit = talonFX.getStickyFault_ReverseHardLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "reverseHardLimit", ()->{
            var value = reverseHardLimit.getValue();
            talonFX.clearStickyFault_ReverseHardLimit();
            return value;
        });
        var reverseSoftLimit = talonFX.getStickyFault_ReverseSoftLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "reverseSoftLimit", ()->{
            var value = reverseSoftLimit.getValue();
            talonFX.clearStickyFault_ReverseSoftLimit();
            return value;
        });
        var statorCurrLimit = talonFX.getStickyFault_StatorCurrLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "statorCurrLimit", ()->{
            var value = statorCurrLimit.getValue();
            talonFX.clearStickyFault_StatorCurrLimit();
            return value;
        });
        var supplyCurrLimit = talonFX.getStickyFault_SupplyCurrLimit();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "supplyCurrLimit", ()->{
            var value = supplyCurrLimit.getValue();
            talonFX.clearStickyFault_SupplyCurrLimit();
            return value;
        });
        var undervoltage = talonFX.getStickyFault_Undervoltage();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "undervoltage", ()->{
            var value = undervoltage.getValue();
            talonFX.clearStickyFault_Undervoltage();
            return value;
        });
        var unlicensedFeatureInUse = talonFX.getStickyFault_UnlicensedFeatureInUse();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "unlicensedFeatureInUse", ()->{
            var value = unlicensedFeatureInUse.getValue();
            //unable to find clearStickyFault for UnlicensedFeatureInUse
            return value;
        });
        var unstableSupplyV = talonFX.getStickyFault_UnstableSupplyV();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "unstableSupplyV", ()->{
            var value = unstableSupplyV.getValue();
            talonFX.clearStickyFault_UnstableSupplyV();
            return value;
        });
        var usingFusedCANcoderWhileUnlicensed = talonFX.getStickyFault_UsingFusedCANcoderWhileUnlicensed();
        Fault.autoUpdating("TalonFX:" + talonFX.getDeviceID() + "usingFusedCANcoderWhileUnlicensed", ()->{
            var value = usingFusedCANcoderWhileUnlicensed.getValue();
            //unable to find clearStickyFault for UsingFusedCANcoderWhileUnlicensed
            return value;
        });

    }

    
}