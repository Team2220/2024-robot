package frc.lib.faults;

import java.util.List;
import java.util.function.DoubleFunction;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;

import frc.lib.devices.TalonFXWrapper;
import frc.lib.eventLoops.EventLoops;

public class TalonFXLogPowerFaults {

        static class StickyFaults {
                private StatusSignal<Boolean> getStickyFault;
                private DoubleFunction<StatusCode> clearStickyFault;
                private Fault fault;

                StickyFaults(String name, StatusSignal<Boolean> getStickyFault,
                                DoubleFunction<StatusCode> clearStickyFault) {
                        this.fault = new Fault(name + " " + getStickyFault.getName());
                        this.getStickyFault = getStickyFault;
                        this.clearStickyFault = clearStickyFault;
                }

                void update() {
                        var hasFault = getStickyFault.getValue();
                        fault.setIsActive(hasFault);
                        clearStickyFault.apply(0.0000000001); // ctre does not like 0 in config calls
                }

        }

        public static void setupChecks(TalonFXWrapper wrapper) {
                var talonFX = wrapper.getTalon();
                var stickyFaults = List.of(
                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_BootDuringEnable(),
                                                talonFX::clearStickyFault_BootDuringEnable),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_BridgeBrownout(),
                                                talonFX::clearStickyFault_BridgeBrownout),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_DeviceTemp(),
                                                talonFX::clearStickyFault_DeviceTemp),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_ForwardHardLimit(),
                                                talonFX::clearStickyFault_ForwardHardLimit),
                                // too noisy
                                // new StickyFaults(
                                // "TalonFX:" + wrapper.getName() + "forwardSoftLimit",
                                // talonFX::getStickyFault_ForwardSoftLimit,
                                // talonFX::clearStickyFault_ForwardSoftLimit
                                // ),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_FusedSensorOutOfSync(),
                                                talonFX::clearStickyFault_FusedSensorOutOfSync),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_Hardware(),
                                                talonFX::clearStickyFault_Hardware),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_MissingDifferentialFX(),
                                                talonFX::clearStickyFault_MissingDifferentialFX),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_OverSupplyV(),
                                                talonFX::clearStickyFault_OverSupplyV),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_ProcTemp(),
                                                talonFX::clearStickyFault_ProcTemp),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_RemoteSensorDataInvalid(),
                                                talonFX::clearStickyFault_RemoteSensorDataInvalid),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_RemoteSensorPosOverflow(),
                                                talonFX::clearStickyFault_RemoteSensorPosOverflow),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_RemoteSensorReset(),
                                                talonFX::clearStickyFault_RemoteSensorReset),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_ReverseHardLimit(),
                                                talonFX::clearStickyFault_ReverseHardLimit),
                                // too noisy
                                // new StickyFaults(
                                // + wrapper.getName() + "reverseSoftLimit",
                                // talonFX::getStickyFault_ReverseSoftLimit,
                                // talonFX::clearStickyFault_ReverseSoftLimit
                                // ),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_StatorCurrLimit(),
                                                talonFX::clearStickyFault_StatorCurrLimit),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_SupplyCurrLimit(),
                                                talonFX::clearStickyFault_SupplyCurrLimit),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_Undervoltage(),
                                                talonFX::clearStickyFault_Undervoltage),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_UnlicensedFeatureInUse(),
                                                // unable to find clearStickyFault for UnlicensedFeatureInUse
                                                (double timeoutSecs) -> StatusCode.OK),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_UnstableSupplyV(),
                                                talonFX::clearStickyFault_UnstableSupplyV),

                                new StickyFaults(
                                                wrapper.getName(),
                                                talonFX.getStickyFault_UsingFusedCANcoderWhileUnlicensed(),
                                                // unable to find clearStickyFault for UsingFusedCANcoderWhileUnlicensed
                                                (double timeoutSecs) -> StatusCode.OK));

                EventLoops.oncePerSec.bind(() -> {
                        for (StickyFaults faults : stickyFaults) {
                                faults.update();
                        }
                });

                var device_temp = talonFX.getDeviceTemp();
                Fault.autoUpdating(wrapper.getName() + "MoterTempToHigh", () -> {
                        var value = device_temp.refresh().getValue();
                        // unable to find clearStickyFault for device_temp
                        return value > 100;
                });

        }
}