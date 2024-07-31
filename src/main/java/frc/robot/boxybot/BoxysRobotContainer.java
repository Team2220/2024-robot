// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.boxybot;

import frc.lib.MusicToneCommand;
import frc.lib.Note;
import frc.lib.selfCheck.RobotSelfCheckCommand;
import frc.robot.DriveTrain;
import frc.robot.DriverTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class BoxysRobotContainer {
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


  // CanStream canStream = new CanStream();
}
