// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.boxybot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot24.DriverTab;
import frc.lib.RobotContainerType;

public class BoxysRobotContainer implements RobotContainerType {
  @SuppressWarnings("unused")
  private final BoxysDriveTrain driveTrain = new BoxysDriveTrain();

  
  public static final DriverTab drivertab = new DriverTab();

  public BoxysRobotContainer() {
  

    configureBindings();

  }

  private void configureBindings() {
    // driver controls

  }

  public Command getAutonomousCommand() {
    return null;
  }

  @Override
  public Command getTestCommand() {
    return null;
  }


  // CanStream canStream = new CanStream();
}
