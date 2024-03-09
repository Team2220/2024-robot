package frc.lib.selfCheck;

import static edu.wpi.first.units.Units.Rotations;
import static frc.lib.units.UnitsUtil.abs;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import frc.lib.TalonFXWrapper;

public class SpinTalonCheck extends CheckCommand {
    TalonFXWrapper talon;
    Measure<Angle> position;

    public SpinTalonCheck(TalonFXWrapper talon) {
        this.talon = talon;
    }

    @Override
    public void initialize() {
        position = talon.getPosition();
        System.out.println(position);
    }

    @Override
    public boolean isFinished() {
        System.out.println(talon.getPosition());
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
        talon.setDutyCycleOut(0.25);
    }
}