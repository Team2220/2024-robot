package frc.robot.Robot24.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.DigitalInputWrapper;
import frc.lib.devices.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SpinTalonCheck;
import frc.lib.tunables.TunableDouble;
import frc.robot.Robot24.Constants;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static frc.lib.units.UnitsUtil.RotationsPerSecCubed;
import static frc.lib.units.UnitsUtil.RotationsPerSecSquared;

public class Intake extends SubsystemBase implements CheckableSubsystem, ShuffleBoardTabWrapper {
    private TalonFXWrapper intake;
    private TunableDouble intakeSpeed;
    private TalonFXWrapper conveyor;

    private DigitalInputWrapper topNoteSensor = new DigitalInputWrapper(Constants.Intake.noteSensorId, "noteSensor",
            true);
    private DigitalInputWrapper bottomNoteSensor = new DigitalInputWrapper(Constants.Intake.bottomNoteSensorID,
            "bottomNoteSensor", true);

    public Intake() {
        intakeSpeed = addTunableDouble("intakeSpeed", .75);
        intake = new TalonFXWrapper(
                Constants.Intake.id_intake,
                "intake",
                false,
                NeutralModeValue.Brake,
                1,
                0,
                0,
                0,
                RotationsPerSecSquared.of(0),
                RotationsPerSecond.of(0),
                RotationsPerSecCubed.of(0),
                false,
                false,
                Rotations.of(0),
                Rotations.of(0),
                null,
                Units.Seconds.of(1),
                Units.Amps.of(55), Units.RotationsPerSecond.of(1));
        conveyor = new TalonFXWrapper(Constants.Intake.id_conv, "conveyor", true, NeutralModeValue.Brake);
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {

        return this.run(() -> {
            intake.set(speed.getAsDouble() * .75);
            conveyor.set(speed.getAsDouble() * .75);
        });
    }

    public void setSpeed(double speed) {
        intake.set(speed);
        conveyor.set(speed);
    }
    public void setSpeed(double iSpeed, double cSpeed) {
        intake.set(iSpeed);
        conveyor.set(cSpeed);
    }

    public Command intakeUntilQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(0);
                conveyor.set(0);

            } else {
                intake.set(intakeSpeed.getValue());
                conveyor.set(intakeSpeed.getValue());
                // System.out.println(intakeSpeed.getValue());
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

    public boolean bottomNoteGet() {
        return bottomNoteSensor.get();

    }

    public boolean isStalled() {
        return intake.isStalled();
    }

    public Command setIntakeUntilQueued() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(0);
                conveyor.set(0);
            } else {
                intake.set(.75);
                conveyor.set(.75);
            }
        });
    }
    public Command setIntakeUntilQueuedSlow() {
        return this.run(() -> {
            if (topNoteSensor.get()) {
                intake.set(0);
                conveyor.set(0);
            } else {
                intake.set(.65);
                conveyor.set(.35);
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
                new SpinTalonCheck(conveyor, true),
                new SpinTalonCheck(conveyor, false),
                new SpinTalonCheck(intake, true),
                new SpinTalonCheck(intake, false),
        };
    }

    public boolean getBottomNoteSensor() {
        return bottomNoteSensor.get();
    }

    public boolean getTopNoteSensor() {
        return topNoteSensor.get();
    }
}
