package frc.lib.selfCheck;

import static edu.wpi.first.units.Units.Rotations;
import static frc.lib.units.UnitsUtil.abs;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import frc.lib.devices.TalonFXWrapper;

public class SpinTalonCheck extends CheckCommand {
    TalonFXWrapper talon;
    Measure<Angle> position;
    boolean isForward;

    public SpinTalonCheck(TalonFXWrapper talon, boolean isForward) {
        this.talon = talon;
        this.isForward = isForward;
    }

    @Override
    public void initialize() {
        position = talon.getPosition();
    }

    @Override
    public boolean isFinished() {
        return abs(position.minus(talon.getPosition())).gt(Rotations.of(10));
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
        talon.setDutyCycleOut(isForward ? 0.25 : -0.25);
    }

    @Override
    public String getDescription() {
        return "spin " + talon.getName() + (isForward ? "forward" : "backward");
    }

}