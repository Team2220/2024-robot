package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.robot.Constants;

public class Arm extends SubsystemBase {
    TalonFXWrapper talon;

    public Arm() {
        talon = new TalonFXWrapper(Constants.Arm.talon_id, "Arm");
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
            talon.setControl(duty);

        });
    }
}