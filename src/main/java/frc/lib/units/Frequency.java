// Copyright (c) Ashwin Kalyan and Team 2220 Blue Twilight.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.lib.units;

import edu.wpi.first.units.UnaryFunction;
import edu.wpi.first.units.Unit;

/**
 * Unit of the number of waves that pass a fixed point in unit time.
 *
 * <p>
 * This is the base type for units of frequency.
 *
 * <p>
 * Actual units (such as {@link Units#hertz} and {@link Units#megaHertz}) can be
 * found in the
 * {@link UnitsUtil} class.
 */
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
