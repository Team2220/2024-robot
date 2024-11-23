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

import java.util.zip.Deflater;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest.SwerveDriveBrake;
import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {

  private final DriveTrain driveTrain = new DriveTrain(
    Constants.DriveTrain.frontLeftOffset,
    Constants.DriveTrain.frontrightoffset, 
    Constants.DriveTrain.backleftoffset, 
    Constants.DriveTrain.backrightoffset
  );
  private final PowerDistribution PowerDistribution = new PowerDistribution();
  @SuppressWarnings("unused")
  public static final DriverTab drivertab = new DriverTab();
  private SendableChooser<Command> autoChooser;
  Joystick mainJoystick = new Joystick(4); // Flight joystick on port 4
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

        // Xbox controller bindings
    var driveCommand = new DriveCommand(
        mainJoystick::getX,
        mainJoystick::getY,
        mainJoystick::getZ,
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

    mainJoystick.getX();

    // // Xbox controller bindings
    // var driveCommand = new DriveCommand(
    //     driverController::getLeftX,
    //     driverController::getLeftY,
    //     driverController::getRightX,
    //     () -> driverController.getHID().getLeftBumper(),
    //     () -> driverController.getHID().getPOV() == 270,
    //     () -> driverController.getHID().getPOV() == 90,
    //     () -> driverController.getHID().getPOV() == 0,
    //     () -> driverController.getHID().getPOV() == 180,
    //     () -> driverController.getHID().getPOV() == 45,
    //     () -> driverController.getHID().getPOV() == 135,
    //     () -> driverController.getHID().getPOV() == 315,
    //     () -> driverController.getHID().getPOV() == 225,
    //     () -> driverController.getHID().getBButton(),
    //     driveTrain);
    // driveTrain.setDefaultCommand(driveCommand);


    driverController.start().onTrue(driveTrain.zeroCommand());
    driverController.back().onTrue(driveTrain.zeroCommand());
    driverController.x().onTrue(driveTrain.zeroTurningMotors());

    // Flight joystick bindings
    new Trigger(() -> mainJoystick.getRawButton(1)) // Trigger button
        .onTrue(driveTrain.zeroCommand());

    new Trigger(() -> mainJoystick.getRawButton(2)) // Button 2
        .onTrue(driveTrain.zeroCommand());

    new Trigger(() -> mainJoystick.getRawButton(3)) // Example for button 3
        .onTrue(driveTrain.zeroCommand());
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
}
