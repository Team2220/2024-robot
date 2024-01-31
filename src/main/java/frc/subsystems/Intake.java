package frc.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
    TalonFXWrapper intake;
    TalonFXWrapper conveyor;

    public Intake() {
        intake = new TalonFXWrapper(Constants.Intake.id_intake, "intake");
        conveyor = new TalonFXWrapper(Constants.Intake.id_conv, "conveyor");
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
            intake.setControl(duty);
            conveyor.setControl(duty);
        });
    }
}