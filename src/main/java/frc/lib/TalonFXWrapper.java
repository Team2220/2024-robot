package frc.lib;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.AudioConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

public class TalonFXWrapper {
    private TalonFX talon;
    private String name;

    public TalonFXWrapper(int id, String name) {
        talon = new TalonFX(id);
        this.name = name;
        // TalonFXLogPowerFaults.setupChecks(this);
        talon.getConfigurator().apply(new TalonFXConfiguration());
        var audioConfigs = new AudioConfigs();
        audioConfigs.BeepOnBoot = false;
        audioConfigs.BeepOnConfig = false;
        audioConfigs.AllowMusicDurDisable = true;
        talon.getConfigurator().apply(audioConfigs);
        var currentLimConfig = new CurrentLimitsConfigs();
        currentLimConfig.StatorCurrentLimit = 40;
        currentLimConfig.StatorCurrentLimitEnable = true;
        currentLimConfig.SupplyCurrentLimit = 40;
        currentLimConfig.SupplyCurrentLimitEnable = true;
        talon.getConfigurator().apply(currentLimConfig);
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
