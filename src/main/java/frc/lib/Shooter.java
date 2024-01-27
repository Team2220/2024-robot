package frc.lib;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
    TalonFXWrapper left;
    TalonFXWrapper right;

    public Shooter() {
        left = new TalonFXWrapper(Constants.Shooter.id_left, "left");
        right = new TalonFXWrapper(Constants.Shooter.id_right, "right");
    }

    public Command dutyCycleCommand(DoubleSupplier leftSpeed, DoubleSupplier rightSpeed) {
        return this.run(() -> {
            DutyCycleOut dutyLeft = new DutyCycleOut(leftSpeed.getAsDouble());
            DutyCycleOut dutyRight = new DutyCycleOut(rightSpeed.getAsDouble());
            left.setControl(dutyLeft);
            right.setControl(dutyRight);
        });
    }
}
