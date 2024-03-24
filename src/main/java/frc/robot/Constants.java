// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

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

  public static class LEDS {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
  }

  public static final boolean isGraphsEnabled = false;

  public static class DriveTrain {
    public static final double DT_BL_SE_OFFSET = RobotInstance.config((robot) -> {
      return switch (robot) {
        case Robot23 -> 8.96484375 - 90;
        case Robot24 -> 356.667 - 90;
      };
    });

    public static final double DT_FR_SE_OFFSET = RobotInstance.config((robot) -> {
      return switch (robot) {
        case Robot23 -> 124.98046875 - 90;
        case Robot24 -> 153.808 - 90;
      };
    });

    public static final double DT_FL_SE_OFFSET = RobotInstance.config((robot) -> {
      return switch (robot) {
        case Robot23 -> 155.302734375 - 90;
        case Robot24 -> 222.099 - 270;
      };
    });

    public static final double DT_BR_SE_OFFSET = RobotInstance.config((robot) -> {
      return switch (robot) {
        case Robot23 -> 247.5 - 90;
        case Robot24 -> 182.284 - 270;
      };
    });
  }

  public static class Shooter {
    public static final int id_left = 23;
    public static final int id_right = 24;
    public static final double gear_ratio = 30.0 / 18.0;
  }

  public static class Intake {
    public static final int id_intake = 31;
    public static final int id_conv = 5;
    public static final int noteSensorId = 0;
    public static final int bottomNoteSensorID = 1;
  }

  public static class Arm {
    public static final int ARM_TALON_LEFT = 27;
    public static final int ARM_TALON_RIGHT = 28;
    public static final double ARM_GEAR_RATIO = (5.0 / 1.0) * (4.0 / 1.0) * (4.0 / 1.0) * (58.0 / 18.0);
  }

  public static final class LimelightConfig {
    public static final String LEFT_NAME = "limelight-left";
    public static final String RIGHT_NAME = "limelight-right";
    public static final String LEFT_URL = "http://10.22.20.10:5800/stream.mjpg";
    public static final String RIGHT_URL = "http://10.22.20.22:5801/stream.mjpg";
  }
}
