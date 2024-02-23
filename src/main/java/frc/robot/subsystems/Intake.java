package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.DigitalInputWrapper;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.SparkMaxWrapper;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SpinTalonCheck;
import frc.lib.tunables.TunableDouble;
import frc.robot.Constants;

public class Intake extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private SparkMaxWrapper intake;
    private TunableDouble intakeSpeed;
    private TalonFXWrapper conveyor;

    private DigitalInputWrapper topNoteSensor = new DigitalInputWrapper(Constants.Intake.noteSensorId, "noteSensor", true);

    public Intake() {
        intakeSpeed = addTunableDouble("intakeSpeed", .5);
        intake = new SparkMaxWrapper(Constants.Intake.id_intake, "intake");
        conveyor = new TalonFXWrapper(Constants.Intake.id_conv, "conveyor", true);
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {

        return this.run(() -> {
            intake.set(speed.getAsDouble());
            conveyor.set(speed.getAsDouble());
        });
    }

    public void setSpeed(double speed) {
        intake.set(speed);
        conveyor.set(speed);
    }

    public Command intakeUntilQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(0.5);
                conveyor.set(0.5);
            } else {
                intake.set(intakeSpeed.getValue());
                conveyor.set(intakeSpeed.getValue());
            }
        });
    }

    public Command intakeUntilNotQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(.5);
                conveyor.set(.5);
            } else {
                intake.set(0);
                conveyor.set(0);
            }
        });
    }

    public Command setIntakeUntilQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(0);
                conveyor.set(0);
            } else {
                intake.set(.5);
                conveyor.set(.5);
            }
        });
    }

    public Command setintakeUntilNotQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(.5);
                conveyor.set(.5);
            } else {
                intake.set(0);
                conveyor.set(0);
            }
        });
    }

    public Command setDutyCycleCommand(double speed) {
        return this.run(() -> {
            intake.set(speed);
            conveyor.set(speed);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                new SpinTalonCheck(conveyor),
        };
    }
}
