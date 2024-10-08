package frc.robot.Robot24.commands;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.limeLight.LimelightHelpers;
import frc.robot.Robot24.subsystems.Arm;

public class Angles extends Command {
    Arm arm;
    private AprilTagFieldLayout layout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    Pose3d RED = layout.getTagPose(4).get();
    Pose3d BLUE = layout.getTagPose(7).get();

    public Angles(Arm arm) {
        this.arm = arm;

    }

    @Override
    public void execute() {
        var goal = DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? RED : BLUE;
        LimelightHelpers.PoseEstimate limelightMeasurement = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight");
        if (limelightMeasurement.tagCount >= 2) {
            double Botpose = limelightMeasurement.pose.getTranslation()
                    .getDistance(((Pose3d) goal).toPose2d().getTranslation());
            double speakerhight = 6.6;
            double armAngle = Math.toDegrees(Math.atan2(speakerhight, Botpose));
            System.out.println(armAngle);
            arm.setPosition(armAngle);
        }
    
    }

    // @Override
    // public void initialize() {
    // botPose = LimelightHelpers.getBotPose2d("limelight");

    // }
}
