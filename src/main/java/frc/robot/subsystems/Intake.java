package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.DigitalInputWrapper;
import frc.lib.SparkMaxWrapper;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SpinTalonCheck;
import frc.lib.tunables.TunableDouble;
import frc.robot.Constants;

public class Intake extends SubsystemBase implements CheckableSubsystem {
    private SparkMaxWrapper intake;
    private TunableDouble intakeSpeed;
    private TalonFXWrapper conveyor;

    private DigitalInputWrapper noteSensor = new DigitalInputWrapper(Constants.Intake.noteSensorId, "noteSensor", true);

    public Intake() {
        intakeSpeed = new TunableDouble("intakeSpeed", .5, "intake");
        intake = new SparkMaxWrapper(Constants.Intake.id_intake, "intake");
        conveyor = new TalonFXWrapper(Constants.Intake.id_conv, "conveyor");
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {

        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble() * -1);
            intake.set(speed.getAsDouble());
            conveyor.setControl(duty);
        });
    }

    public Command intakeUntilQueued() {
        return this.run(() -> {
            if (noteSensor.get()) {
                intake.set(0);
                conveyor.setControl(new DutyCycleOut(0));
            } else {
                intake.set(intakeSpeed.getValue());
                conveyor.setControl(new DutyCycleOut(intakeSpeed.getValue()));
            }
        });
    }

    public Command setDutyCycleCommand(double speed) {
        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed);
            intake.set(speed);
            conveyor.setControl(duty);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                new SpinTalonCheck(conveyor),
        };
    }
}
