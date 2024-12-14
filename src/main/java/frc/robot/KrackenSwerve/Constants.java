// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.KrackenSwerve;

import static edu.wpi.first.units.Units.Degree;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import frc.lib.RobotInstance;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;
  }

  public static class DriveTrain {
    private static Measure<Angle> ofset = Degree.of(180);
    public static final Measure<Angle> frontLeftOffset = Degree.of(259.94).plus(ofset);

    public static final Measure<Angle>  frontrightoffset = Degree.of(121.41).plus(ofset);

    public static final Measure<Angle> backleftoffset = Degree.of(154.00).plus(ofset);

    public static final Measure<Angle> backrightoffset = Degree.of(184.74).plus(ofset);
  }


  public static final class LimelightConfig {
    public static final String LEFT_NAME = "limelight-left";
    public static final String RIGHT_NAME = "limelight-right";
    public static final String LEFT_URL = "http://10.22.20.10:5800/stream.mjpg";
    public static final String RIGHT_URL = "http://10.22.20.22:5801/stream.mjpg";
  }
}
