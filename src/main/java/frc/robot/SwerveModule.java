// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.configs.AudioConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib.PWMEncoder;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.tunables.TunableDouble;

public class SwerveModule implements ShuffleBoardTabWrapper {
  GenericEntry speed;
  GenericEntry angle;
  GenericEntry drivePositionEntry;
  private final TalonFX m_driveMotor;
  private final TalonFX m_turningMotor;

  private final PWMEncoder m_turningEncoder;

  public static final double DT_WHEEL_DIAMETER = Units.inchesToMeters(4);

  // Drive gear ratio (that number is the number of revolutions of the motor to
  // get one revolution of the output)
  public static final double DT_DRIVE_GEAR_RATIO = (50.0 / 14.0) * (16.0 / 28.0) * (45.0 / 15.0);
  // Drive motor inverted
  public static final boolean DT_DRIVE_MOTOR_INVERTED = true;

  // Steer gear ratio (that number is the number of revolutions of the steer motor
  // to get one revolution of the output)
  public static final double DT_STEER_GEAR_RATIO = 150.0 / 7.0;
  // Steer motor inverted
  public static final boolean DT_STEER_MOTOR_INVERTED = false;

  // Steer encoder gear ratio
  public static final double DT_STEER_ENCODER_GEAR_RATIO = 1;
  // Steer encoder inverted
  public static final boolean DT_STEER_ENCODER_INVERTED = false;

  private double offset;
  private String name;

  public SwerveModule(
      String name, int driveMotorChannel,
      int turningMotorChannel,
      int turningEncoderChannelA, double offset) {
    this.offset = offset;
    this.name = name;
    m_driveMotor = new TalonFX(driveMotorChannel);
    m_driveMotor.setNeutralMode(NeutralModeValue.Brake);
    m_driveMotor.getConfigurator().apply(new TalonFXConfiguration());
    m_turningMotor = new TalonFX(turningMotorChannel);
    m_turningMotor.setNeutralMode(NeutralModeValue.Brake);
    m_turningMotor.getConfigurator().apply(new TalonFXConfiguration());
    CurrentLimitsConfigs currentConfigs = new CurrentLimitsConfigs();
    currentConfigs.StatorCurrentLimit = 60;
    currentConfigs.StatorCurrentLimitEnable = true;
    currentConfigs.SupplyCurrentLimit = 60;
    currentConfigs.SupplyCurrentLimitEnable = true;
    m_driveMotor.getConfigurator().apply(currentConfigs);
    m_turningMotor.getConfigurator().apply(currentConfigs);
    VoltageConfigs voltageConfigs = new VoltageConfigs();
    voltageConfigs.PeakForwardVoltage = 10;
    voltageConfigs.PeakReverseVoltage = -10;
    m_driveMotor.getConfigurator().apply(voltageConfigs);
    m_turningMotor.getConfigurator().apply(voltageConfigs);
    AudioConfigs audioConfigs = new AudioConfigs();
    audioConfigs.BeepOnBoot = false;
    audioConfigs.BeepOnConfig = false;
    audioConfigs.AllowMusicDurDisable = true;
    m_driveMotor.getConfigurator().apply(audioConfigs);
    m_turningMotor.getConfigurator().apply(audioConfigs);

    m_turningEncoder = new PWMEncoder(turningEncoderChannelA);
    speed = Shuffleboard.getTab("swerve").add(name + " speed", 0).getEntry();
    angle = Shuffleboard.getTab("swerve").add(name + " angle", 0).getEntry();
    drivePositionEntry = Shuffleboard.getTab("swerve").add(name + " drivePostion", 0).getEntry();
    Shuffleboard.getTab("swerve").addDouble(name + "encoder", m_turningEncoder::getPosition);
    var driveConfigs = new Slot0Configs();
    var steerConfigs = new Slot0Configs();
    SwerveModule.DT_DRIVE_P.addChangeListener((value) -> {
      driveConfigs.kP = value;
      m_driveMotor.getConfigurator().apply(driveConfigs);
    });
    SwerveModule.DT_DRIVE_I.addChangeListener((value) -> {
      driveConfigs.kI = value;
      m_driveMotor.getConfigurator().apply(driveConfigs);
    });
    SwerveModule.DT_DRIVE_D.addChangeListener((value) -> {
      driveConfigs.kD = value;
      m_driveMotor.getConfigurator().apply(driveConfigs);
    });
    SwerveModule.DT_DRIVE_F.addChangeListener((value) -> {
      driveConfigs.kV = value;
      m_driveMotor.getConfigurator().apply(driveConfigs);
    });
    SwerveModule.DT_STEER_P.addChangeListener((value) -> {
      steerConfigs.kP = value;
      m_turningMotor.getConfigurator().apply(steerConfigs);
    });
    SwerveModule.DT_STEER_I.addChangeListener((value) -> {
      steerConfigs.kI = value;
      m_turningMotor.getConfigurator().apply(steerConfigs);

    });
    SwerveModule.DT_STEER_D.addChangeListener((value) -> {
      steerConfigs.kD = value;
      m_turningMotor.getConfigurator().apply(steerConfigs);
    });
    SwerveModule.DT_STEER_F.addChangeListener((value) -> {
      steerConfigs.kV = value;
      m_turningMotor.getConfigurator().apply(steerConfigs);
    });

    m_turningMotor.setPosition(-angleToEncoderTicks(getAngle().getDegrees()));

  //   Shuffleboard.getTab("swerve")
  //       .addDouble(name, this::getDriveVelocity)
  //       .withWidget(BuiltInWidgets.kGraph).withSize(1, 1);
  }

  public SwerveModuleState getState() {
    return new SwerveModuleState(
        getDriveVelocity(), getAngle());
  }

  private double getDriveVelocity() {
    double ticks = m_driveMotor.getVelocity().getValueAsDouble();
    double revolutionsMotorToRevolutionsWheel = 1.0 / DT_DRIVE_GEAR_RATIO // Reduction from motor to output
        * (1.0 / (SwerveModule.DT_WHEEL_DIAMETER * Math.PI));

    return ticks * revolutionsMotorToRevolutionsWheel;
  }

  public static final TunableDouble DT_DRIVE_P = new TunableDouble("DT_DRIVE_P", 0.025, "swerve").setSpot(0, 0);
  public static final TunableDouble DT_DRIVE_I = new TunableDouble("DT_DRIVE_I", 0, "swerve").setSpot(1, 0);
  public static final TunableDouble DT_DRIVE_D = new TunableDouble("DT_DRIVE_D", 0.00001, "swerve").setSpot(2, 0);
  public static final TunableDouble DT_DRIVE_F = new TunableDouble("DT_DRIVE_F", 0, "swerve").setSpot(3, 0);

  // PID values for the steer motor
  public static final TunableDouble DT_STEER_P = new TunableDouble("DT_STEER_P", 1, "swerve").setSpot(0, 1);
  public static final TunableDouble DT_STEER_I = new TunableDouble("DT_STEER_I", 0, "swerve").setSpot(1, 1);
  public static final TunableDouble DT_STEER_D = new TunableDouble("DT_STEER_D", 0.0001, "swerve").setSpot(2, 1);
  public static final TunableDouble DT_STEER_F = new TunableDouble("DT_STEER_F", 0, "swerve").setSpot(3, 1);

  private double getDrivePosition() {
    double ticks = m_driveMotor.getRotorPosition().getValueAsDouble();
    double revolutionsMotorToRevolutionsWheel = 1.0 / DT_DRIVE_GEAR_RATIO // Reduction from motor to output
        * (1.0 * (SwerveModule.DT_WHEEL_DIAMETER * Math.PI));

    return ticks * revolutionsMotorToRevolutionsWheel;
  }

  private double mpsToEncoderTicks(double mps) {
    double wheelRevolutions = (DT_WHEEL_DIAMETER * Math.PI);
    double motorRev = mps / wheelRevolutions * DT_DRIVE_GEAR_RATIO;
    double ticks = motorRev;
    return ticks;
  }

  private double angleToEncoderTicks(double angle) {
    double angleToWheelRev = angle / 360.0;
    double motorRev = angleToWheelRev * DT_STEER_GEAR_RATIO;
    return motorRev;
  }

  private double steerEncoderTicksToAngle(double ticks) {
    double motorRotation = ticks;
    double moduleRotation = motorRotation / DT_STEER_GEAR_RATIO;
    double angle = moduleRotation * 360;
    return angle;
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        getDrivePosition(), getAngle());
  }

  private Rotation2d getAngle() {
    var rAngle = Rotation2d.fromDegrees(m_turningEncoder.getPosition() - offset);
    return Rotation2d.fromDegrees(MathUtil.inputModulus(rAngle.getDegrees(), 0, 360));
  }

  public void setDesiredState(SwerveModuleState desiredState) {
    drivePositionEntry.setDouble(getDrivePosition());

    Rotation2d rotation2d = Rotation2d
        .fromDegrees(steerEncoderTicksToAngle(-m_turningMotor.getPosition().getValueAsDouble()));
    SwerveModuleState state = SwerveModuleState.optimize(desiredState, rotation2d);
    speed.setDouble(mpsToEncoderTicks(state.speedMetersPerSecond));
    m_driveMotor.setControl(new VelocityDutyCycle(mpsToEncoderTicks(state.speedMetersPerSecond) * -1));
    m_turningMotor.setControl(new PositionDutyCycle(
        angleToEncoderTicks(convertAngle(rotation2d.getDegrees(), state.angle.getDegrees()) * -1)));

  }

  public static double convertAngle(double start, double end) {
    int angleRevo = (int) (start / 360);
    start %= 360;
    end %= 360;
    double change = start - end;
    if (change > 270) {
      end += 360;
    } else if (change < -270) {
      end -= 360;
    }
    end += 360 * angleRevo;
    return end;
  }

  public TalonFX getM_driveMotor() {
    return m_driveMotor;
  }

  public TalonFX getM_turningMotor() {
    return m_turningMotor;
  }

  @Override
  public String getName() {
    return name;
  }
}
