// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.KrackenSwerve;

import frc.lib.drivetrain.DriveCommand;
import frc.lib.drivetrain.DriveTrain;
import frc.lib.drivetrain.ObjectTracker;
import frc.lib.faults.Fault;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.limeLight.LimelightPortForwarding;
import frc.lib.music.MusicToneCommand;
import frc.lib.music.Note;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.lib.xbox.CommandXBoxWrapper;
import frc.robot.KrackenSwerve.Constants.OperatorConstants;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {

  private final DriveTrain driveTrain = new DriveTrain();
  private final PowerDistribution PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();
  private SendableChooser<Command> autoChooser;
  private final CommandXBoxWrapper driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);

  public RobotContainer() {
    PDHLogPowerFaults.setPdh(PowerDistribution, 8, 12, 13, 14, 15, 16, 17, 22, 23);
    LimelightPortForwarding.setup();

    configureBindings();

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
        () -> driverController.getHID().getBButton(),
        driveTrain);
    driveTrain.setDefaultCommand(driveCommand);
    // driverController.joysticksTrigger().onTrue(driveCommand);

    driverController.start().onTrue(driveTrain.zeroCommand());
    // duplacates on purpos
    driverController.back().onTrue(driveTrain.zeroCommand());

    driverController.x().whileTrue((driveTrain.xcommand()));

    driverController.a().whileTrue(new ObjectTracker(driveTrain, driverController::getLeftX, driverController::getLeftY));
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
         );
  }
  // CanStream canStream = new CanStream();
}
