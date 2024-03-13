package frc.robot.commands;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.units.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Arm;
import frc.lib.LimelightHelpers;

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
        Pose2d pose = LimelightHelpers.getBotPose2d("limelight-right");
        double Botpose = pose.getTranslation().getDistance(((Pose3d) goal).toPose2d().getTranslation());
        double speakerhight = 6.6;
        double armAngle = Math.atan2(speakerhight, Botpose);
        System.out.println(armAngle);
    }

    // @Override
    // public void initialize() {
    //     botPose = LimelightHelpers.getBotPose2d("limelight");

    // }
}
    