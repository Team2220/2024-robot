package frc.lib;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.faults.Fault;
import frc.lib.faults.TalonFXLogPowerFaults;
import frc.lib.tunables.TunableDouble;

public class TalonFXWrapper {
    private TalonFX talon;
    private String name;
    private TalonFXConfiguration talonFXConfigs;
    private static Fault fault = new Fault("TalonFX device disconnected");
    private StatusSignal<Integer> firmwareVersionSignal;
    private Fault softLimitOverrideFault;

    public TalonFXWrapper(
            int id,
            String name,
            boolean isInverted,
            double P,
            double I,
            double D,
            double G,
            double Acceleration,
            double CruiseVelocity,
            double Jerk,
            boolean forwardSoftLimitEnable,
            boolean reverseSoftLimitEnable,
            double forwardSoftLimitTreshold,
            double reverseSoftLimitThreshold) {
        talon = new TalonFX(id);
        this.name = name;
        firmwareVersionSignal = talon.getVersion();
        TalonFXLogPowerFaults.setupChecks(this);
        softLimitOverrideFault = new Fault(getName() + " Device ID: " + id + " Soft Limit Overrided");

        talonFXConfigs = new TalonFXConfiguration();

        talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        talonFXConfigs.MotorOutput.Inverted = isInverted ? InvertedValue.Clockwise_Positive
                : InvertedValue.CounterClockwise_Positive;

        talonFXConfigs.Audio.BeepOnBoot = false;
        talonFXConfigs.Audio.BeepOnConfig = false;
        talonFXConfigs.Audio.AllowMusicDurDisable = true;

        talonFXConfigs.CurrentLimits.StatorCurrentLimit = 60;
        talonFXConfigs.CurrentLimits.StatorCurrentLimitEnable = true;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimit = 60;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;

        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = forwardSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = reverseSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = forwardSoftLimitTreshold;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = reverseSoftLimitThreshold;

        talonFXConfigs.Voltage.PeakForwardVoltage = 10;
        talonFXConfigs.Voltage.PeakReverseVoltage = -10;

        talon.getConfigurator().apply(talonFXConfigs);

        new TunableDouble("P", P, getName(), value -> {
            talonFXConfigs.Slot0.kP = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("I", I, getName(), value -> {
            talonFXConfigs.Slot0.kI = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("D", D, getName(), value -> {
            talonFXConfigs.Slot0.kD = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("G", G, getName(), value -> {
            talonFXConfigs.Slot0.kG = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("Acceleration", Acceleration, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicAcceleration = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("CruiseVelocity", CruiseVelocity, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicCruiseVelocity = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("Jerk", Jerk, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicJerk = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        RobotControllerTriggers.isSysActive().debounce(2).onFalse(Commands.runOnce(() -> {
            talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Coast;
            talon.getConfigurator().apply(talonFXConfigs);
        }).ignoringDisable(true));

        RobotControllerTriggers.isSysActive().onTrue(Commands.runOnce(() -> {
            talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
            talon.getConfigurator().apply(talonFXConfigs);
        }).ignoringDisable(true));
    }

    public TalonFXWrapper(int id, String name, boolean isInverted) {
        this(
                id,
                name,
                isInverted,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                false,
                false,
                0,
                0);
    }

    public void setSoftLimitsEnabled(boolean enabled) {
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = enabled;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = enabled;
        talon.getConfigurator().apply(talonFXConfigs);

        softLimitOverrideFault.setIsActive(enabled);
    }

    public void holdPosition() {
        double position = talon.getPosition().getValueAsDouble();
        talon.setControl(new MotionMagicVoltage(position));
    }

    public String getName() {
        return name + " (" + talon.getDeviceID() + ")";
    }

    public TalonFX getTalon() {
        return talon;
    }

    public void checkFault() {
        // if (firmwareVersionSignal.refresh().getError() != StatusCode.OK) {
        // fault.setIsActive(true);
        // }
    }

    public void setControl(ControlRequest controlRequest) {
        talon.setControl(controlRequest);
    }

    public void setPosition(double newPosition) {
        talon.setPosition(newPosition);
    }

    public StatusSignal<Double> getRotorPosition() {
        return talon.getRotorPosition();
    }

    // multaplying by 10 to convert duty cycle to voltage
    public void set(double speed) {
        talon.setControl(new VoltageOut(speed * 10));
    }
}
