package frc.lib.selfCheck;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import frc.lib.units.UnitsUtil;
import frc.robot.SwerveModule;

public class SwerveModuleSelfCheck extends CheckCommand {
    private SwerveModule module;
    Measure<Angle> position;
    Measure<Angle> tolerance;

    public SwerveModuleSelfCheck(SwerveModule module, Measure<Angle> position, Measure<Angle> tolerance) {
        this.module = module;
        this.position = position;
        this.tolerance = tolerance;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        module.setDesiredState(new SwerveModuleState(0, new Rotation2d(position)));
    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        var currentPosition = Units.Degrees.of(module.getPosition().angle.getDegrees());
        var diff = (currentPosition.minus(position));
        return UnitsUtil.abs(diff).lte(tolerance);
        
    }

    @Override
    public double getTimeoutSeconds() {
        return 5;
         }

    @Override
    public String getDescription() {
        return "set Position Of " + module.getName() + " to " + position.toLongString();
       

    }
}
