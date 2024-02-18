package frc.lib.selfCheck;


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

    }
    @Override
    public boolean isFinished() {
        return Math.abs(position - talon.getRotorPosition().getValueAsDouble()) > 20;
    }

    @Override
    public
    double getTimeoutSeconds() {
        return 3;

    }
    @Override
    public void end(boolean interrupted) {
        talon.set (0);
    }
    @Override
    public void execute() {
        talon.set (0.25);
    }
}