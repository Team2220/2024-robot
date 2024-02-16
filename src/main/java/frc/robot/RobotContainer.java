// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.lib.CommandXBoxWrapper;
import frc.lib.RobotSelfCheckCommand;
import frc.lib.TalonOrchestra;
import frc.lib.faults.PDHLogPowerFaults;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // private CANdle left = new CANdle(Constants.LEDS.LEFT);
  // private CANdle right = new CANdle(Constants.LEDS.RIGHT);
  @SuppressWarnings("unused")
  // private final LEDs m_leds;
  private final DriveTrain driveTrain = new DriveTrain();
  // The robot's subsystems and commands are defined here...
  private final PowerDistribution m_PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();
  private final SendableChooser<Command> autoChooser;
  private final CommandXBoxWrapper m_driverController = new CommandXBoxWrapper(OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper m_operatorController = new CommandXBoxWrapper(
      OperatorConstants.kOperatorControllerPort);

  private final Shooter shooter = new Shooter();
  private final Arm m_arm = new Arm();
  private final Intake intake = new Intake();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    PDHLogPowerFaults.setPdh(m_PowerDistribution, 8, 12, 13, 14, 15, 16, 17, 22, 23);
    Shuffleboard.getTab("can").addDouble("can utilization", () -> RobotController.getCANStatus().percentBusUtilization)
        .withWidget(BuiltInWidgets.kGraph);
    // GetMACAddress.main();
    // Configure the trigger bindings
    configureBindings();

    shooter.setDefaultCommand(shooter.dutyCycleCommand(() -> {
      return m_operatorController.getLeftTriggerAxis(0);
    }, () -> {
      return m_operatorController.getRightTriggerAxis(0);
    }));

    intake.setDefaultCommand(intake.dutyCycleCommand(() -> {
      return m_operatorController.getRightY(0.1);
    }));

    var armCommand = Commands.run(() -> {
      var joyStickPosition = m_operatorController.getLeftY(0.1);
      if (joyStickPosition > 0.01 || joyStickPosition < -0.01) {
        m_arm.setDutyCycle(joyStickPosition);
      } else {
        m_arm.holdPosition();
      }
    }, m_arm);
    m_operatorController.leftYTrigger(0.1).onTrue(armCommand);
    m_arm.setDefaultCommand(armCommand);

    m_operatorController.a().onTrue(Commands.runOnce(m_arm::setZero, m_arm));
    m_operatorController.y().onTrue(m_arm.setPositionCommand(90));
    m_operatorController.b().onTrue(m_arm.setPositionCommand(45));

    var driveCommand = driveTrain.driveCommand(() -> {
      return m_driverController.getLeftX(0.1) * -1;
    }, () -> {
      return m_driverController.getLeftY(0.1) * -1;
    }, () -> {
      return m_driverController.getRightX(0.15) * -1;
    });
    driveTrain.setDefaultCommand(driveCommand);
    m_driverController.joysticksTrigger(.1).onTrue(driveCommand);

    // m_leds = new LEDs(
    // new LedSegment[] { new LedSegment(left), new LedSegment(right) },
    // new LedSignal[] {
    // LedSignal.isBrownedOut(),
    // LedSignal.isDSConnected(),
    // // LedSignal.hasTarget(),
    // LedSignal.isEndGame(),
    // // LedSignal.hasActiveFault(),
    // LedSignal.getLowBatteryLedSignal()
    // });
    autoChooser = AutoBuilder.buildAutoChooser();

    // Another option that allows you to specify the default auto by its name
    // autoChooser = AutoBuilder.buildAutoChooser("My Default Auto");

    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    m_driverController.a().onTrue(driveTrain.zeroCommand());
    m_driverController.x().whileTrue((driveTrain.xcommand()));
    m_driverController.y().onTrue(new TalonOrchestra(driveTrain));
    m_driverController.b().onTrue(driveTrain.slowMode());
    // Schedule `exampleMethodCommand` when the Xbox controller's B button is
    // pressed,
    // cancelling on release.
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }

  public Command getTestCommand() {
    return new RobotSelfCheckCommand(driveTrain);
  }
}
