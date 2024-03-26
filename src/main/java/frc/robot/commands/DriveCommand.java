package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.DriveTrain;

public class DriveCommand extends Command {
    private BooleanSupplier slow;
    private DoubleSupplier xspeed;
    private DoubleSupplier yspeed;
    private DoubleSupplier rot;
    private DriveTrain driveTrain;
    PIDController controller = new PIDController(
            DriveTrain.rotationConstants.kP,
            DriveTrain.rotationConstants.kI,
            DriveTrain.rotationConstants.kD);
    double wantedAngle = 0;
    boolean pid = false;

    public DriveCommand(
            DoubleSupplier xspeed,
            DoubleSupplier yspeed,
            DoubleSupplier rot,
            BooleanSupplier slow,
            DriveTrain driveTrain) {
        this.slow = slow;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.rot = rot;
        this.driveTrain = driveTrain;
        addRequirements(driveTrain);
    }
    // if(m_driverController.getHID().getPOV() != -1){
    // pid = true;
    // }

    @Override
    public void execute() {
        double coefficient = slow.getAsBoolean() ? 0.5 : 1;
        this.driveTrain.drive(
                this.xspeed.getAsDouble() * coefficient,
                this.yspeed.getAsDouble() * coefficient,
                this.rot.getAsDouble() * coefficient,
                true);
    }
}
