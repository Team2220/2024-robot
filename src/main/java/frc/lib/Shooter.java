package frc.lib;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;

public class Shooter {
    TalonFXWrapper left;
    TalonFXWrapper right;

    public Shooter() {
        left = new TalonFXWrapper(0, null);
        right = new TalonFXWrapper(0, null);
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
