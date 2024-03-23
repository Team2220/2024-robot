// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.lib.CommandObserver;
import frc.lib.LoopTimer;
import frc.lib.eventLoops.EventLoops;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private Command m_testCommand;

  private RobotContainer m_robotContainer;

  @Override
  public void robotInit() {
    SignalLogger.start();
    DataLogManager.start();
    BuildConstToString buildConst = new BuildConstToString();
    DataLogManager.log(buildConst.toString());

    DriverStation.startDataLog(DataLogManager.getLog());

    CommandObserver.start();
    addPeriodic(EventLoops.everyLoop::poll, 0.02);
    addPeriodic(EventLoops.oncePerSec::poll, 1);
    addPeriodic(EventLoops.oncePerMin::poll, 60);

    DriverStation.silenceJoystickConnectionWarning(true);

    m_robotContainer = new RobotContainer();
    Shuffleboard.getTab("Scheduler").add("Scheduler", CommandScheduler.getInstance()).withSize(3, 2);
  }

  LoopTimer loopTimer = new LoopTimer("Robot Periodic", 15);

  @Override
  public void robotPeriodic() {
    loopTimer.measure(() -> {
      CommandScheduler.getInstance().run();
    });
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }


  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
