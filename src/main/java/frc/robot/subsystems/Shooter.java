package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.SparkMaxWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem {
    private SparkMaxWrapper left;
    private SparkMaxWrapper right;

    public Shooter() {
        left = new SparkMaxWrapper(Constants.Shooter.id_left, "leftShooter");
        left.setInverted(true);
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter");
        right.setInverted(false);
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

    public Command velocityCommand(double speed) {
        return this.run(() -> {
            left.setReference(speed * Constants.Shooter.gear_ratio);
            right.setReference(speed * Constants.Shooter.gear_ratio);
        }).finallyDo(() -> {
            left.setReference(0);
            right.setReference(0);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }
}
