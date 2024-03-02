package frc.lib;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import frc.lib.tunables.TunableDouble;

public interface ShuffleBoardTabWrapper {
    default void addGraph(String name, DoubleSupplier supplier) {
        Shuffleboard.getTab(getName())
                .addDouble(name, supplier)
                .withWidget(BuiltInWidgets.kGraph);
    }

    default <U extends Unit<U>> void addGraph(String name, Supplier<Measure<U>> supplier, Unit<U> unit) {
        Shuffleboard.getTab(getName())
                .addDouble(name, () -> {
                    return supplier.get().in(unit);
                })
                .withWidget(BuiltInWidgets.kGraph);
    }

    default <U extends Unit<U>> void addMeasure(String name, Supplier<Measure<U>> supplier) {
        Shuffleboard.getTab(getName())
                .addString(name, () -> {
                    return supplier.get().toLongString();
                });
    }

    String getName();

    default TunableDouble addTunableDouble(String name, double defaultValue) {
        return new TunableDouble(name, defaultValue, getName());
    }

    default SuppliedValueWidget<Double> addDouble(String name, DoubleSupplier supplier) {
        return Shuffleboard.getTab(getName()).addDouble(name, supplier);
    }

    default SuppliedValueWidget<Boolean> addBoolean(String name, BooleanSupplier supplier) {
        return Shuffleboard.getTab(getName()).addBoolean(name, supplier);
    }

}
