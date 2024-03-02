package frc.robot.subsystems;

import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Seconds;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.SparkMaxWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.tunables.TunableDouble;
import frc.robot.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private SparkMaxWrapper left;
    private SparkMaxWrapper right;
    private TunableDouble shooterSpeed;
    private TunableDouble tolerance;

    public Shooter() {
        shooterSpeed = addTunableDouble("shooterSpeed", 7000);
        tolerance = addTunableDouble("tolerance", 300);
        left = new SparkMaxWrapper(Constants.Shooter.id_left, "leftShooter", true, 0.000115, 0, 0,
                RPM.per(Seconds).of(0), RPM.of(0), 0);
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter", false, 0.000115, 0, 0,
                RPM.per(Seconds).of(0), RPM.of(0), 0);

        addGraph("ShooterVelocityRight", () -> right.getVelocity() * Constants.Shooter.gear_ratio);
        addGraph("ShooterVelocityLeft", () -> left.getVelocity() * Constants.Shooter.gear_ratio);
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            left.set(speed.getAsDouble());
            right.set(speed.getAsDouble());
        });
    }

    public Command setDutyCycleCommand(double speed) {
        return this.run(() -> {
            left.set(speed);
            right.set(speed);
        });
    }

    public boolean isAtSetPoint() {
        return left.isAtReference(shooterSpeed.getValue() / Constants.Shooter.gear_ratio, tolerance.getValue())
                && right.isAtReference(shooterSpeed.getValue() / Constants.Shooter.gear_ratio, tolerance.getValue());
    }

    public Command velocityCommand() {
        return this.run(() -> {
            double speed = shooterSpeed.getValue();
            left.setReference(speed * Constants.Shooter.gear_ratio);
            right.setReference(speed * Constants.Shooter.gear_ratio);
        }).finallyDo(() -> {
            left.setReference(0);
            right.setReference(0);
        });
    }

    public Command velocityCommandy() {
        return this.run(() -> {
            double speed = shooterSpeed.getValue();
            left.setReference(speed * Constants.Shooter.gear_ratio * -1);
            right.setReference(speed * Constants.Shooter.gear_ratio * -1);
        }).finallyDo(() -> {
            left.setReference(0);
            right.setReference(0);
        });
    }

    public void setDefaultSpeed(boolean forward) {
        double speed = shooterSpeed.getValue() * (forward ? 1 : -1);
        left.setReference(speed * Constants.Shooter.gear_ratio);
        right.setReference(speed * Constants.Shooter.gear_ratio);
    }

    public void stopShooter() {
        left.set(0);
        right.set(0);
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}