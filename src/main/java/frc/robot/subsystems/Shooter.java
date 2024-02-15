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
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter");
    }

    public Command dutyCycleCommand(DoubleSupplier leftSpeed, DoubleSupplier rightSpeed) {
        return this.run(() -> {
            left.set(leftSpeed.getAsDouble());
            right.set(rightSpeed.getAsDouble());
        });
    }

    public Command velocityCommand(double leftSpeed, double rightSpeed) {
        return this.run(() -> {
            left.setReference(leftSpeed * Constants.Shooter.gear_ratio);
            right.setReference(rightSpeed * Constants.Shooter.gear_ratio);
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
