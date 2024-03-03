package frc.lib.faults;

import java.util.List;
import java.util.function.Supplier;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;

import frc.lib.TalonFXWrapper;
import frc.lib.eventLoops.EventLoops;

public class TalonFXLogPowerFaults {

    static class StickyFaults {
        private Supplier<StatusSignal<Boolean>> stickyFaultSignal;
        private Supplier<StatusCode> stickyFaultyCode;
        private Fault fault;

        StickyFaults(String stickyFaultName, Supplier<StatusSignal<Boolean>> stickyFaultSignal, Supplier<StatusCode> stickyFaultyCode) {
            this.fault = new Fault(stickyFaultName);
            this.stickyFaultSignal = stickyFaultSignal;
            this.stickyFaultyCode = stickyFaultyCode;
        }

        void update() {
            var hasFault = stickyFaultSignal.get().getValue();
            fault.setIsActive(hasFault);
            stickyFaultyCode.get();
        }

        
    }
    public static void setupChecks(TalonFXWrapper wrapper) {
        var talonFX = wrapper.getTalon();
        var stickyFaults = List.of(
            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "bootDuringEnable", 
                talonFX::getStickyFault_BootDuringEnable, 
                talonFX::clearStickyFault_BootDuringEnable
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "bridgeBrownout", 
                talonFX::getStickyFault_BridgeBrownout, 
                talonFX::clearStickyFault_BridgeBrownout
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "deviceTemp", 
                talonFX::getStickyFault_DeviceTemp, 
                talonFX::clearStickyFault_DeviceTemp
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "forwardHardLimit", 
                talonFX::getStickyFault_ForwardHardLimit, 
                talonFX::clearStickyFault_ForwardHardLimit
            ),
            // too noisy
            // new StickyFaults(
            //     "TalonFX:" + wrapper.getName() + "forwardSoftLimit", 
            //     talonFX::getStickyFault_ForwardSoftLimit, 
            //     talonFX::clearStickyFault_ForwardSoftLimit
            // ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "fusedSensorOutOfSync", 
                talonFX::getStickyFault_FusedSensorOutOfSync, 
                talonFX::clearStickyFault_FusedSensorOutOfSync
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "hardware", 
                talonFX::getStickyFault_Hardware, 
                talonFX::clearStickyFault_Hardware
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "missingDifferentialFX", 
                talonFX::getStickyFault_MissingDifferentialFX, 
                talonFX::clearStickyFault_MissingDifferentialFX
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "overSupplyV", 
                talonFX::getStickyFault_OverSupplyV, 
                talonFX::clearStickyFault_OverSupplyV
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "procTemp", 
                talonFX::getStickyFault_ProcTemp, 
                talonFX::clearStickyFault_ProcTemp
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "remoteSensorDataInvalid", 
                talonFX::getStickyFault_RemoteSensorDataInvalid, 
                talonFX::clearStickyFault_RemoteSensorDataInvalid
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "remoteSensorPosOverflow", 
                talonFX::getStickyFault_RemoteSensorPosOverflow, 
                talonFX::clearStickyFault_RemoteSensorPosOverflow
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "remoteSensorReset", 
                talonFX::getStickyFault_RemoteSensorReset, 
                talonFX::clearStickyFault_RemoteSensorReset
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "reverseHardLimit", 
                talonFX::getStickyFault_ReverseHardLimit, 
                talonFX::clearStickyFault_ReverseHardLimit
            ),
            // too noisy
            // new StickyFaults(
            //     "TalonFX:" + wrapper.getName() + "reverseSoftLimit", 
            //     talonFX::getStickyFault_ReverseSoftLimit, 
            //     talonFX::clearStickyFault_ReverseSoftLimit
            // ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "statorCurrLimit", 
                talonFX::getStickyFault_StatorCurrLimit, 
                talonFX::clearStickyFault_StatorCurrLimit
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "supplyCurrLimit", 
                talonFX::getStickyFault_SupplyCurrLimit, 
                talonFX::clearStickyFault_SupplyCurrLimit
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "undervoltage", 
                talonFX::getStickyFault_Undervoltage, 
                talonFX::clearStickyFault_Undervoltage
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "unlicensedFeatureInUse", 
                talonFX::getStickyFault_UnlicensedFeatureInUse,
                // unable to find clearStickyFault for UnlicensedFeatureInUse
                () -> StatusCode.OK
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "unstableSupplyV", 
                talonFX::getStickyFault_UnstableSupplyV, 
                talonFX::clearStickyFault_UnstableSupplyV
            ),

            new StickyFaults(
                "TalonFX:" + wrapper.getName() + "usingFusedCANcoderWhileUnlicensed", 
                talonFX::getStickyFault_UsingFusedCANcoderWhileUnlicensed, 
                // unable to find clearStickyFault for UsingFusedCANcoderWhileUnlicensed
                () -> StatusCode.OK
            )
        );

        EventLoops.oncePerSec.bind(() -> {
            for (StickyFaults faults: stickyFaults) {
                faults.update();
            }
            });

        var device_temp = talonFX.getDeviceTemp();
        Fault.autoUpdating("TalonFX:" + wrapper.getName() + "MoterTempToHigh", () -> {
            var value = device_temp.refresh().getValue();
            // unable to find clearStickyFault for device_temp
            return value > 100;
        });

    }
}