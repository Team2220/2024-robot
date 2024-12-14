package frc.lib.selfCheck;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import frc.lib.drivetrain.SwerveModule;
import frc.lib.units.UnitsUtil;

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
        System.out.println("initialize");
      System.out.println("position =" + position.in(Units.Degrees));
      System.out.println("getCurrentPosition =" + getCurrentPosition().in(Units.Degrees));
    }

    @Override
    public void execute() {
        module.setDesiredState(new SwerveModuleState(0, new Rotation2d(position)));
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("end");
        System.out.println("position =" + position.in(Units.Degrees));
        System.out.println("getCurrentPosition =" + getCurrentPosition().in(Units.Degrees));
    }

    @Override
    public boolean isFinished() {
        var currentPosition = UnitsUtil.inputModulus(getCurrentPosition(),Units.Degrees.of(0),Units.Degrees.of(180));
        var wrappedPosition = UnitsUtil.inputModulus(getCurrentPosition(),Units.Degrees.of(0),Units.Degrees.of(180));
        var diff = (currentPosition.minus(wrappedPosition));
        return UnitsUtil.abs(diff).lte(tolerance);
    }

    private Measure<Angle> getCurrentPosition() {
        return Units.Degrees.of(module.getPosition().angle.getDegrees());
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
