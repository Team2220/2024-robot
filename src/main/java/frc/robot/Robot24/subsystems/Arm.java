package frc.robot.Robot24.subsystems;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.DigitalInputWrapper;
import frc.lib.devices.TalonFXWrapper;
import frc.lib.devices.TalonFXWrapper.FollowerConfig;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.PositionTalonCheck;
import frc.robot.Robot24.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    TalonFXWrapper ArmTalonFX;

    private DigitalInputWrapper zeroLimitSwitch = new DigitalInputWrapper(Constants.Arm.zeroSwitchID,
            "zeroLimitSwitch", true);

    public Arm() {
        ArmTalonFX = new TalonFXWrapper(
                Constants.Arm.ARM_TALON_LEFT,
                "Arm",
                false,
                NeutralModeValue.Coast,
                Constants.Arm.ARM_GEAR_RATIO,
                175,
                0,
                0.1,
                RotationsPerSecond.per(Seconds).of(3000),
                RotationsPerSecond.of(3000),
                RotationsPerSecond.per(Seconds).per(Seconds).of(3000),
                true,
                true,
                Rotations.of(120.0 / 360.0),
                Rotations.of(0),
                new FollowerConfig(Constants.Arm.ARM_TALON_RIGHT, true),
                Units.Seconds.of(3),
                Units.Amps.of(75),
                Units.RotationsPerSecond.of(1));
        addDouble("ArmAngle",
                () -> ArmTalonFX.getPosition().in(Degrees));
    }

    public Command toggleCoast() {
        return this.runOnce(() -> {
            if (ArmTalonFX.getNeutralMode() == NeutralModeValue.Coast) {
                ArmTalonFX.setNeutralMode(NeutralModeValue.Brake);
            } else {
                ArmTalonFX.setNeutralMode(NeutralModeValue.Coast);
            }
        }).ignoringDisable(true);
    };;

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            var spd = speed.getAsDouble() * 10;
            ArmTalonFX.setVoltageOut(Volts.of(spd));
        });
    }

    public void setNeturalMode(NeutralModeValue value) {
        ArmTalonFX.setNeutralMode(value);
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
            // System.out.println("setting" + degrees);
            this.setPosition(degrees);
        }).until(() -> atPosition(degrees, 2));
        // .finallyDo(() -> {
        // System.out.println("JITHIN: end: set angle to " + degrees);
        // });
    }

    public Command autoSetPositionOnceCommand(double degrees) {
        return this.run(() -> {
            // System.out.println("setting" + degrees);
            this.setPosition(degrees);
        }).withTimeout(.75);
        // .finallyDo(() -> {
        // System.out.println("JITHIN: end: set angle to " + degrees);
        // });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                new PositionTalonCheck(ArmTalonFX, Degrees.of(45), Degrees.of(5)),
                new PositionTalonCheck(ArmTalonFX, Degrees.of(0), Degrees.of(5))

        };
    }

}