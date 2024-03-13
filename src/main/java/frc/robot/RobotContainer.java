// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.lib.CommandXBoxWrapper;
import frc.lib.LimelightHelpers;
import frc.lib.LimelightPortForwarding;
import frc.lib.MusicToneCommand;
import frc.lib.Note;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.leds.LEDs;
import frc.lib.leds.LedSignal;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

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
  private final CommandXBoxWrapper m_driverController = new CommandXBoxWrapper(OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper m_operatorController = new CommandXBoxWrapper(
      OperatorConstants.kOperatorControllerPort);

  private final Shooter shooter = new Shooter();
  private final Arm m_arm = new Arm();
  private final Intake intake = new Intake();

  public RobotContainer() {
    PDHLogPowerFaults.setPdh(m_PowerDistribution, 8, 12, 13, 14, 15, 16, 17, 22, 23);
    LimelightPortForwarding.setup();
    Shuffleboard.getTab("can").addDouble("can utilization", () -> RobotController.getCANStatus().percentBusUtilization)
        .withWidget(BuiltInWidgets.kGraph);

    configureBindings();

    m_leds = new LEDs(
        new int[] {1, 2},
        new LedSignal[] {
            LedSignal.isBrownedOut(),
            LedSignal.isDSConnected(),
            LedSignal.isEndGame(),
            // LedSignal.hasgamepiceBottomLedSignal(intake::getBottomNoteSensor),
            // LedSignal.hasgamepiceTopLedSignal(intake::getTopNoteSensor),
            LedSignal.getLowBatteryLedSignal(),
            LedSignal.shooterAtSetPoint(() -> shooter.isAtSetPoint())
        });

    NamedCommands.registerCommand("armSpeakerPos", m_arm.setPositionOnceCommand(55));
    NamedCommands.registerCommand("armRest", m_arm.setPositionOnceCommand(0));
    NamedCommands.registerCommand("3.1", m_arm.setPositionOnceCommand(32));
    NamedCommands.registerCommand("3.2", m_arm.setPositionOnceCommand(34));
    NamedCommands.registerCommand("3.2", m_arm.setPositionOnceCommand(28));
    NamedCommands.registerCommand("saboStart", m_arm.setPositionOnceCommand(46));

    NamedCommands.registerCommand("intake", intake.setIntakeUntilQueued());
    NamedCommands.registerCommand("intake+", intake.setDutyCycleCommand(.75).withTimeout(15));
    NamedCommands.registerCommand("intakeShot", intake.setDutyCycleCommand(.5).withTimeout(1));
    NamedCommands.registerCommand("shooter",
        Commands.parallel(
            Commands.sequence(
                Commands.waitSeconds(2),
                intake.setDutyCycleCommand(.5).withTimeout(1)),
            shooter.setDutyCycleCommand(1).withTimeout(2)));

    NamedCommands.registerCommand("conveyor", intake.setDutyCycleCommand(.5).withTimeout(2));
    NamedCommands.registerCommand("shooter+", Commands.run(() -> {
      shooter.setDefaultSpeed();
    }, shooter).withTimeout(15));

    try {
      autoChooser = AutoBuilder.buildAutoChooser();
      SmartDashboard.putData("Auto Chooser", autoChooser);

    } catch (Exception exception) {
      autoChooser = new SendableChooser<>();
      DriverStation.reportError(exception.toString(), exception.getStackTrace());

    }

  }
  boolean isArmHeld = false;

  private void configureBindings() {
    // driver controls
    var driveCommand = driveTrain.driveCommand(() -> {
      double coefficient = m_driverController.getHID().getLeftBumper() ? 0.5 : 1;
      return m_driverController.getLeftX() * -1 * coefficient;
    }, () -> {
      double coefficient = m_driverController.getHID().getLeftBumper() ? 0.5 : 1;
      return m_driverController.getLeftY() * coefficient;
    }, () -> {
      double coefficient = m_driverController.getHID().getLeftBumper() ? 0.5 : 1;
      return m_driverController.getRightX() * -1 * coefficient;
    });
    driveTrain.setDefaultCommand(driveCommand);
    m_driverController.joysticksTrigger().onTrue(driveCommand);

    m_driverController.start().onTrue(driveTrain.zeroCommand());

    m_driverController.x().whileTrue((driveTrain.xcommand()));

    new Trigger(shooter::isAtSetPoint)
        .whileTrue(m_driverController.rumbleCommand(.75))
        .whileTrue(m_operatorController.rumbleCommand(.74));

    m_driverController.b().onTrue(m_arm.setPositionCommand(55));

    m_driverController.a().onTrue(m_arm.setPositionCommand(0));

    m_driverController.rightTrigger()
        .whileTrue(Commands.run(shooter::setDefaultSpeed, shooter))
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

    m_driverController.leftTrigger().whileTrue(intake.intakeUntilQueued());

    m_driverController.back().whileTrue(new MusicToneCommand(256, driveTrain));

    intake.setDefaultCommand(intake.dutyCycleCommand(() -> {
      return m_operatorController.getRightY() * .75;
    }));

    var armCommand = Commands.run(() -> {
      var joyStickPosition = m_operatorController.getLeftY() * 0.55;
      if (joyStickPosition > 0.01 || joyStickPosition < -0.01) {
        isArmHeld = false;
        m_arm.setDutyCycle(joyStickPosition);
      } else {
        if(isArmHeld == false){
          m_arm.holdPosition();
        }
        isArmHeld = true;
      }
    }, m_arm);

    m_operatorController.leftYTrigger().onTrue(armCommand);
    m_arm.setDefaultCommand(armCommand);

    m_operatorController.leftTrigger().whileTrue(intake.intakeUntilQueued());

    m_operatorController.leftBumper().whileTrue(intake.setDutyCycleCommand(-.75));

    m_operatorController.rightTrigger().whileTrue(shooter.setDutyCycleCommand(-1));

    m_operatorController.rightBumper().whileTrue(intake.setDutyCycleCommand(.75));

    m_operatorController.start().onTrue(Commands.runOnce(m_arm::setZero, m_arm));

    m_operatorController.y().onTrue(m_arm.setPositionCommand(100));

    m_operatorController.a().onTrue(m_arm.setPositionCommand(0));

    m_operatorController.b().onTrue(m_arm.setPositionCommand(32));

    m_operatorController.x().onTrue(m_arm.setPositionCommand(55));

    m_operatorController.leftStick().whileTrue(m_arm.overrideSoftLimits());

    m_operatorController.povUp().onTrue(Commands.runOnce(() -> {
      LimelightHelpers.setPipelineIndex("limelight-right", 2);
    }));

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
}
