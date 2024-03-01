package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.MotionMagicVoltage;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem {
    TalonFXWrapper ArmTalonFX;


    public Arm() {
        ArmTalonFX = new TalonFXWrapper(Constants.Arm.ARM_TALON, "Arm", false, 15, 0, 0.1, 0,
                Units.RotationsPerSecond.per(Units.Seconds).of(3000), Units.RotationsPerSecond.of(3000),
                Units.RotationsPerSecond.per(Units.Seconds).per(Units.Seconds).of(300), true, true,
                110.0 / 360.0 * Constants.Arm.ARM_GEAR_RATIO, 0);
        Shuffleboard.getTab("Arm").addDouble("ArmAngle",
                () -> ArmTalonFX.getRotorPosition().refresh().getValueAsDouble() / Constants.Arm.ARM_GEAR_RATIO * 360);

    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            var spd = speed.getAsDouble() * 10;
            ArmTalonFX.setVoltageOut(Units.Volts.of(spd));
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

    public void setDutyCycle(double value) {
        ArmTalonFX.set(value);
    }

    public void setPosition(double degrees) {
        var deg = degrees / 360.0 * Constants.Arm.ARM_GEAR_RATIO;
        ArmTalonFX.setMotionMagicVoltage(Units.Degrees.of(deg));
    }

    public void setZero() {
        ArmTalonFX.setPosition(0);
    }

    public Command setPositionCommand(double degrees) {
        return this.run(() -> {
            this.setPosition(degrees);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}