package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.SparkMaxWrapper;
import frc.lib.TalonFXWrapper;
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
        shooterSpeed = addTunableDouble("shooterSpeed", 1000);
        tolerance = addTunableDouble("tolerance", 100);
        left = new SparkMaxWrapper(Constants.Shooter.id_left, "leftShooter", true);
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter", false);

        addGraph("ShooterVelocityRight", () -> right.getVelocity() * Constants.Shooter.gear_ratio);
        addGraph("ShooterVelocityLeft", () -> left.getVelocity() * Constants.Shooter.gear_ratio);
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            left.set(speed.getAsDouble() * .85);
            right.set(speed.getAsDouble());
        });
    }

    public Command setDutyCycleCommand(double speed) {
        return this.run(() -> {
            left.set(speed);
            right.set(speed);
        }).finallyDo(() -> {
            left.set(0);
            right.set(0);
        });
    }

    public boolean isAtSetPoint() {
        return left.isAtReference(shooterSpeed.getValue(), tolerance.getValue())
                && right.isAtReference(shooterSpeed.getValue(), tolerance.getValue());
    }

    public Command velocityCommand() {
        return this.run(() -> {
            double speed = shooterSpeed.getValue();
            left.setReference(speed / Constants.Shooter.gear_ratio);
            right.setReference(speed / Constants.Shooter.gear_ratio);
        }).finallyDo(() -> {
            left.setReference(0);
            right.setReference(0);
        });
    }

    public void setSpeed(double speed) {
        left.setReference(speed / Constants.Shooter.gear_ratio);
        right.setReference(speed / Constants.Shooter.gear_ratio);
    }

    // public Command shooterReady() {
    //     return this.run(() -> {
    //         left.set(1);
    //         right.set(1);
    //         if (shooterSpeed.getValue() > 7500) {
    //             conveyor.set(.5);
    //         } else {
    //             conveyor.set(0);
    //         }
    //     });
    // }

    // public Command ampShot() {
    //     return this.run(() -> {
    //         left.set(-1);
    //         right.set(-1);
    //         if (shooterSpeed.getValue() < -7500) {
    //             conveyor.set(1);
    //         } else {
    //             conveyor.set(0);
    //         }
    //     });
    // }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}