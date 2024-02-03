package frc.lib;

public class UnconstrainedTalonFXWrapperCheck extends CheckCommand {
    TalonFXWrapper talon;
    double position;

    public UnconstrainedTalonFXWrapperCheck(TalonFXWrapper talon) {
        this.talon = talon;
    }

    // @Override
    // public void initialize() {
    //     position = talon.getRotorPosition().getValueAsDouble();

    // }
    // @Override
    // public boolean isFinished() {
    //     return Math.abs(position - talon.getRotorPosition().getValueAsDouble()) > 10;
    // }

    @Override
    double getTimeoutSeconds() {
        return 10;

    }

}