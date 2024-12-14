// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Robot24;

import frc.lib.DriverStationTriggers;
import frc.lib.RobotContainerInterface;
import frc.lib.Arduino.Arduino;
import frc.lib.can.CanStream;
import frc.lib.controllers.CommandXBoxWrapper;
import frc.lib.controllers.LazyCommandXboxWrapper;
import frc.lib.devices.DigitalInputWrapper;
import frc.lib.drivetrain.DriveCommand;
import frc.lib.drivetrain.DriveTrain;
import frc.lib.drivetrain.ObjectTracker;
import frc.lib.faults.Fault;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.leds.CANdleWrapper;
import frc.lib.leds.LEDs;
import frc.lib.leds.LedSignal;
import frc.lib.limeLight.LimelightPortForwarding;
import frc.lib.music.MusicToneCommand;
import frc.lib.music.Note;
import frc.lib.music.TalonOrchestra;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.Robot24.Constants.OperatorConstants;
import frc.robot.Robot24.commands.AutoArmAngles;
import frc.robot.Robot24.commands.AutoArmAngles;
import frc.robot.Robot24.commands.AutoIntake;
import frc.robot.Robot24.subsystems.Arm;
import frc.robot.Robot24.subsystems.Intake;
import frc.robot.Robot24.subsystems.Shooter;

import com.ctre.phoenix.led.CANdle;
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
import static frc.lib.Arduino.ArduinoCommand.*;

public class RobotContainer implements RobotContainerInterface{

  
    // driverController.a().onTrue(arduino.runCommand(BLUE));
    // driverController.b().onTrue(arduino.runCommand(GREEN));

  @SuppressWarnings("unused")
  private final LEDs leds;
  private final DriveTrain driveTrain = new DriveTrain(
    Constants.DriveTrain.frontLeftOffset,
    Constants.DriveTrain.frontrightoffset, 
    Constants.DriveTrain.backleftoffset, 
    Constants.DriveTrain.backrightoffset
  );
  private final PowerDistribution PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public final DriverTab drivertab = new DriverTab();
  private SendableChooser<Command> autoChooser;
  private final CommandXBoxWrapper driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper operatorController = new CommandXBoxWrapper("Operator Controller",
      OperatorConstants.kOperatorControllerPort);
  private final CommandXBoxWrapper testController = new CommandXBoxWrapper("Test Controller",
      OperatorConstants.kTestControllerPort);

  public final Arduino arduino = new Arduino();

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
        new CANdleWrapper[] {
            new CANdleWrapper(1, 512),
            new CANdleWrapper(2, 512) },
        new LedSignal[] {
            LedSignal.isBrownedOut(),
            LedSignal.isDSConnected(),
            LedSignal.isEndGame(),
            // LedSignal.probalynotgoingtowork(driverController.x()),
           // LedSignal.customrainbow(driverController.x()),
            LedSignal.hasgamepiceTopLedSignal(intake::getTopNoteSensor),
            LedSignal.intakeStalled(intake::isStalled),
            LedSignal.hasgamepiceBottomLedSignal(intake::getBottomNoteSensor),                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
            LedSignal.coastButton(coastButton::get),
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

   private static DigitalInputWrapper coastButton = new DigitalInputWrapper(Constants.Arm.coastButtonID,
            "coastButton", true);

  boolean isArmHeld = false;

  private void configureBindings() {
coastButton.asTrigger().and(DriverStationTriggers.isDisabled()).onTrue(arm.toggleCoast());

    // driver controls

    var driveCommand = new DriveCommand(
        driverController,
        driveTrain);
    driveTrain.setDefaultCommand(driveCommand);
    // driverController.joysticksTrigger().onTrue(driveCommand);

    driverController.start().onTrue(driveTrain.zeroCommand());
    // duplacates on purpos
    driverController.back().onTrue(driveTrain.zeroCommand());

    //driverController.x().whileTrue((driveTrain.xcommand()));
    driverController.x().onTrue(driveTrain.zeroTurningMotors());

    // driverController.povRight().whileTrue(new Angles(arm));

    new Trigger(shooter::isGoingWrongWay)
        .whileTrue(driverController.rumbleCommand(.50))
        .whileTrue(operatorController.rumbleCommand(.80));

    driverController.b().onTrue(arm.setPositionCommand(55));

    driverController.a().whileTrue(new AutoArmAngles(arm, driveTrain, driverController::getLeftX, driverController:: getLeftY, driverController:: getRightX ));
    // driverController.a().whileTrue(new ObjectTracker(driveTrain, driverController::getLeftX, driverController::getLeftY));

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
        .whileTrue(shooter.setDutyCycleCommand(0.25))
        .whileTrue(driverController.rumbleCommand(.1).withTimeout(4))
        .whileFalse(driverController.rumbleCommand(.8).withTimeout(.5))

        .onFalse(Commands.startEnd(() -> {
          if (driverController.getHID().getRightBumper()) {
            shooter.setDutyCycle(0.25);
            intake.setSpeed(0);
          } else {
            shooter.setDutyCycle(0.25);
            intake.setSpeed(.5);
          }
        }, () -> {
          shooter.stopShooter();
          intake.setSpeed(0);
        }, shooter, intake).withTimeout(1));

    driverController.leftTrigger().whileTrue(arm.setPositionOnceCommand(0).andThen(intake.intakeUntilQueued()));


    driverController.rightBumper().whileTrue(shooter.setDutyCycleCommand(-0.75));
    driverController.leftBumper().whileTrue(intake.setDutyCycleCommand(-0.5));





    // Operator controls #######################################################################################################
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

    // _operatorController.povDown().whileTrue(shooter.setDutyCycleCommand(-1));



// ###############test commands####################

testController.a().onTrue(arduino.runCommand(GREEN));
testController.b().onTrue(arduino.runCommand(POLICE).alongWith(new TalonOrchestra("PoliceSirin.chrp", driveTrain)));
testController.x().onTrue(arduino.runCommand(BLUE));
testController.y().onTrue(arduino.runCommand(YELLOW));





















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
        driveTrain
        // shooter,
        // intake,
        //  arm
         );
  }
  // CanStream canStream = new CanStream();
}
