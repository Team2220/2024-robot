// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.lib.PDHLogPowerFaults;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private final DriveTrain driveTrain = new DriveTrain();
  // The robot's subsystems and commands are defined here...
  private final PowerDistribution m_PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
      private final CommandXboxController m_codriverController =
      new CommandXboxController(OperatorConstants.kCodriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    PDHLogPowerFaults.setPdh(m_PowerDistribution);
    // Configure the trigger bindings
    configureBindings();
    driveTrain.setDefaultCommand(driveTrain.driveCommand(() -> {
      return MathUtil.applyDeadband(m_driverController.getLeftX(), 0.1);      
    }, () -> {
      return MathUtil.applyDeadband(m_driverController.getLeftY() * -1, 0.1);
    }, () -> {
      return MathUtil.applyDeadband(m_driverController.getRightX(), 0.1);

    }));
  }
  

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    m_driverController.a().onTrue(driveTrain.zeroCommand());  
    m_driverController.x().whileTrue((driveTrain.xcommand()));
    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto();
  }
}
