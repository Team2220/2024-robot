package frc.lib.drivetrain;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.controller.PIDController;
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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import static edu.wpi.first.math.util.Units.inchesToMeters;
import static edu.wpi.first.units.Units.Degrees;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.hardware.TalonFX;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PathPlannerLogging;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.NavXWrapper;
import frc.lib.limeLight.LimelightHelpers;
import frc.lib.limeLight.LimelightHelpers.PoseEstimate;
import frc.lib.music.TalonFXSubsystem;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;
import frc.lib.selfCheck.SwerveModuleSelfCheck;
import frc.lib.selfCheck.UnwrappedTalonSpinCheck;

/**
 * Standard deviations of the vision measurements. Increase these numbers to
 * trust global measurements from vision
 * less. This matrix is in the form [x, y, theta]ᵀ, with units in meters and
 * radians.
 */

public class DriveTrain extends SubsystemBase implements TalonFXSubsystem, CheckableSubsystem, ShuffleBoardTabWrapper {
    public static final PIDConstants rotationConstants = new PIDConstants(2, 0.0, 0.3);

    double driveRadius = Math
            .sqrt(Math.pow(DRIVETRAIN_TRACKWIDTH_METERS / 2, 2) + Math.pow(DRIVETRAIN_WHEELBASE_METERS / 2, 2));
    private final SwerveModule frontLeft;
    private final SwerveModule frontRight;
    private final SwerveModule backLeft;
    private final SwerveModule backRight;

    public DriveTrain(double frontLeftOffset, double frontRightOffset, double backLeftOffset, double backRightOffset) {
        frontLeft = new SwerveModule(
            "frontleft",
            12,
            11,
            1,
            frontLeftOffset
        );
        frontRight = new SwerveModule(
            "frontright",
            18,
            17,
            2,
            frontRightOffset
        );
        backLeft = new SwerveModule(
            "backleft",
            16,
            15,
            3,
            backLeftOffset
        );
        backRight = new SwerveModule(
            "backright",
            14,
            13,
            0,
            backRightOffset
        );
        Shuffleboard.getTab("field").add("Field", poseEstimatorField).withSize(4, 3);
        Shuffleboard.getTab("limeLight").add("limeLight", limeLightField)
                .withSize(4, 4)
                .withPosition(0, 0);
        GenericEntry bruh = Shuffleboard.getTab("field").add("tag-count", 0).getEntry();
        // System.out.println("Velocity" + MAX_VELOCITY_METERS_PER_SECOND);
        AutoBuilder.configureHolonomic(
                this::getPose, // Robot pose supplier
                this::resetPose, // Method to reset odometry (will be called if your auto has a starting pose)
                this::getSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
                this::autoDriveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
                new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your
                                                 // Constants class
                        new PIDConstants(.001, 0.0, 0.0), // Translation PID constants
                        rotationConstants, // Rotation PID constants
                        MAX_VELOCITY_METERS_PER_SECOND, // Max module speed, in m/s
                        driveRadius, // Drive base radius in meters. Distance from robot center to furthest module.
                        new ReplanningConfig() // Default path replanning config. See the API for the options here
                ),
                () -> {
                    // Boolean supplier that controls when the path will be mirrored for the red
                    // alliance
                    // This will flip the path being followed to the red side of the field.
                    // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

                    var alliance = DriverStation.getAlliance();
                    if (alliance.isPresent()) {
                        return alliance.get() == DriverStation.Alliance.Red;
                    }
                    return false;
                },
                this // Reference to this subsystem to set requirements
        );
        // Set up custom logging to add the current path to a field 2d widget
        PathPlannerLogging.setLogActivePathCallback((poses) -> {
            poseEstimatorField.getObject("path").setPoses(poses);
            // for (Pose2d pose : poses) {
            // System.out.println(pose);
            // }
        });

        addGraph("GyroRate", navx::getRate);
    }

    GenericEntry gyroAngle = Shuffleboard.getTab("swerve").add("gyroAngle", 0).getEntry();

    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        double x = xSpeed * MAX_VELOCITY_METERS_PER_SECOND * -1;
        double y = ySpeed * MAX_VELOCITY_METERS_PER_SECOND * -1;
        double r = rot * MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND * -1;
        driveRobotRelative(
                fieldRelative
                        ? ChassisSpeeds.fromFieldRelativeSpeeds(x, y, r, getGyroscopeRotation())
                        : new ChassisSpeeds(x, y, r));
    }

    public void driveRobotRelative(ChassisSpeeds speed) {
        var swerveModuleStates = KINEMATICS.toSwerveModuleStates(speed);
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, MAX_VELOCITY_METERS_PER_SECOND);
        frontLeft.setDesiredState(swerveModuleStates[0]);
        frontRight.setDesiredState(swerveModuleStates[1]);
        backLeft.setDesiredState(swerveModuleStates[2]);
        backRight.setDesiredState(swerveModuleStates[3]);
    }

    @Override
    public TalonFX[] getTalonFXs() {
        return new TalonFX[] {
                backLeft.getDriveMotor(),
                backLeft.getTurningMotor(),
                frontLeft.getDriveMotor(),
                frontLeft.getTurningMotor(),
                backRight.getDriveMotor(),
                backRight.getTurningMotor(),
                frontRight.getDriveMotor(),
                frontRight.getTurningMotor(),
        };
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[] {
                // new UnwrappedTalonSpinCheck("backLeftDrive", backLeft.getDriveMotor(), true),
                // new UnwrappedTalonSpinCheck("backLeftDrive", backLeft.getDriveMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("backLeftTurn", backLeft.getTurningMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("backLeftTurn", backLeft.getTurningMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("frontLeftDrive", frontLeft.getDriveMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("frontLeftDrive", frontLeft.getDriveMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("frontLeftTurn", frontLeft.getTurningMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("frontLeftTurn", frontLeft.getTurningMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("backRightDrive", backRight.getDriveMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("backRightDrive", backRight.getDriveMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("backRightTurn", backRight.getTurningMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("backRightTurn", backRight.getTurningMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("frontRightDrive", frontRight.getDriveMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("frontRightDrive", frontRight.getDriveMotor(),
                // false),
                // new UnwrappedTalonSpinCheck("frontRightTurn", frontRight.getTurningMotor(),
                // true),
                // new UnwrappedTalonSpinCheck("frontRightTurn", frontRight.getTurningMotor(),
                // false),
                new SwerveModuleSelfCheck(backLeft, Degrees.of(90), Degrees.of(5))
        };
    }

    public void autoDriveRobotRelative(ChassisSpeeds speed) {
        driveRobotRelative(
                new ChassisSpeeds(speed.vyMetersPerSecond * 1.55, speed.vxMetersPerSecond * -1.55,
                        speed.omegaRadiansPerSecond));
    }

    private final Field2d poseEstimatorField = new Field2d();
    private final Field2d limeLightField = new Field2d();
    private final Pose2d startPose = new Pose2d(0, 0, Rotation2d.fromDegrees(0));

    public void resetPose(Pose2d pose) {
        var newPose = new Pose2d(pose.getY() * -1, pose.getX(), pose.getRotation());
        System.out.println("resseting pose to" + newPose.toString());
        poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(), newPose);
    }

    public Command zeroCommand() {
        return this.runOnce(() -> {
            navx.zero();
            poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(), startPose);
        });
    }

    public ChassisSpeeds getSpeeds() {
        return KINEMATICS.toChassisSpeeds(getModuleStates());
    }

    public void periodic() {
        poseEstimatorField.setRobotPose(getPose());

        LimelightHelpers.PoseEstimate limelightMeasurement = LimelightHelpers
                .getBotPoseEstimate_wpiBlue("limelight-right");
        if (limelightMeasurement != null) {
            if (limelightMeasurement.tagCount >= 1) {
                poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(0.5, 0.5, 0.5));
                poseEstimator.addVisionMeasurement(
                        limelightMeasurement.pose,
                        limelightMeasurement.timestampSeconds);
            }
            limeLightField.setRobotPose(limelightMeasurement.pose);
        }
        gyroAngle.setDouble(getGyroscopeRotation().getDegrees());
        poseEstimator.update(
                getGyroscopeRotation(), getModulePositions());
        // System.out.println(MAX_VELOCITY_METERS_PER_SECOND);
        // System.out.println(LimelightHelpers.getBotPose2d_wpiBlue("limelight-name"));
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
        // return new Pose2d(pose.getX(), pose.getY(), pose.getRotation());
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

    /**
     * The maximum velocity of the robot in meters per second.
     * <p>
     * This is a measure of how fast the robot should be able to drive in a straight
     * line.
     */
    public static final double MAX_VELOCITY_METERS_PER_SECOND = 6380.0 / 60.0 / SwerveModule.DT_DRIVE_GEAR_RATIO
            * SwerveModule.DT_WHEEL_DIAMETER * Math.PI;
    // ModuleConfiguration.MK4I_L2.getDriveReduction() *
    // ModuleConfiguration.MK4I_L2.getWheelDiameter() * PI;

    /**
     * The maximum angular velocity of the robot in radians per second.
     * <p>
     * This is a measure of how fast the robot can rotate in place.
     */
    // Here we calculate the theoretical maximum angular velocity. You can also
    // replace this with a measured amount.
    public static final double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = (MAX_VELOCITY_METERS_PER_SECOND /
            Math.hypot(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0));

    public Command xcommand() {
        return this.run(() -> {
            backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-135)));
            frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
            backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(135)));
            frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
        });
    }
}