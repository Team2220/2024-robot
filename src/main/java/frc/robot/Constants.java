// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

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
    public static final int kCodriverControllerPort = 1;
  }

  public static class LEDS {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
  }

  public static class Shooter {
    public static int id_left = 10;
    public static int id_right = 11;
  }

  public static class Intake {
    public static int id_intake = 8;
    public static int id_conv = 9;
  }

  public static final class LimelightConfig {
    public static final String LEFT_NAME = "limelight-left";
    public static final String RIGHT_NAME = "limelight-right";
    public static final String LEFT_URL = "http://10.22.20.10:5800/stream.mjpg";
    public static final String RIGHT_URL = "http://10.22.20.11:5800/stream.mjpg";
  }
}
