package frc.robot.Robot24.subsystems;

import static frc.lib.units.UnitsUtil.RotationsPerSecSquared;

import java.util.function.DoubleSupplier;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SpinTalonCheck;
import frc.lib.tunables.TunableMeasure;
import frc.lib.units.UnitsUtil;
import frc.robot.Robot24.Constants;

public class Shooter extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private TalonFXWrapper left;
    private TalonFXWrapper right;
    private TunableMeasure<Velocity<Angle>> shooterSpeed;
    private TunableMeasure<Velocity<Angle>> tolerance;
    private double desiredspeed = 0;

    public Shooter() {
        shooterSpeed = new TunableMeasure<>("shooterSpeed", Units.RPM.of(7000), "Shooter");
        tolerance = new TunableMeasure<>("tolerance", Units.RPM.of(300), "Shooter");
        left = new TalonFXWrapper(
                Constants.Shooter.id_left,
                "leftShooter",
                true,
                NeutralModeValue.Coast,
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
                null, Units.Seconds.of(3), Units.Amps.of(75), Units.RotationsPerSecond.of(1));

        right = new TalonFXWrapper(
                Constants.Shooter.id_right,
                "rightShooter",
                false,
                NeutralModeValue.Coast,
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
                null, Units.Seconds.of(3), Units.Amps.of(75), Units.RotationsPerSecond.of(1));

        addGraph("ShooterVelocityRight", () -> right.getVelocity(), Units.RPM);
        addGraph("ShooterVelocityLeft", () -> left.getVelocity(), Units.RPM);
    }

    public boolean isGoingWrongWay() {
        if (this.desiredspeed > 0
                && (left.getVelocity().lt(Units.RPM.of(0)) || (right.getVelocity().lt(Units.RPM.of(0))))) {
            return true;
        }
        if (this.desiredspeed < 0
                && (left.getVelocity().gt(Units.RPM.of(0)) || (right.getVelocity().gt(Units.RPM.of(0))))) {
            return true;
        }
        else{

            return false;
        }
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            this.desiredspeed = speed.getAsDouble();
            left.set(speed.getAsDouble());
            right.set(speed.getAsDouble());
        });
    }

    public Command setDutyCycleCommand(double speed) {
        return this.run(() -> {
            this.desiredspeed = speed;
            left.set(speed);
            right.set(speed);
        })
                .finallyDo(() -> {
                    left.set(0);
                    right.set(0);
                    this.desiredspeed = 0;
                });
    }

    public Command setyDutyCycleCommand() {
        return this.run(() -> {
            this.desiredspeed = -1;
            left.set(-1);
            right.set(-1);
        })
                .finallyDo(() -> {
                    this.desiredspeed = 0;
                    left.set(0);
                    right.set(0);
                });
    }

    // public boolean isAtSetPoint() {
    // return // left.isAtReference(shooterSpeed.getValue(), tolerance.getValue())
    // // &&
    // right.isAtReference(shooterSpeed.getValue(), tolerance.getValue());
    // }

    // public Command velocityCommand() {
    // return this.run(() -> {
    // var speed = shooterSpeed.getValue();
    // left.setVelocity(speed);
    // right.setVelocity(speed);
    // }).finallyDo(() -> {
    // left.setVelocity(Units.RPM.of(0));
    // right.setVelocity(Units.RPM.of(0));
    // });
    // }

    // public void setDefaultSpeed() {
    // Measure<Velocity<Angle>> speed = shooterSpeed.getValue();
    // left.setVelocity(speed);
    // right.setVelocity(speed);
    // }

    public void setDutyCycle(double speed) {
        left.set(speed);
        right.set(speed);
        this.desiredspeed = speed;
    }

    // public void setDefaultySpeed() {
    // Measure<Velocity<Angle>> speed = shooterSpeed.getValue();
    // left.setVelocity(speed.times(-1));
    // right.setVelocity(speed.times(-1));
    // }
    // public Command velocityCommandy() {
    // return this.run(() -> {
    // var speed = shooterSpeed.getValue();
    // left.setVelocity(speed.times(-1));
    // right.setVelocity(speed.times(-1));
    // }).finallyDo(() -> {
    // left.setVelocity(Units.RPM.of(0));
    // right.setVelocity(Units.RPM.of(0));
    // });
    // }

    public void stopShooter() {
        left.set(0);
        right.set(0);
        this.desiredspeed = 0;
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