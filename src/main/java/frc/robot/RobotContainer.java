// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.lib.CommandXBoxWrapper;
import frc.lib.LimelightPortForwarding;
import frc.lib.MusicToneCommand;
import frc.lib.Note;
import frc.lib.can.CanStream;
import frc.lib.faults.Fault;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.leds.LEDs;
import frc.lib.leds.LedSignal;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {

  @SuppressWarnings("unused")
  private final LEDs m_leds;
  private final DriveTrain driveTrain = new DriveTrain();
  private final PowerDistribution m_PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();
  private SendableChooser<Command> autoChooser;
  private final CommandXBoxWrapper m_driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper m_operatorController = new CommandXBoxWrapper("Operator Controller",
      OperatorConstants.kOperatorControllerPort);

  private final Shooter shooter = new Shooter();
  final Arm m_arm = new Arm();
  private final Intake intake = new Intake();

  public RobotContainer() {
    PDHLogPowerFaults.setPdh(m_PowerDistribution, 8, 12, 13, 14, 15, 16, 17, 22, 23);
    LimelightPortForwarding.setup();
    if (Constants.isGraphsEnabled) {
      Shuffleboard.getTab("can")
          .addDouble("can utilization", () -> RobotController.getCANStatus().percentBusUtilization)
          .withWidget(BuiltInWidgets.kGraph);
    }

    configureBindings();

    m_leds = new LEDs(
        new int[] { 1, 2 },
        new LedSignal[] {
            LedSignal.isBrownedOut(),

            LedSignal.isDSConnected(),
            LedSignal.isEndGame(),
            LedSignal.hasgamepiceTopLedSignal(intake::getTopNoteSensor),
            LedSignal.hasgamepiceBottomLedSignal(intake::getBottomNoteSensor),
            LedSignal.intakeStalled(intake::isStalled),
            LedSignal.getLowBatteryLedSignal(),
            LedSignal.erolsPurpleLight(() -> m_operatorController.getHID().getPOV() == 90), // left dpad
            // LedSignal.seanscolors(() -> m_driverController.getHID().getPOV() != -1), //
            // all depad
            LedSignal.seanscolors(() -> m_operatorController.getHID().getPOV() == 270),
            LedSignal.shooterAtSetPoint(() -> shooter.isAtSetPoint()),
        });

    NamedCommands.registerCommand("armSpeakerPos", m_arm.autoSetPositionOnceCommand(55));
    NamedCommands.registerCommand("firstArmSpeakerPos", m_arm.autoSetPositionOnceCommand(55).withTimeout(2));
    NamedCommands.registerCommand("armRestFull", m_arm.autoSetPositionOnceCommand(0).withTimeout(2));
    NamedCommands.registerCommand("armRest", m_arm.autoSetPositionOnceCommand(15));
    NamedCommands.registerCommand("3.1", m_arm.autoSetPositionOnceCommand(29.5));
    NamedCommands.registerCommand("3.2", m_arm.autoSetPositionOnceCommand(26));
    NamedCommands.registerCommand("3.3", m_arm.autoSetPositionOnceCommand(34));
    NamedCommands.registerCommand("3.4", m_arm.autoSetPositionOnceCommand(40));
    NamedCommands.registerCommand("3.5", m_arm.autoSetPositionOnceCommand(100));
    NamedCommands.registerCommand("saboStart", m_arm.autoSetPositionOnceCommand(46));
    NamedCommands.registerCommand("armIntake",
        m_arm.autoSetPositionOnceCommand(20).andThen(intake.setIntakeUntilQueued()));
    NamedCommands.registerCommand("intake", intake.setIntakeUntilQueued());
    NamedCommands.registerCommand("intake+", intake.setIntakeUntilQueuedSlow());
    NamedCommands.registerCommand("intakeShot", intake.setDutyCycleCommand(.75).withTimeout(1.5));
    NamedCommands.registerCommand("shooter",
        Commands.parallel(
            Commands.sequence(
                Commands.waitSeconds(2),
                intake.setDutyCycleCommand(.5).withTimeout(1)),
            shooter.setDutyCycleCommand(1).withTimeout(2)));

    NamedCommands.registerCommand("conveyor", intake.setDutyCycleCommand(.5).withTimeout(2));
    NamedCommands.registerCommand("shooter+",
        shooter.setDutyCycleCommand(1).withTimeout(20));
    NamedCommands.registerCommand("shooter-",
        shooter.setyDutyCycleCommand().withTimeout(20));

    try {
      autoChooser = AutoBuilder.buildAutoChooser();
      SmartDashboard.putData("Auto Chooser", autoChooser);
    } catch (Exception exception) {
      autoChooser = new SendableChooser<>();
      SmartDashboard.putData("Auto Chooser", autoChooser);
      DriverStation.reportError(exception.toString(), exception.getStackTrace());
      new Fault("Failed to set up Auto Chooser.").setIsActive(true);
    }

  }

  boolean isArmHeld = false;

  private void configureBindings() {
    // driver controls

    var driveCommand = new DriveCommand(
        m_driverController::getLeftX,
        m_driverController::getLeftY,
        m_driverController::getRightX,
        () -> m_driverController.getHID().getLeftBumper(),
        () -> m_driverController.getHID().getPOV() == 270,
        () -> m_driverController.getHID().getPOV() == 90,
        () -> m_driverController.getHID().getPOV() == 0,
        () -> m_driverController.getHID().getPOV() == 180,
        () -> m_driverController.getHID().getPOV() == 45,
        () -> m_driverController.getHID().getPOV() == 135,
        () -> m_driverController.getHID().getPOV() == 315,
        () -> m_driverController.getHID().getPOV() == 225,
        driveTrain);
    driveTrain.setDefaultCommand(driveCommand);
    m_driverController.joysticksTrigger().onTrue(driveCommand);

    m_driverController.start().onTrue(driveTrain.zeroCommand());
    // duplacates on purpos
    m_driverController.back().onTrue(driveTrain.zeroCommand());

    m_driverController.x().whileTrue((driveTrain.xcommand()));

    // m_driverController.povRight().whileTrue(new Angles(m_arm));

    new Trigger(shooter::isAtSetPoint)
        .whileTrue(m_driverController.rumbleCommand(.75))
        .whileTrue(m_operatorController.rumbleCommand(.74));

    m_driverController.b().onTrue(m_arm.setPositionCommand(55));

    m_driverController.a().onTrue(m_arm.setPositionCommand(0));

    m_driverController.y()
        .whileTrue(m_arm.setPositionOnceCommand(100))
        .whileTrue(shooter.setyDutyCycleCommand())

        .onFalse(Commands.startEnd(() -> {
          shooter.setyDutyCycleCommand();
          intake.setSpeed(.75);
        }, () -> {
          shooter.stopShooter();
          intake.setSpeed(0);
        }, shooter, intake).withTimeout(1.5));

    m_driverController.rightTrigger()
        .whileTrue(Commands.run(shooter::setDefaultSpeed, shooter))
        .whileTrue(m_driverController.rumbleCommand(.1).withTimeout(4))
        .whileFalse(m_driverController.rumbleCommand(.8).withTimeout(.5))

        .onFalse(Commands.startEnd(() -> {
          if (m_driverController.getHID().getRightBumper()) {
            shooter.setDefaultSpeed();
            intake.setSpeed(0);
          } else {
            shooter.setDefaultSpeed();
            intake.setSpeed(.75);
          }
        }, () -> {
          shooter.stopShooter();
          intake.setSpeed(0);
        }, shooter, intake).withTimeout(1));

    m_driverController.leftTrigger().whileTrue(m_arm.setPositionOnceCommand(0).andThen(intake.intakeUntilQueued()));

    // Operator controls

    intake.setDefaultCommand(intake.dutyCycleCommand(() -> {
      return m_operatorController.getRightY();
    }));

    var armCommand = Commands.run(() -> {
      var joyStickPosition = m_operatorController.getLeftY() * 0.55;
      if (joyStickPosition > 0.01 || joyStickPosition < -0.01) {
        isArmHeld = false;
        m_arm.setDutyCycle(joyStickPosition);
      } else {
        if (isArmHeld == false) {
          m_arm.holdPosition();
        }
        isArmHeld = true;
      }
    }, m_arm);

    m_operatorController.leftYTrigger().onTrue(armCommand);
    m_arm.setDefaultCommand(armCommand);

    m_operatorController.leftTrigger().whileTrue(m_arm.setPositionOnceCommand(0).andThen(intake.intakeUntilQueued()));

    m_operatorController.leftBumper().whileTrue(intake.setDutyCycleCommand(-.75));

    m_operatorController.rightTrigger().whileTrue(shooter.setDutyCycleCommand(1));

    m_operatorController.rightBumper().whileTrue(intake.setDutyCycleCommand(.75));

    m_operatorController.start().onTrue(Commands.runOnce(m_arm::setZero, m_arm));
    // duplicates on purpos
    m_operatorController.back().onTrue(Commands.runOnce(m_arm::setZero, m_arm));

    m_operatorController.y().onTrue(m_arm.setPositionCommand(100));

    m_operatorController.a().onTrue(m_arm.setPositionCommand(120));

    m_operatorController.b().whileTrue(m_arm.setPositionCommand(0));

    m_operatorController.x().onTrue(m_arm.setPositionCommand(55));

    m_operatorController.leftStick().whileTrue(m_arm.overrideSoftLimits());

    // m_operatorController.povLeft().onTrue(Commands.runOnce(() -> {
    // LimelightHelpers.setPipelineIndex("limelight-right", 2);
    // }));

    m_operatorController.povUp().onTrue(m_arm.setPositionCommand(43.7));
    m_operatorController.povDown().whileTrue(shooter.setDutyCycleCommand(-1));

    // m_operatorController.povDown().whileTrue(shooter.setDutyCycleCommand(-1));

  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }

  public Command getTestCommand() {
    return new RobotSelfCheckCommand(
        Commands.sequence(
            new MusicToneCommand(Note.MiddleC, driveTrain).withTimeout(0.25),
            new MusicToneCommand(Note.HighC, driveTrain).withTimeout(0.25)),
        Commands.sequence(
            new MusicToneCommand(Note.MiddleC, driveTrain).withTimeout(0.25),
            new MusicToneCommand(Note.LowC, driveTrain).withTimeout(0.25)),
        driveTrain,
        intake);
  }
  // CanStream canStream = new CanStream();
}
