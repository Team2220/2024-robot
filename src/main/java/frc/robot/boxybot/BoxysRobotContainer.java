// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.boxybot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.Arduino.Arduino;
import frc.lib.controllers.CommandXBoxWrapper;

import static frc.lib.Arduino.ArduinoCommand.*;

import frc.robot.Robot24.Constants.OperatorConstants;
import frc.robot.Robot24.DriverTab;

public class BoxysRobotContainer {

  @SuppressWarnings("unused")
  private final BoxysDriveTrain driveTrain = new BoxysDriveTrain();

  private final CommandXBoxWrapper driverController = new CommandXBoxWrapper("Driver Controller",
      OperatorConstants.kDriverControllerPort);

  
  public static final DriverTab drivertab = new DriverTab();

  public static final Arduino arduino = new Arduino();

  public BoxysRobotContainer() {
  

    configureBindings();

  }

  

  private void configureBindings() {
    // driver controls

    driverController.a().onTrue(arduino.runCommand(BLUE));
    driverController.b().onTrue(arduino.runCommand(GREEN));

  }

  public Command getAutonomousCommand() {
    return null;
  }

  public Command getTestCommand() {
    return Commands.print("test");
  }
}
