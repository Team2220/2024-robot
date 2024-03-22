package frc.lib.tunables;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Units;

public class TunableDebouncer {
    private Debouncer debouncer;
    private TunableMeasure<Time> measureDebouncer;
    private String name;
    private String tab;

    public TunableDebouncer(String name, String tab, Measure<Time> debounceTime, DebounceType debounceType) {
        debouncer = new Debouncer(debounceTime.in(Units.Seconds), debounceType);
        measureDebouncer = new TunableMeasure<>(name, debounceTime, tab, (time) -> {
            this.debouncer = new Debouncer(time.in(Units.Seconds), debounceType);
        });
    }

    public boolean calculate(boolean input) {
        return debouncer.calculate(input);
    }
}
