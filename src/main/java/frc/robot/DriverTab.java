package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Constants.LimelightConfig;

public class DriverTab {
  ShuffleboardTab tab = Shuffleboard.getTab("driver");

  public DriverTab() {
    HttpCamera leftCamera = new HttpCamera(LimelightConfig.LEFT_NAME, LimelightConfig.LEFT_URL);
    CameraServer.getVideo(leftCamera);
    tab.add(leftCamera).withPosition(0, 0).withSize(5, 5);

    HttpCamera rightCamera = new HttpCamera(LimelightConfig.RIGHT_NAME, LimelightConfig.RIGHT_URL);
    CameraServer.getVideo(rightCamera);
    tab.add(rightCamera).withPosition(5, 0).withSize(5, 5);
  }
}
