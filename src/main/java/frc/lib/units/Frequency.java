package frc.lib.units;

import edu.wpi.first.units.UnaryFunction;
import edu.wpi.first.units.Unit;

public class Frequency extends Unit<Frequency> {
    /** Creates a new unit with the given name and multiplier to the base unit. */
    Frequency(double baseUnitEquivalent, String name, String symbol) {
        super(Frequency.class, baseUnitEquivalent, name, symbol);
    }

    Frequency(
            UnaryFunction toBaseConverter, UnaryFunction fromBaseConverter, String name, String symbol) {
        super(Frequency.class, toBaseConverter, fromBaseConverter, name, symbol);
    }

}
