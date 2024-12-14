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
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import static edu.wpi.first.math.util.Units.inchesToMeters;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Radian;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import java.util.function.DoubleSupplier;
import java.util.stream.Stream;

import org.ejml.equation.Function;

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
import frc.lib.units.UnitsUtil;
import frc.robot.Robot24.Constants;

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

    public DriveTrain(Measure<Angle> frontLeftOffset, Measure<Angle> frontrightoffset, Measure<Angle> backleftoffset, Measure<Angle> backrightoffset) {
        this.frontLeft = new SwerveModule(
                "frontleft",
                12,
                11,
                1,
                frontLeftOffset);
        this.frontRight = new SwerveModule(
                "frontright",
                18,
                17,
                2,
                frontrightoffset);
        this.backLeft = new SwerveModule(
                "backleft",
                16,
                15,
                3,
                backleftoffset);
        this.backRight = new SwerveModule(
                "backright",
                14,
                13,
                0,
                backrightoffset);

        this.poseEstimator = new SwerveDrivePoseEstimator(
                KINEMATICS,
                getGyroscopeRotation(),
                getModulePositions(),
                startPose,
                stateStdDevs,
                visionMeasurementStdDevs);

        Shuffleboard.getTab("field").add("Field", poseEstimatorField).withSize(4, 3);
        Shuffleboard.getTab("limeLight").add("limeLight", limeLightField)
                .withSize(4, 4)
                .withPosition(0, 0);
        AutoBuilder.configureHolonomic(
                this::getPose, // Robot pose supplier
                this::resetPose, // Method to reset odometry (will be called if your auto has a starting pose)
                this::getSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
                this::autoDriveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
                new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your
                                                 // Constants class
                        new PIDConstants(.001, 0.0, 0.0), // Translation PID constants
                        rotationConstants, // Rotation PID constants
                        MAX_VELOCITY.in(MetersPerSecond), // Max module speed, in m/s
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
        Measure<Velocity<Distance>> x = MAX_VELOCITY.times(-xSpeed);
        Measure<Velocity<Distance>> y = MAX_VELOCITY.times(-ySpeed);
        Measure<Velocity<Angle>> r = MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND.times(-rot);
        driveRobotRelative(
                fieldRelative
                        ? ChassisSpeeds.fromFieldRelativeSpeeds(x, y, r, getGyroscopeRotation())
                        : new ChassisSpeeds(x, y, r));
    }

    public void driveRobotRelative(ChassisSpeeds speed) {
        var swerveModuleStates = KINEMATICS.toSwerveModuleStates(speed);
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, MAX_VELOCITY);
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
        return Stream.of(
            backLeft.getCheckCommands(),
            frontLeft.getCheckCommands(),
            backRight.getCheckCommands(),
            frontRight.getCheckCommands()
        ).flatMap(Stream::of)
        .toArray(CheckCommand[]::new);
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

    public void toggleCoast(){
        frontLeft.toggleCoast();
        frontRight.toggleCoast();
        backLeft.toggleCoast();
        backRight.toggleCoast();


    };


    private final SwerveModule frontLeft;
    private final SwerveModule frontRight;
    private final SwerveModule backLeft;
    private final SwerveModule backRight;

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

    private final SwerveDrivePoseEstimator poseEstimator;

    /**
     * The maximum velocity of the robot in meters per second.
     * <p>
     * This is a measure of how fast the robot should be able to drive in a straight
     * line.
     */
    public static final Measure<Velocity<Distance>> MAX_VELOCITY = UnitsUtil.velocityForWheel(
        SwerveModule.DT_WHEEL_DIAMETER,
                RotationsPerSecond.of(6380.0 / 60.0 / SwerveModule.DT_DRIVE_GEAR_RATIO));
        
    // ModuleConfiguration.MK4I_L2.getDriveReduction() *
    // ModuleConfiguration.MK4I_L2.getWheelDiameter() * PI;

    /**
     * The maximum angular velocity of the robot in radians per second.
     * <p>
     * This is a measure of how fast the robot can rotate in place.
     */
    // Here we calculate the theoretical maximum angular velocity. You can also
    // replace this with a measured amount.
    public static final Measure<Velocity<Angle>> MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = RadiansPerSecond.of(MAX_VELOCITY.in(MetersPerSecond) /
            Math.hypot(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0));

    public Command xcommand() {
        return this.run(() -> {
            backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-135)));
            frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
            backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(135)));
            frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
        });
    }

    public Command zeroTurningMotors() {
        return this.runOnce(() -> {
            backLeft.zeroTurningMotor();
            backRight.zeroTurningMotor();
            frontLeft.zeroTurningMotor();
            frontRight.zeroTurningMotor();
        });
    }
}