package frc.robot.boxybot;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import static edu.wpi.first.math.util.Units.inchesToMeters;

import com.ctre.phoenix6.hardware.TalonFX;
import com.pathplanner.lib.util.PIDConstants;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.NavXWrapper;
import frc.lib.limeLight.LimelightHelpers;
import frc.lib.music.TalonFXSubsystem;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.robot.Robot24.Constants;

/**
 * Standard deviations of the vision measurements. Increase these numbers to
 * trust global measurements from vision
 * less. This matrix is in the form [x, y, theta]ᵀ, with units in meters and
 * radians.
 */

public class BoxysDriveTrain extends SubsystemBase
        implements TalonFXSubsystem, CheckableSubsystem, ShuffleBoardTabWrapper {

    
    public BoxysDriveTrain() {
        Shuffleboard.getTab("field").add("Field", poseEstimatorField).withSize(4, 3).withPosition(0, 0);
        Shuffleboard.getTab("limeLight").add("limeLight", limeLightField)
                .withSize(4, 4)
                .withPosition(0, 0);

        addGraph("GyroRate", navx::getRate);
    }

    GenericEntry gyroAngle = Shuffleboard.getTab("swerve").add("gyroAngle", 0).getEntry();

    @Override
    public TalonFX[] getTalonFXs() {
        return new TalonFX[] {

        };
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {

        };
    }

    private final Field2d poseEstimatorField = new Field2d();
    private final Field2d limeLightField = new Field2d();
    private final Pose2d startPose = new Pose2d(0, 0, Rotation2d.fromDegrees(0));

    public void resetPose(Pose2d pose) {
        var newPose = new Pose2d(pose.getY() * -1, pose.getX(), pose.getRotation());
        System.out.println("resseting pose to" + newPose.toString());
        poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(),
                newPose);
    }

    public Command zeroCommand() {
        return this.runOnce(() -> {
            navx.zero();
            poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(),
                    startPose);
        });
    }

    private final BoxysSwerveModule frontLeft = new BoxysSwerveModule("frontleft");
    private final BoxysSwerveModule frontRight = new BoxysSwerveModule("frontright");
    private final BoxysSwerveModule backLeft = new BoxysSwerveModule("backleft");
    private final BoxysSwerveModule backRight = new BoxysSwerveModule("backright");

    GenericEntry skibidi = Shuffleboard.getTab("field").add("tag-count", 0).getEntry();

    public void periodic() {
        poseEstimatorField.setRobotPose(getPose());

        LimelightHelpers.PoseEstimate limelightMeasurement = LimelightHelpers
                .getBotPoseEstimate_wpiBlue("limelight-right");
        if (limelightMeasurement.tagCount >= 1) {
            poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(0.5, 0.5, 0.5));
            poseEstimator.addVisionMeasurement(
                    limelightMeasurement.pose,
                    limelightMeasurement.timestampSeconds);
        }
        // System.out.println(limelightMeasurement.tagCount);
        skibidi.setDouble(limelightMeasurement.tagCount);

        limeLightField.setRobotPose(limelightMeasurement.pose);
        gyroAngle.setDouble(getGyroscopeRotation().getDegrees());
        poseEstimator.update(getGyroscopeRotation(), getModulePositions());
        // System.out.println(MAX_VELOCITY_METERS_PER_SECOND);
        // System.out.println(LimelightHelpers.getBotPose2d_wpiBlue("limelight-right"));
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    NavXWrapper navx = new NavXWrapper();

    public Rotation2d getGyroscopeRotation() {
        return navx.getAngle();
    }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[] {
                frontLeft.getPosition(),
                frontRight.getPosition(),
                backLeft.getPosition(),
                backRight.getPosition()
        };
    }

    public SwerveModuleState[] getModuleStates() {
        return new SwerveModuleState[] {
                frontLeft.getState(),
                frontRight.getState(),
                backLeft.getState(),
                backRight.getState()
        };
    }

    private static final Vector<N3> visionMeasurementStdDevs = VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(10));
    public static final double DRIVETRAIN_TRACKWIDTH_METERS = inchesToMeters(24.5);
    public static final double DRIVETRAIN_WHEELBASE_METERS = inchesToMeters(24.5);
    private static final Vector<N3> stateStdDevs = VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5));

    public static final SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            // // Front left
            // new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
            // DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // // Front right
            // new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
            // -DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // // Back left
            // new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
            // DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // // Back right
            // new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
            // -DRIVETRAIN_WHEELBASE_METERS / 2.0)

            // Front left
            new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // Front right
            new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // Back left
            new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0),
            // Back right
            new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0));

    SwerveDrivePoseEstimator poseEstimator = new SwerveDrivePoseEstimator(
            KINEMATICS,
            getGyroscopeRotation(),
            getModulePositions(),
            startPose,
            stateStdDevs,
            visionMeasurementStdDevs);
}