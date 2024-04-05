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
import frc.robot.commands.AutoIntake;
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
  private final LEDs leds;
  private final DriveTrain driveTrain = new DriveTrain();
  private final PowerDistribution PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();
  private SendableChooser<Command> autoChooser;
  private final CommandXBoxWrapper driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper operatorController = new CommandXBoxWrapper("Operator Controller",
      OperatorConstants.kOperatorControllerPort);

  private final Shooter shooter = new Shooter();
  final Arm arm = new Arm();
  private final Intake intake = new Intake();

  public RobotContainer() {
    PDHLogPowerFaults.setPdh(PowerDistribution, 8, 12, 13, 14, 15, 16, 17, 22, 23);
    LimelightPortForwarding.setup();
    if (Constants.isGraphsEnabled) {
      Shuffleboard.getTab("can")
          .addDouble("can utilization", () -> RobotController.getCANStatus().percentBusUtilization)
          .withWidget(BuiltInWidgets.kGraph);
    }

    configureBindings();

    leds = new LEDs(
        new int[] { 1, 2 },
        new LedSignal[] {
            LedSignal.isBrownedOut(),
            LedSignal.isDSConnected(),
            LedSignal.isEndGame(),
            LedSignal.hasgamepiceTopLedSignal(intake::getTopNoteSensor),
            LedSignal.intakeStalled(intake::isStalled),
            LedSignal.hasgamepiceBottomLedSignal(intake::getBottomNoteSensor),
            LedSignal.coastButton(arm::getcoastButton),
            LedSignal.getLowBatteryLedSignal(),
            LedSignal.erolsPurpleLight(() -> operatorController.getHID().getPOV() == 90), // left dpad
            // LedSignal.seanscolors(() -> driverController.getHID().getPOV() != -1), //
            // all depad
            // LedSignal.shooterAtSetPoint(() -> shooter.isAtSetPoint()),
        });

    NamedCommands.registerCommand("armSpeakerPos", arm.autoSetPositionOnceCommand(55));
    NamedCommands.registerCommand("firstArmSpeakerPos", arm.autoSetPositionOnceCommand(55).withTimeout(2));
    NamedCommands.registerCommand("armRestFull", arm.autoSetPositionOnceCommand(0).withTimeout(2));
    NamedCommands.registerCommand("armRest", arm.autoSetPositionOnceCommand(15));
    NamedCommands.registerCommand("3.1", arm.autoSetPositionOnceCommand(29.5));
    NamedCommands.registerCommand("3.2", arm.autoSetPositionOnceCommand(26));
    NamedCommands.registerCommand("3.3", arm.autoSetPositionOnceCommand(34));
    NamedCommands.registerCommand("3.4", arm.autoSetPositionOnceCommand(40));
    NamedCommands.registerCommand("3.5", arm.autoSetPositionOnceCommand(100));
    NamedCommands.registerCommand("saboStart", arm.autoSetPositionOnceCommand(46));
    NamedCommands.registerCommand("armIntake",
        arm.autoSetPositionOnceCommand(20).andThen(intake.setIntakeUntilQueued()));
    NamedCommands.registerCommand("intake", intake.setIntakeUntilQueued());
    NamedCommands.registerCommand("intake+", intake.setIntakeUntilQueuedSlow());
    NamedCommands.registerCommand("intakeShot", intake.setDutyCycleCommand(.75).withTimeout(.5));
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
    NamedCommands.registerCommand("autoIntake", new AutoIntake(intake));

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
        driverController::getLeftX,
        driverController::getLeftY,
        driverController::getRightX,
        () -> driverController.getHID().getLeftBumper(),
        () -> driverController.getHID().getPOV() == 270,
        () -> driverController.getHID().getPOV() == 90,
        () -> driverController.getHID().getPOV() == 0,
        () -> driverController.getHID().getPOV() == 180,
        () -> driverController.getHID().getPOV() == 45,
        () -> driverController.getHID().getPOV() == 135,
        () -> driverController.getHID().getPOV() == 315,
        () -> driverController.getHID().getPOV() == 225,
        driveTrain);
    driveTrain.setDefaultCommand(driveCommand);
    driverController.joysticksTrigger().onTrue(driveCommand);

    driverController.start().onTrue(driveTrain.zeroCommand());
    // duplacates on purpos
    driverController.back().onTrue(driveTrain.zeroCommand());

    driverController.x().whileTrue((driveTrain.xcommand()));

    // driverController.povRight().whileTrue(new Angles(m_arm));

    new Trigger(shooter::isGoingWrongWay)
        .whileTrue(driverController.rumbleCommand(.50))
        .whileTrue(operatorController.rumbleCommand(.80));

    driverController.b().onTrue(arm.setPositionCommand(55));

    driverController.a().onTrue(arm.setPositionCommand(0));

    driverController.y()
        .whileTrue(arm.setPositionOnceCommand(100))
        .whileTrue(shooter.setyDutyCycleCommand())

        .onFalse(Commands.startEnd(() -> {
          shooter.setyDutyCycleCommand();
          intake.setSpeed(.75);
        }, () -> {
          shooter.stopShooter();
          intake.setSpeed(0);
        }, shooter, intake).withTimeout(1.5));

    driverController.rightTrigger()
        .whileTrue(shooter.setDutyCycleCommand(1))
        .whileTrue(driverController.rumbleCommand(.1).withTimeout(4))
        .whileFalse(driverController.rumbleCommand(.8).withTimeout(.5))

        .onFalse(Commands.startEnd(() -> {
          if (driverController.getHID().getRightBumper()) {
            shooter.setDutyCycle(1);
            intake.setSpeed(0);
          } else {
            shooter.setDutyCycle(1);
            intake.setSpeed(.75);
          }
        }, () -> {
          shooter.stopShooter();
          intake.setSpeed(0);
        }, shooter, intake).withTimeout(1));

    driverController.leftTrigger().whileTrue(arm.setPositionOnceCommand(0).andThen(intake.intakeUntilQueued()));

    // Operator controls
    intake.setDefaultCommand(intake.dutyCycleCommand(() -> {
      return operatorController.getRightY();
    }));

    var armCommand = Commands.run(() -> {
      var joyStickPosition = operatorController.getLeftY() * 0.55;
      if (joyStickPosition > 0.01 || joyStickPosition < -0.01) {
        isArmHeld = false;
        arm.setDutyCycle(joyStickPosition);
      } else {
        if (isArmHeld == false) {
          arm.holdPosition();
        }
        isArmHeld = true;
      }
    }, arm);

    operatorController.leftYTrigger().onTrue(armCommand);
    arm.setDefaultCommand(armCommand);

    operatorController.leftTrigger().whileTrue(arm.setPositionOnceCommand(0).andThen(intake.intakeUntilQueued()));

    operatorController.leftBumper().whileTrue(intake.setDutyCycleCommand(-.75));

    operatorController.rightTrigger().whileTrue(shooter.setDutyCycleCommand(1));

    operatorController.rightBumper().whileTrue(intake.setDutyCycleCommand(.75));

    operatorController.start().onTrue(Commands.runOnce(arm::setZero, arm));
    // duplicates on purpos
    operatorController.back().onTrue(Commands.runOnce(arm::setZero, arm));

    operatorController.y().onTrue(arm.setPositionCommand(100));

    operatorController.a().onTrue(arm.setPositionCommand(120));

    operatorController.b().whileTrue(arm.setPositionCommand(0));

    operatorController.x().onTrue(arm.setPositionCommand(55));

    operatorController.leftStick().whileTrue(arm.overrideSoftLimits());

    // operatorController.povLeft().onTrue(Commands.runOnce(() -> {
    // LimelightHelpers.setPipelineIndex("limelight-right", 2);
    // }));

    operatorController.povUp().onTrue(arm.setPositionCommand(43.7));
    operatorController.povDown().whileTrue(shooter.setDutyCycleCommand(-1));

    //_operatorController.povDown().whileTrue(shooter.setDutyCycleCommand(-1));

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
        shooter,
        intake,
        arm);
  }
  // CanStream canStream = new CanStream();
}
