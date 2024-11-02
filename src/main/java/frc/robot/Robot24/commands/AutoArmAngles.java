package frc.robot.Robot24.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.drivetrain.DriveTrain;
import frc.lib.limeLight.LimelightHelpers;
import frc.lib.tunables.TunableDouble;
import frc.robot.Robot24.subsystems.Arm;

public class AutoArmAngles extends Command {
    Arm arm;
    DriveTrain driveTrain;
    DoubleSupplier xSupplier;
    DoubleSupplier ySupplier;
    DoubleSupplier rotSupplier;
    private AprilTagFieldLayout layout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    Pose3d RED = layout.getTagPose(4).get();
    Pose3d BLUE = layout.getTagPose(7).get();
    private PIDController turningPid = new PIDController(0, 0, 0);
    private static TunableDouble p = new TunableDouble("P", .0007, true, "limelight");
    private static TunableDouble i = new TunableDouble("I", 0, true, "limelight");
    private static TunableDouble d = new TunableDouble("D", 0.00001, true, "limelight");
    InterpolatingDoubleTreeMap map = new InterpolatingDoubleTreeMap();

    public AutoArmAngles(Arm arm, DriveTrain driveTrain, DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier rotSupplier) {
        addRequirements(driveTrain, arm);
        this.arm = arm;
        this.driveTrain = driveTrain;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.rotSupplier = rotSupplier;
        map.put(0.0, 63.0);
        map.put(1.0, 40.0);
        map.put(3.0, 20.0);
        map.put(5.0, 10.0);
        map.put(7.0, 0.0);
        turningPid.setTolerance(5);
        // turningPid.setSetpoint(0);
    }

    @Override
    public void execute() {
        turningPid.setPID(p.getValue(), i.getValue(), d.getValue());
        double out = 0;

        var goal = DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? RED : BLUE;
        driveTrain.getPose();
        double DistanceToSpeaker = driveTrain.getPose().getTranslation().getDistance(goal.toPose2d().getTranslation());
        double Rotation = driveTrain.getPose().getRotation().getDegrees();
        out = turningPid.calculate(
                Rotation,
                Math.toDegrees(Math.atan2(
                        driveTrain.getPose().getY() - goal.getY(),
                        driveTrain.getPose().getX() - goal.getX()

                )));

        driveTrain.drive(xSupplier.getAsDouble(), ySupplier.getAsDouble(), rotSupplier.getAsDouble(), true);
        double armAngle = map.get(DistanceToSpeaker);
        // double speakerhight = 6.6;
        // double armAngle = Math.toDegrees(Math.atan2(speakerhight, DistanceToSpeaker))
        // * .83;
        System.out.println("Arm angle: " + armAngle);
        System.out.println("Distance to speaker: " + DistanceToSpeaker);
        arm.setPosition(armAngle);

    }

    @Override
    public void initialize() {
        // botPose = LimelightHelpers.getBotPose2d("limelight-right");

    }
}