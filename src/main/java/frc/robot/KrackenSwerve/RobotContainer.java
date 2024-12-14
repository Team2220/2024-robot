// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.KrackenSwerve;

import frc.lib.RobotContainerInterface;
import frc.lib.controllers.CommandJoystickWrapper;
import frc.lib.controllers.CommandXBoxWrapper;
import frc.lib.drivetrain.DriveCommand;
import frc.lib.drivetrain.DriveTrain;
import frc.lib.drivetrain.ObjectTracker;
import frc.lib.faults.Fault;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.limeLight.LimelightPortForwarding;
import frc.lib.music.MusicToneCommand;
import frc.lib.music.Note;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.KrackenSwerve.Constants.OperatorConstants;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer implements RobotContainerInterface {

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
  private final CommandXBoxWrapper driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);
  
  private final CommandJoystickWrapper joystick = new CommandJoystickWrapper("Driver Joystick", 4, false);

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
        driverController,
        joystick,
        driveTrain);
    driveTrain.setDefaultCommand(driveCommand);
    // driverController.joysticksTrigger().onTrue(driveCommand);

    driverController.start().onTrue(driveTrain.zeroCommand());
    // duplacates on purpos
    driverController.back().onTrue(driveTrain.zeroCommand());
    joystick.button(2).onTrue(driveTrain.zeroCommand());

    // driverController.x().onTrue(new MusicToneCommand(Note.HigherC, driveTrain).withTimeout(0.25));
  
    driverController.x().onTrue(driveTrain.zeroTurningMotors());


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
