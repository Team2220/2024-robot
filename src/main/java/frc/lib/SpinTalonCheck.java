package frc.lib;

import com.ctre.phoenix6.controls.DutyCycleOut;

import frc.lib.selfCheck.CheckCommand;

public class SpinTalonCheck extends CheckCommand {
    TalonFXWrapper talon;
    double position;

    public SpinTalonCheck(TalonFXWrapper talon) {
        this.talon = talon;
    }

    @Override
    public void initialize() {
        position = talon.getRotorPosition().getValueAsDouble();

    }
    @Override
    public boolean isFinished() {
        return Math.abs(position - talon.getRotorPosition().getValueAsDouble()) > 10;
    }

    @Override
    public
    double getTimeoutSeconds() {
        return 10;

    }
    @Override
    public void end(boolean interrupted) {
        talon.setControl(new DutyCycleOut(0));
    }
    @Override
    public void execute() {
        talon.setControl(new DutyCycleOut(0.25));
    }
}