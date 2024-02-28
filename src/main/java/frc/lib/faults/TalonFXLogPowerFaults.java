package frc.lib.faults;

import frc.lib.TalonFXWrapper;

public class TalonFXLogPowerFaults {

    public static void setupChecks(TalonFXWrapper wrapper) {
        var talonFX = wrapper.getTalon();
        var bootDuringEnable = talonFX.getStickyFault_BootDuringEnable();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "bootDuringEnable", () -> {
            var value = bootDuringEnable.refresh().getValue();
            talonFX.clearStickyFault_BootDuringEnable();
            return value;
        });
        var bridgeBrownout = talonFX.getStickyFault_BridgeBrownout();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "bridgeBrownout", () -> {
            var value = bridgeBrownout.refresh().getValue();
            talonFX.clearStickyFault_BridgeBrownout();
            return value;
        });
        var deviceTemp = talonFX.getStickyFault_DeviceTemp();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "deviceTemp", () -> {
            var value = deviceTemp.refresh().getValue();
            talonFX.clearStickyFault_DeviceTemp();
            return value;
        });
        var forwardHardLimit = talonFX.getStickyFault_ForwardHardLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "forwardHardLimit", () -> {
            var value = forwardHardLimit.refresh().getValue();
            talonFX.clearStickyFault_ForwardHardLimit();
            return value;
        });
        var forwardSoftLimit = talonFX.getStickyFault_ForwardSoftLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "forwardSoftLimit", () -> {
            var value = forwardSoftLimit.refresh().getValue();
            talonFX.clearStickyFault_ForwardSoftLimit();
            return value;
        });
        var fusedSensorOutOfSync = talonFX.getStickyFault_FusedSensorOutOfSync();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "fusedSensorOutOfSync", () -> {
            var value = fusedSensorOutOfSync.refresh().getValue();
            talonFX.clearStickyFault_FusedSensorOutOfSync();
            return value;
        });
        var hardware = talonFX.getStickyFault_Hardware();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "hardware", () -> {
            var value = hardware.refresh().getValue();
            talonFX.clearStickyFault_Hardware();
            return value;
        });
        var missingDifferentialFX = talonFX.getStickyFault_MissingDifferentialFX();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "missingDifferentialFX", () -> {
            var value = missingDifferentialFX.refresh().getValue();
            talonFX.clearStickyFault_MissingDifferentialFX();
            return value;
        });
        var overSupplyV = talonFX.getStickyFault_OverSupplyV();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "overSupplyV", () -> {
            var value = overSupplyV.refresh().getValue();
            talonFX.clearStickyFault_OverSupplyV();
            return value;
        });
        var procTemp = talonFX.getStickyFault_ProcTemp();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "procTemp", () -> {
            var value = procTemp.refresh().refresh().getValue();
            talonFX.clearStickyFault_ProcTemp();
            return value;
        });
        var remoteSensorDataInvalid = talonFX.getStickyFault_RemoteSensorDataInvalid();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "remoteSensorDataInvalid", () -> {
            var value = remoteSensorDataInvalid.refresh().getValue();
            talonFX.clearStickyFault_RemoteSensorDataInvalid();
            return value;
        });
        var remoteSensorPosOverflow = talonFX.getStickyFault_RemoteSensorPosOverflow();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "remoteSensorPosOverflow", () -> {
            var value = remoteSensorPosOverflow.refresh().getValue();
            talonFX.clearStickyFault_RemoteSensorPosOverflow();
            return value;
        });
        var remoteSensorReset = talonFX.getStickyFault_RemoteSensorReset();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "remoteSensorReset", () -> {
            var value = remoteSensorReset.refresh().getValue();
            talonFX.clearStickyFault_RemoteSensorReset();
            return value;
        });
        var reverseHardLimit = talonFX.getStickyFault_ReverseHardLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "reverseHardLimit", () -> {
            var value = reverseHardLimit.refresh().getValue();
            talonFX.clearStickyFault_ReverseHardLimit();
            return value;
        });
        var reverseSoftLimit = talonFX.getStickyFault_ReverseSoftLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "reverseSoftLimit", () -> {
            var value = reverseSoftLimit.refresh().getValue();
            talonFX.clearStickyFault_ReverseSoftLimit();
            return value;
        });
        var statorCurrLimit = talonFX.getStickyFault_StatorCurrLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "statorCurrLimit", () -> {
            var value = statorCurrLimit.refresh().getValue();
            talonFX.clearStickyFault_StatorCurrLimit();
            return value;
        });
        var supplyCurrLimit = talonFX.getStickyFault_SupplyCurrLimit();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "supplyCurrLimit", () -> {
            var value = supplyCurrLimit.refresh().getValue();
            talonFX.clearStickyFault_SupplyCurrLimit();
            return value;
        });
        var undervoltage = talonFX.getStickyFault_Undervoltage();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "undervoltage", () -> {
            var value = undervoltage.refresh().getValue();
            talonFX.clearStickyFault_Undervoltage();
            return value;
        });
        var unlicensedFeatureInUse = talonFX.getStickyFault_UnlicensedFeatureInUse();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "unlicensedFeatureInUse", () -> {
            var value = unlicensedFeatureInUse.refresh().getValue();
            // unable to find clearStickyFault for UnlicensedFeatureInUse
            return value;
        });
        var unstableSupplyV = talonFX.getStickyFault_UnstableSupplyV();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "unstableSupplyV", () -> {
            var value = unstableSupplyV.refresh().getValue();
            talonFX.clearStickyFault_UnstableSupplyV();
            return value;
        });
        var usingFusedCANcoderWhileUnlicensed = talonFX.getStickyFault_UsingFusedCANcoderWhileUnlicensed();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "usingFusedCANcoderWhileUnlicensed", () -> {
            var value = usingFusedCANcoderWhileUnlicensed.refresh().getValue();
            // unable to find clearStickyFault for UsingFusedCANcoderWhileUnlicensed
            return value;
        });
        var device_temp = talonFX.getDeviceTemp();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "MoterTempToHigh", () -> {
            var value = device_temp.refresh().getValue();
            // unable to find clearStickyFault for UsingFusedCANcoderWhileUnlicensed
            return value > 100;

        });

    }
}