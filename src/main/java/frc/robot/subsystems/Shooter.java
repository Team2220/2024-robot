package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.TalonFXWrapper;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class Shooter extends SubsystemBase implements CheckableSubsystem{
    private CANSparkMax left;
    private CANSparkMax right;

    private RelativeEncoder encoder_left;
    private RelativeEncoder encoder_right;

    private SparkPIDController pid_controller_left;
    private SparkPIDController pid_controller_right;

    public double kP_left, kI_left, kD_left;
    public double kP_right, kI_right, kD_right;

    public Shooter() {
        left = new CANSparkMax(Constants.Shooter.id_left, MotorType.kBrushless);
        right = new CANSparkMax(Constants.Shooter.id_right, MotorType.kBrushless);

        left.restoreFactoryDefaults();
        right.restoreFactoryDefaults();

        pid_controller_left = left.getPIDController();
        pid_controller_right = right.getPIDController();

        encoder_left = left.getEncoder();
        encoder_right = right.getEncoder();

        pid_controller_left.setP(kP_left);
        pid_controller_left.setI(kI_left);
        pid_controller_left.setD(kD_left);

        pid_controller_right.setP(kP_right);
        pid_controller_right.setI(kI_right);
        pid_controller_right.setD(kD_right);

        // Shuffleboard.getTab("Shooter").add("left pid controller", pid_controller_left);
        // Shuffleboard.getTab("Shooter").add("right pid controller", pid_controller_right);
    }

    public Command dutyCycleCommand(DoubleSupplier leftSpeed, DoubleSupplier rightSpeed) {
        return this.run(() -> {
            left.set(leftSpeed.getAsDouble());
            right.set(rightSpeed.getAsDouble());
        });
    }

    public Command velocityCommand(double leftSpeed, double rightSpeed) {
        return this.run(() -> {
            pid_controller_left.setReference(leftSpeed * Constants.Shooter.gear_ratio, CANSparkMax.ControlType.kSmartVelocity);
            pid_controller_right.setReference(rightSpeed * Constants.Shooter.gear_ratio, CANSparkMax.ControlType.kSmartVelocity);
        }).finallyDo(() -> {
            pid_controller_left.setReference(0, CANSparkMax.ControlType.kSmartVelocity);
            pid_controller_right.setReference(0, CANSparkMax.ControlType.kSmartVelocity);
        });
    }

    @Override
    public CheckCommand[] getCheckCommands() {
       return new CheckCommand[]{};
    }
}
