package frc.lib;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

public class TalonFXWrapper {
    private TalonFX talon;
    private String name;

    public TalonFXWrapper(int id, String name) {
        talon = new TalonFX(id);
        this.name = name;
        TalonFXLogPowerFaults.setupChecks(this);
        TalonFXRegistry.register(talon);
        talon.getConfigurator().apply(new TalonFXConfiguration());

    }

    public String getName() {
        return name + " (" + talon.getDeviceID() + ")";
    }

    public TalonFX getTalon() {
        return talon;
    }

    public void setControl(DutyCycleOut dutyLeft) {
        talon.setControl(dutyLeft);
    }


    
    public void setControlPosition(PositionDutyCycle positionDutyCycle) {
        talon.setControl(positionDutyCycle);
    }

public void setPosition(double newPosition){
    talon.setPosition(newPosition);
}
}
