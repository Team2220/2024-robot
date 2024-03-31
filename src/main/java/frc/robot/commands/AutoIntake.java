package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

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
            intake.setSpeed(0.45);
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