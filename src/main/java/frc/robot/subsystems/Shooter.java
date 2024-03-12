package frc.robot.subsystems;

import static frc.lib.units.UnitsUtil.RotationsPerSecSquared;

import java.util.function.DoubleSupplier;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SpinTalonCheck;
import frc.lib.tunables.TunableMeasure;
import frc.lib.units.UnitsUtil;
import frc.robot.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private TalonFXWrapper left;
    private TalonFXWrapper right;
    private TunableMeasure<Velocity<Angle>> shooterSpeed;
    private TunableMeasure<Velocity<Angle>> tolerance;

    public Shooter() {
        shooterSpeed = new TunableMeasure<>("shooterSpeed", Units.RPM.of(7000), "Shooter");
        tolerance = new TunableMeasure<>("tolerance", Units.RPM.of(300), "Shooter");
        left = new TalonFXWrapper(
                Constants.Shooter.id_left,
                "leftShooter",
                true,
                Constants.Shooter.gear_ratio,
                1,
                0,
                0,
                RotationsPerSecSquared.of(0),
                Units.RotationsPerSecond.of(0),
                UnitsUtil.RotationsPerSecCubed.of(0),
                false,
                false,
                Units.Rotations.of(0),
                Units.Rotations.of(0),
                null);

        right = new TalonFXWrapper(
                Constants.Shooter.id_right,
                "rightShooter",
                false,
                Constants.Shooter.gear_ratio,
                1,
                0,
                0,
                RotationsPerSecSquared.of(0),
                Units.RotationsPerSecond.of(0),
                UnitsUtil.RotationsPerSecCubed.of(0),
                false,
                false,
                Units.Rotations.of(0),
                Units.Rotations.of(0),
                null);

        addGraph("ShooterVelocityRight", () -> right.getVelocity(), Units.RPM);
        addGraph("ShooterVelocityLeft", () -> left.getVelocity(), Units.RPM);
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
        return // left.isAtReference(shooterSpeed.getValue(), tolerance.getValue())
               // &&
        right.isAtReference(shooterSpeed.getValue(), tolerance.getValue());
    }

    public Command velocityCommand() {
        return this.run(() -> {
            var speed = shooterSpeed.getValue();
            left.setVelocity(speed);
            right.setVelocity(speed);
        }).finallyDo(() -> {
            left.setVelocity(Units.RPM.of(0));
            right.setVelocity(Units.RPM.of(0));
        });
    }

    public void setDefaultSpeed() {
        Measure<Velocity<Angle>> speed = shooterSpeed.getValue();
        left.setVelocity(speed);
        right.setVelocity(speed);
    }

    public Command velocityCommandy() {
        return this.run(() -> {
            var speed = shooterSpeed.getValue();
            left.setVelocity(speed.times(-1));
            right.setVelocity(speed.times(-1));
        }).finallyDo(() -> {
            left.setVelocity(Units.RPM.of(0));
            right.setVelocity(Units.RPM.of(0));
        });
    }

    public void stopShooter() {
        left.set(0);
        right.set(0);
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                new SpinTalonCheck(left, true),
                new SpinTalonCheck(left, false),
                new SpinTalonCheck(right, true),
                new SpinTalonCheck(right, false),
        };
    }

}