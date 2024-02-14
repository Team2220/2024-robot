package frc.robot;

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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static edu.wpi.first.math.util.Units.inchesToMeters;

import java.lang.reflect.Field;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.hardware.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PathPlannerLogging;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.RobotInstance;
import frc.lib.TalonFXSubsystem;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.CheckableSubsystem;

/**
 * Standard deviations of the vision measurements. Increase these numbers to
 * trust global measurements from vision
 * less. This matrix is in the form [x, y, theta]ᵀ, with units in meters and
 * radians.
 */

public class DriveTrain extends SubsystemBase implements TalonFXSubsystem,CheckableSubsystem{
    

    double driveRadius = Math
            .sqrt(Math.pow(DRIVETRAIN_TRACKWIDTH_METERS / 2, 2) + Math.pow(DRIVETRAIN_WHEELBASE_METERS / 2, 2));

    public DriveTrain() {
        Shuffleboard.getTab("field").add("Field", m_field);
        AutoBuilder.configureHolonomic(
                this::getPose, // Robot pose supplier
                this::resetPose, // Method to reset odometry (will be called if your auto has a starting pose)
                this::getSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
                this::autoDriveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
                new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your
                                                 // Constants class
                        new PIDConstants(0.3, 0.0, 0.025456738383963862983267), // Translation PID constants
                        new PIDConstants(0.3, 0.0, 0.025621832482875328792385), // Rotation PID constants
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
            m_field.getObject("path").setPoses(poses);
            for (Pose2d pose : poses) {
                System.out.println(pose);
            }
        });
    }

    GenericEntry gyroAngle = Shuffleboard.getTab("swerve").add("gyroAngle", 0).getEntry();
    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        driveRobotRelative(
                fieldRelative
                        ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, getGyroscopeRotation())
                        : new ChassisSpeeds(xSpeed, ySpeed, rot));
    }


    
    public void driveRobotRelative(ChassisSpeeds speed) {
        var swerveModuleStates = KINEMATICS.toSwerveModuleStates(speed);
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, MAX_VELOCITY_METERS_PER_SECOND);
        m_frontLeft.setDesiredState(swerveModuleStates[0]);
        m_frontRight.setDesiredState(swerveModuleStates[1]);
        m_backLeft.setDesiredState(swerveModuleStates[2]);
        m_backRight.setDesiredState(swerveModuleStates[3]);
    }
    @Override
    public TalonFX[] getTalonFXs() {
        return new TalonFX[]{
            m_backLeft.getM_driveMotor(),
            m_backLeft.getM_turningMotor(),
            m_frontLeft.getM_driveMotor(),
            m_frontLeft.getM_turningMotor(),
            m_backRight.getM_driveMotor(),
            m_backRight.getM_turningMotor(),
            m_frontRight.getM_driveMotor(),
            m_frontRight.getM_turningMotor(),
        };
    }

    @Override
    public CheckCommand[] getCheckCommands() {
        return new CheckCommand[]{};
    }

    public void autoDriveRobotRelative(ChassisSpeeds speed) {
        driveRobotRelative(
                new ChassisSpeeds(speed.vyMetersPerSecond * -1.320011, speed.vxMetersPerSecond * 1.320011, speed.omegaRadiansPerSecond));
    }

    private final Field2d m_field = new Field2d();
    private final Pose2d m_startPose = new Pose2d(0, 0, Rotation2d.fromDegrees(0));

    public void resetPose(Pose2d pose) {
        poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(), pose);
        System.out.println("fortnite");
    }

    public Command zeroCommand() {
        return this.runOnce(() -> {
            navx.reset();
            poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions(), m_startPose);
        });
    }

    public Command driveCommand(DoubleSupplier xspeed, DoubleSupplier yspeed, DoubleSupplier rot) {
        return this.run(() -> {
            this.drive(
                    xspeed.getAsDouble() * MAX_VELOCITY_METERS_PER_SECOND * .25,
                    yspeed.getAsDouble() * MAX_VELOCITY_METERS_PER_SECOND * -.25,
                    rot.getAsDouble() * MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND / 2.0, true);
        });
    }

    public ChassisSpeeds getSpeeds() {
        return KINEMATICS.toChassisSpeeds(getModuleStates());
    }

    public static final double DT_BL_SE_OFFSET = RobotInstance.config((robot) -> {
        return switch (robot) {
            case Robot23 -> 8.96484375 - 90;
            case Robot24 -> 346.81640625 - 90;
        };
    });

    public static final double DT_FR_SE_OFFSET = RobotInstance.config((robot) -> {
        return switch (robot) {
            case Robot23 -> 124.98046875 - 90;
            case Robot24 -> 153.80859375 - 90;
        };
    });

    public static final double DT_FL_SE_OFFSET = RobotInstance.config((robot) -> {
        return switch (robot) {
            case Robot23 -> 155.302734375 - 90;
            case Robot24 -> 38.84765625 - 90;
        };
    });

    public static final double DT_BR_SE_OFFSET = RobotInstance.config((robot) -> {
        return switch (robot) {
            case Robot23 -> 247.5 - 90;
            case Robot24 -> 352.705078125 - 90;
        };
    });

    private final SwerveModule m_frontLeft = new SwerveModule("frontleft", 12, 11, 1, DT_FL_SE_OFFSET);
    private final SwerveModule m_frontRight = new SwerveModule("frontright", 18, 17, 2, DT_FR_SE_OFFSET);
    private final SwerveModule m_backLeft = new SwerveModule("backleft", 16, 15, 3, DT_BL_SE_OFFSET);
    private final SwerveModule m_backRight = new SwerveModule("backright", 14, 13, 0, DT_BR_SE_OFFSET);

    public void periodic() {
        m_field.setRobotPose(getPose());
        gyroAngle.setDouble(getGyroscopeRotation().getDegrees());
        poseEstimator.update(
                getGyroscopeRotation(), getModulePositions());
        //System.out.println(MAX_VELOCITY_METERS_PER_SECOND);        
    }

    private Pose2d getPose() {
        var pose = poseEstimator.getEstimatedPosition();
        return new Pose2d(pose.getY() * -1, pose.getX(), pose.getRotation());
    }

    AHRS navx = new AHRS();
    double gyroOffset = 0;

    public Rotation2d getGyroscopeRotation() {
        var angle = navx.getAngle() + gyroOffset;
        return Rotation2d.fromDegrees(angle * -1);
    }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[] {
                m_frontLeft.getPosition(),
                m_frontRight.getPosition(),
                m_backLeft.getPosition(),
                m_backRight.getPosition()
        };
    }

    public SwerveModuleState[] getModuleStates() {
        return new SwerveModuleState[] {
                m_frontLeft.getState(),
                m_frontRight.getState(),
                m_backLeft.getState(),
                m_backRight.getState()
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
            m_startPose,
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
            m_backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-135)));
            m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
            m_backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(135)));
            m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
        });
    }
    
    public Command slowMode() {
        return this.run(() -> {
            m_backLeft.setDesiredState(new SwerveModuleState(DriveTrain.MAX_VELOCITY_METERS_PER_SECOND * .25, Rotation2d.fromDegrees(90)));
            m_frontLeft.setDesiredState(new SwerveModuleState(DriveTrain.MAX_VELOCITY_METERS_PER_SECOND * .25, Rotation2d.fromDegrees(90)));
            m_backRight.setDesiredState(new SwerveModuleState(DriveTrain.MAX_VELOCITY_METERS_PER_SECOND * .25, Rotation2d.fromDegrees(90)));
            m_frontRight.setDesiredState(new SwerveModuleState(DriveTrain.MAX_VELOCITY_METERS_PER_SECOND * .25, Rotation2d.fromDegrees(90)));
        });
    }
}
