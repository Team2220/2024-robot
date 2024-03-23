// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.lib.CommandXBoxWrapper;
import frc.lib.LimelightPortForwarding;
import frc.lib.Note;
import frc.lib.faults.Fault;
import frc.lib.faults.PDHLogPowerFaults;
import frc.lib.leds.LEDs;
import frc.lib.leds.LedSignal;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.Constants.OperatorConstants;


import com.fasterxml.jackson.annotation.JacksonInject.Value;
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
  private final CommandXBoxWrapper m_driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);
  private final CommandXBoxWrapper m_operatorController = new CommandXBoxWrapper("Operator Controller",
      OperatorConstants.kOperatorControllerPort);
  public RobotContainer() {
    PDHLogPowerFaults.setPdh(m_PowerDistribution, 
    8, 10, 12, 13, 14, 15, 16, 17, 22, 23);

    configureBindings();

    m_leds = new LEDs(
        new int[] { 1, 2 },
        new LedSignal[] {
            LedSignal.isBrownedOut(),
            LedSignal.isDSConnected(),
        });

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
    // duplacates on purpos
    m_driverController.back().onTrue(driveTrain.zeroCommand());
  }
}
