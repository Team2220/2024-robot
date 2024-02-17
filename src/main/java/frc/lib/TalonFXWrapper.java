package frc.lib;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.faults.TalonFXLogPowerFaults;
import frc.lib.tunables.TunableDouble;

public class TalonFXWrapper {
    private TalonFX talon;
    private String name;
    private TalonFXConfiguration talonFXConfigs;

    public TalonFXWrapper(
            int id,
            String name,
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
        TalonFXLogPowerFaults.setupChecks(this);
        talonFXConfigs = new TalonFXConfiguration();
        talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        talonFXConfigs.Audio.BeepOnBoot = false;
        talonFXConfigs.Audio.BeepOnConfig = false;
        talonFXConfigs.Audio.AllowMusicDurDisable = true;

        talonFXConfigs.CurrentLimits.StatorCurrentLimit = 40;
        talonFXConfigs.CurrentLimits.StatorCurrentLimitEnable = true;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimit = 40;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;

        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = forwardSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = reverseSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = forwardSoftLimitTreshold;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = reverseSoftLimitThreshold;

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


        RobotControllerTriggers.isSysActive().debounce(2).onFalse(Commands.runOnce(()->{
            talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Coast;
            talon.getConfigurator().apply(talonFXConfigs);
        } ).ignoringDisable(true));

          RobotControllerTriggers.isSysActive().onTrue(Commands.runOnce(()->{
            talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
            talon.getConfigurator().apply(talonFXConfigs);
        } ).ignoringDisable(true));
    }


    public TalonFXWrapper(int id, String name) {
        this(
                id,
                name,
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
    }

    public void holdPosition() {
        double position = talon.getPosition().getValueAsDouble();
        talon.setControl(new MotionMagicVoltage(position));
    }

    public String getName() {
        return name + " (" + talon.getDeviceID() + ")";
    }

    public void setInverted(boolean isInverted) {
        talon.setInverted(isInverted);
    }

    public TalonFX getTalon() {
        return talon;
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
}
