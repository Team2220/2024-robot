package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem {
    TalonFXWrapper ArmTalonFX;

    final MotionMagicVoltage m_positionDutyCycle = new MotionMagicVoltage(0);

   // public Arm() {
       // ArmTalonFX = new TalonFXWrapper(Constants.Arm.ARM_TALON, "Arm", 15, 0, 0.1, 0, 3000, 3000,
       //         3000, true, true, 0, 90 * Constants.Arm.ARM_GEAR_RATIO);
      //  ArmTalonFX.setInverted(true);
   // }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
            ArmTalonFX.setControl(duty);
        });
    }

    public void holdPosition() {
        ArmTalonFX.holdPosition();
    }

    public void setDutyCycle(double value) {
        DutyCycleOut duty = new DutyCycleOut(value);
        ArmTalonFX.setControl(duty);
    }

    public void setPosition(double degrees) {

        ArmTalonFX.setControl(m_positionDutyCycle.withPosition(degrees / 360.0 * Constants.Arm.ARM_GEAR_RATIO));
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