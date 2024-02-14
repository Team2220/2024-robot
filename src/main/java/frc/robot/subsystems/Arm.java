package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;

public class Arm extends SubsystemBase implements CheckableSubsystem {
    private CANSparkMax armSpark;
    private SparkPIDController armPidController;
    public double kP_arm, kI_arm, kD_arm;
    final PositionDutyCycle m_positionDutyCycle = new PositionDutyCycle(0);

    public Arm() {
        armSpark = new CANSparkMax(Constants.Arm.arm_id, MotorType.kBrushless);

        armSpark.restoreFactoryDefaults();

        armPidController = armSpark.getPIDController();

        armPidController.setP(kP_arm);
        armPidController.setI(kI_arm);
        armPidController.setD(kD_arm);

        Shuffleboard.getTab("Arm").add("Arm PID Controller", armPidController);
    }

    // public Command dutyCycleCommand(DoubleSupplier speed) {
    // return this.run(() -> {
    // DutyCycleOut duty = new DutyCycleOut(speed.getAsDouble());
    // talon.setControl(duty);

    // });
    // }
    public void setPosition(double degrees) {

    }

    public void setZero() {
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {};
    }

}