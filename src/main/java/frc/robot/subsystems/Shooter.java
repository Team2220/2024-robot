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
import frc.lib.SparkMaxWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SparkMAXSpinCheck;
import frc.lib.tunables.TunableMeasure;
import frc.robot.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private SparkMaxWrapper left;
    private SparkMaxWrapper right;
    private TunableMeasure<Velocity<Angle>> shooterSpeed;
    private TunableMeasure<Velocity<Angle>> tolerance;

    public Shooter() {
        shooterSpeed = new TunableMeasure<>("shooterSpeed", Units.RPM.of(7000), "Shooter");
        tolerance = new TunableMeasure<>("tolerance", Units.RPM.of(300), "Shooter");
        left = new SparkMaxWrapper(Constants.Shooter.id_left, "leftShooter", true, Constants.Shooter.gear_ratio,
                0.000115, 0, 0, RotationsPerSecSquared.of(0), Units.RotationsPerSecond.of(0), 0);
        right = new SparkMaxWrapper(Constants.Shooter.id_right, "rightShooter", false, Constants.Shooter.gear_ratio,
                0.000115, 0, 0, RotationsPerSecSquared.of(0), Units.RotationsPerSecond.of(0), 0);

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
            left.setReference(speed);
            right.setReference(speed);
        }).finallyDo(() -> {
            left.setReference(Units.RPM.of(0));
            right.setReference(Units.RPM.of(0));
        });
    }

    public void setDefaultSpeed() {
        Measure<Velocity<Angle>> speed = shooterSpeed.getValue();
        left.setReference(speed);
        right.setReference(speed);
    }

    public Command velocityCommandy() {
        return this.run(() -> {
            var speed = shooterSpeed.getValue();
            left.setReference(speed.times(-1));
            right.setReference(speed.times(-1));
        }).finallyDo(() -> {
            left.setReference(Units.RPM.of(0));
            right.setReference(Units.RPM.of(0));
        });
    }

    public void stopShooter() {
        left.set(0);
        right.set(0);
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                new SparkMAXSpinCheck(left, true),
                new SparkMAXSpinCheck(left, false),
                new SparkMAXSpinCheck(right, true),
                new SparkMAXSpinCheck(right, false),
        };
    }

}