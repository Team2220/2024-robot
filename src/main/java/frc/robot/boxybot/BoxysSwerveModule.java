// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.boxybot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.GenericEntry;
import frc.lib.ShuffleBoardTabWrapper;

public class BoxysSwerveModule implements ShuffleBoardTabWrapper {
  GenericEntry speed;
  GenericEntry angle;

  private String name;

  public BoxysSwerveModule(
      String name) {
    this.name = name;

  }

  public SwerveModuleState getState() {
    return new SwerveModuleState(
        getDriveVelocity(), getAngle());
  }

  private double getDriveVelocity() {
    return desiredState.speedMetersPerSecond;
  }

  private double getDrivePosition() {
    return 0;
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        getDrivePosition(), getAngle());
  }

  private Rotation2d getAngle() {
    return desiredState.angle;
  }

  SwerveModuleState desiredState = new SwerveModuleState();

  public void setDesiredState(SwerveModuleState desiredState) {
    this.desiredState = desiredState;

  }

  @Override
  public String getName() {
    return name;
  }
}
