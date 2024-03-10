package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import java.util.function.DoubleSupplier;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.TalonFXWrapper;
import frc.lib.TalonFXWrapper.FollowerConfig;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    TalonFXWrapper ArmTalonFX;

    public Arm() {
        ArmTalonFX = new TalonFXWrapper(
                Constants.Arm.ARM_TALON_LEFT,
                "Arm",
                false,
                Constants.Arm.ARM_GEAR_RATIO,
                150,
                0,
                0.1,
                RotationsPerSecond.per(Seconds).of(3000),
                RotationsPerSecond.of(3000),
                RotationsPerSecond.per(Seconds).per(Seconds).of(3000), true, true,
                Rotations.of(110.0 / 360.0), Rotations.of(0),
                new FollowerConfig(Constants.Arm.ARM_TALON_RIGHT, true)
                );
        addDouble("ArmAngle",
                () -> ArmTalonFX.getPosition().in(Units.Degrees));
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            var spd = speed.getAsDouble() * 10;
            ArmTalonFX.setVoltageOut(Volts.of(spd));
        });
    }

    public Command overrideSoftLimits() {
        return Commands.startEnd(() -> {
            ArmTalonFX.setSoftLimitsEnabled(false);
        }, () -> {
            ArmTalonFX.setSoftLimitsEnabled(true);
        });
    }

    public void holdPosition() {
        ArmTalonFX.holdPosition();
    }

    public boolean atPosition(double degrees, double tolarance) {
        return Math.abs((degrees) - (ArmTalonFX.getPosition().in(Degrees))) <= tolarance;
    }

    public void setDutyCycle(double value) {
        ArmTalonFX.set(value);
    }

    public void setPosition(double degrees) {
        var deg = degrees / 360.0;
        ArmTalonFX.setMotionMagicVoltage(Rotations.of(deg));
    }

    public void setZero() {
        ArmTalonFX.setPosition(0);
    }

    public Command setPositionCommand(double degrees) {
        return this.run(() -> {
            this.setPosition(degrees);
        });
        // .until(() -> atPosition(degrees, 2));
    }

    public Command setPositionOnceCommand(double degrees) {
        return this.run(() -> {
            this.setPosition(degrees);
        }).until(() -> atPosition(degrees, 2));
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}