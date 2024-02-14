package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Intake extends SubsystemBase implements CheckableSubsystem {
    private CANSparkMax intakeRev;
    private SparkPIDController intakePidController;
    public double kP_intake, kI_intake, kD_intake;
    // TalonFXWrapper conveyor;

    public Intake() {
        intakeRev = new CANSparkMax(Constants.Intake.id_intake, MotorType.kBrushless);

        intakeRev.restoreFactoryDefaults();

        intakePidController = intakeRev.getPIDController();

        intakePidController.setP(kP_intake);
        intakePidController.setI(kI_intake);
        intakePidController.setD(kD_intake);

        // Shuffleboard.getTab("Intake").add("Intake PID Controller", intakePidController);
        // conveyor = new TalonFXWrapper(Constants.Intake.id_conv, "conveyor");
    }

    public Command dutyCycleCommand(DoubleSupplier speed) {
        return this.run(() -> {
            DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
            // intakeRev.setControl(duty);
            // conveyor.setControl(duty);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }
}
