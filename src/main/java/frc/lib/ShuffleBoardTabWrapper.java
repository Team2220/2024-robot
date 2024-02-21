package frc.lib;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib.tunables.TunableDouble;

public interface ShuffleBoardTabWrapper {
    default void addGraph(String name, DoubleSupplier supplier) {
        Shuffleboard.getTab(getName())
                .addDouble(name, supplier)
                .withWidget(BuiltInWidgets.kGraph);
    }

    String getName();

    default TunableDouble addTunableDouble(String name, double defaultValue) {
        return new TunableDouble(name, defaultValue, getName());
    }

}
