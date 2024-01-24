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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static edu.wpi.first.math.util.Units.inchesToMeters;

import java.util.function.DoubleSupplier;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Standard deviations of the vision measurements. Increase these numbers to
 * trust global measurements from vision
 * less. This matrix is in the form [x, y, theta]ᵀ, with units in meters and
 * radians.
 */

public class DriveTrain extends SubsystemBase {
    public DriveTrain() {
        SmartDashboard.putData("Field", m_field);

    }

    GenericEntry gyroAngle = Shuffleboard.getTab("swerve").add("gyroAngle", 0).getEntry();
    public static final double kMaxAngularSpeed = Math.PI; // 1/2 rotation per second
    public static final double kMaxSpeed = 3.0; // 3 meters per second

    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        var swerveModuleStates = KINEMATICS.toSwerveModuleStates(
                fieldRelative
                        ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, getGyroscopeRotation())
                        : new ChassisSpeeds(xSpeed, ySpeed, rot));
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, kMaxSpeed);
        m_frontLeft.setDesiredState(swerveModuleStates[0]);
        m_frontRight.setDesiredState(swerveModuleStates[1]);
        m_backLeft.setDesiredState(swerveModuleStates[2]);
        m_backRight.setDesiredState(swerveModuleStates[3]);
    }

    private final Field2d m_field = new Field2d();

    public Command zeroCommand() {
        return this.runOnce(() -> {
            navx.reset();
poseEstimator.resetPosition(getGyroscopeRotation(), getModulePositions() ,new Pose2d());
        });
    }

    public Command driveCommand(DoubleSupplier xspeed, DoubleSupplier yspeed, DoubleSupplier rot) {
        return this.run(() -> {
            this.drive(xspeed.getAsDouble(), yspeed.getAsDouble(), rot.getAsDouble(), true);
        });
    }
    
    //public static final double DT_FL_SE_OFFSET = 155.302734375 - 90;
    public static final double DT_FL_SE_OFFSET = 38.84765625 - 90;

    // Steer CANcoder offset front right
    //public static final double DT_FR_SE_OFFSET = 124.98046875 - 90;
    public static final double DT_FR_SE_OFFSET = 153.80859375 - 90;

    // Steer CANcoder offset back left
    //public static final double DT_BL_SE_OFFSET = 8.96484375 - 90;
    public static final double DT_BL_SE_OFFSET = 346.81640625 - 90;

    // Steer CANcoder offset back right
    //public static final double DT_BR_SE_OFFSET = 247.5 - 90;
    public static final double DT_BR_SE_OFFSET = 352.705078125 - 90;

    private final SwerveModule m_frontLeft = new SwerveModule("frontleft", 12, 11, 1, DT_FL_SE_OFFSET);
    private final SwerveModule m_frontRight = new SwerveModule("frontright", 18, 17, 2, DT_FR_SE_OFFSET);
    private final SwerveModule m_backLeft = new SwerveModule("backleft", 16, 15, 3, DT_BL_SE_OFFSET);
    private final SwerveModule m_backRight = new SwerveModule("backright", 14, 13, 0, DT_BR_SE_OFFSET);

    public void periodic() {
        m_field.setRobotPose(poseEstimator.getEstimatedPosition());
        gyroAngle.setDouble(getGyroscopeRotation().getDegrees());
        poseEstimator.update(
                getGyroscopeRotation(), getModulePositions());
    }

    AHRS navx = new AHRS();

    public Rotation2d getGyroscopeRotation() {
        var angle = navx.getAngle();
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
            new Pose2d(),
            stateStdDevs,
            visionMeasurementStdDevs);

    public Command xcommand() {
        return this.run(() -> {
        m_backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-135)));
        m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
        m_backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(135)));
        m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
        });
    }
}
