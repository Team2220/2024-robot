package frc.lib;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;

public class Intake {
    TalonFXWrapper intake;
    TalonFXWrapper conveyor;

    public Intake() {
        intake = new TalonFXWrapper(0, null);
        conveyor = new TalonFXWrapper(0, null);
    }

    public Command dutyCycleCommand(DoubleSupplier leftSpeed, DoubleSupplier rightSpeed) {
        this.run(() -> {
            SetControlDutyCycle duty = new SetControlDutyCycle();
            left.setControl(DutyCycle);
            right.setControl(DutyCycle);
        });
        return null;

    }
}
