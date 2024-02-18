package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.SparkMaxWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.tunables.TunableDouble;
import frc.robot.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem {
    private SparkMaxWrapper left;
    private SparkMaxWrapper right;
    private TunableDouble shooterSpeed;

    public Shooter() {
        shooterSpeed = new TunableDouble("shooterSpeed", 100, "shooter");
        left = new SparkMaxWrapper(Constants.Shooter.id_left, "leftShooter");
        left.setInverted(true);
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter");
        right.setInverted(false);
        Shuffleboard.getTab("ShooterSpeed")
                .addDouble("ShooterVelocityRight", () -> right.getVelocity() * Constants.Shooter.gear_ratio)
                .withWidget(BuiltInWidgets.kGraph);

        Shuffleboard.getTab("ShooterSpeed")
                .addDouble("ShooterVelocityLeft", () -> left.getVelocity() * Constants.Shooter.gear_ratio)
                .withWidget(BuiltInWidgets.kGraph);
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
        }).finallyDo(() -> {
            left.set(0);
            right.set(0);
        });
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

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }
}
