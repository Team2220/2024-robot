package frc.lib.selfCheck;

import com.ctre.phoenix6.controls.DutyCycleOut;

import frc.lib.TalonFXWrapper;

public class SpinTalonCheck extends CheckCommand {
    TalonFXWrapper talon;
    double position;

    public SpinTalonCheck(TalonFXWrapper talon) {
        this.talon = talon;
    }

    @Override
    public void initialize() {
        position = talon.getRotorPosition().getValueAsDouble();
        System.out.println(position);


    }

    @Override
    public boolean isFinished() {
        System.out.println(talon.getRotorPosition().getValueAsDouble());
        return Math.abs(position - talon.getRotorPosition().getValueAsDouble()) > 10;
        
    }

    @Override
    public double getTimeoutSeconds() {
        return 3;

    }

    @Override
    public void end(boolean interrupted) {
        talon.setDutyCycleOut(0);
    }

    @Override
    public void execute() {
        talon.setDutyCycleOut(0.25);
    }
}