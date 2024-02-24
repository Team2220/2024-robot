package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

public class ShootCommand extends Command {
    Shooter shooter;
    Intake intake;

    public ShootCommand(Shooter shooter, Intake intake) {
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    public void execute() {
        shooter.setDefaultSpeed();
        if (shooter.isAtSetPoint()) {
            intake.setSpeed(.75);
        } 
    }

    public void end() {
        shooter.stopShooter();
        intake.setSpeed(0);
    }
}
