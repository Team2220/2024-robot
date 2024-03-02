package frc.lib.tunables;

import java.util.function.Consumer;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;

public class TunableMeasure<U extends Unit<U>> {
    private String name;
    private Measure<U> defaultValue;
    private String tab;
    private TunableDouble tunable;
    private U unit;

    public TunableMeasure(String name, Measure<U> defaultValue, String tab) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.tab = tab;

        unit = defaultValue.unit();
        tunable = new TunableDouble(name, defaultValue.in(unit), tab);
    }

    public TunableMeasure(String name, Measure<U> defaultValue, String tab, Consumer<Measure<U>> onChange) {
        this(name, defaultValue, tab);
        addChangeListener(onChange);
    }

    public Measure<U> getValue() {
        return unit.of(tunable.getValue());
    }

    public void addChangeListener(Consumer<Measure<U>> onChange) {
        tunable.addChangeListener((value) -> {
            onChange.accept(unit.of(value));
        });
    }
}
