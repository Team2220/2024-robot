package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem {
    TalonFXWrapper ArmTalonFX;

    final PositionDutyCycle m_positionDutyCycle = new PositionDutyCycle(0);

    public Arm() {

        ArmTalonFX = new TalonFXWrapper(Constants.Arm.ARM_TALON, "Arm");
        TunableTalonFX.addTunableTalonFX(ArmTalonFX.getTalon(), 0, 0, 0, 0);
    }

    // public Command dutyCycleCommand(DoubleSupplier speed) {
    // return this.run(() -> {
    // DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
    // talon.setControl(duty);

    // });
    // }
    public void setPosition(double degrees) {

        ArmTalonFX.setControlPosition(m_positionDutyCycle.withPosition(degrees / 360 * Constants.Arm.ARM_GEAR_RATIO));
    }

    public void setZero() {
        ArmTalonFX.setPosition(0);
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}