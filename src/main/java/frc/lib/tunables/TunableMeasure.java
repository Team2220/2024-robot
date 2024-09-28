package frc.lib.tunables;

import java.util.function.Consumer;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import frc.lib.functional.BooleanObjectConsumer;

public class TunableMeasure<U extends Unit<U>> {
    private TunableDouble tunable;
    private U unit;

    public TunableMeasure(String name, Measure<U> defaultValue, String tab) {
        unit = defaultValue.unit();
        tunable = new TunableDouble(name, defaultValue.in(unit), tab);
    }

    public TunableMeasure(String name, Measure<U> defaultValue, String tab, Consumer<Measure<U>> onChange) {
        this(name, defaultValue, tab);
        addChangeListener(onChange);
    }

    public TunableMeasure(String name, Measure<U> defaultValue, String tab, BooleanObjectConsumer<Measure<U>> onChange) {
        this(name, defaultValue, tab);
        addChangeListener(onChange);
    }

    public Measure<U> getValue() {
        return unit.of(tunable.getValue());
    }

    public void addChangeListener(BooleanObjectConsumer<Measure<U>> onChange) {
        tunable.addChangeListener((isInit, value) -> {
            onChange.accept(isInit, unit.of(value));
        });
    }
        public void addChangeListener(Consumer<Measure<U>> onChange) {
            addChangeListener((isInit, value)->onChange.accept(value));
        }
}
