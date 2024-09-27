package frc.robot.Robot24.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot24.subsystems.Intake;

public class AutoIntake extends Command {
    Intake intake;
    boolean hasSeenNote = false;

    public AutoIntake(Intake intake) {
        this.intake = intake;

    }

    @Override
    public void initialize() {
        hasSeenNote = false;
    }

    @Override
    public void execute() {
        if (intake.getTopNoteSensor()) {
            intake.setSpeed(0);
        } else if (hasSeenNote) {
            intake.setSpeed(0.65, .4);
        } else if (intake.getBottomNoteSensor()) {
            hasSeenNote = true;
        }
        else{
            intake.setSpeed(0.75);
        } 

    }
    @Override
    public void end(boolean interrupted) {
        intake.setSpeed(0);
    }
}