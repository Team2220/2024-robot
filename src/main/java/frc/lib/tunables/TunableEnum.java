package frc.lib.tunables;

import java.util.EnumSet;
import java.util.function.Consumer;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.lib.functional.BooleanObjectConsumer;

public class TunableEnum<V extends Enum<V>> {
  private V defaultValue;
  private ComplexWidget shuffleboardWidget;
  private final SendableChooser<V> autoChooser;

  /**
   * Creates a TunableDouble. It can be enabled and disabled (Use defaultValue)
   *
   * @param name
   * @param d
   * @param tunable
   */

  public TunableEnum(String name, V defaultValue, Class<V> klass, String tab) {
    this(name, defaultValue, klass, true, tab);
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, String tab, BooleanObjectConsumer<V> onChange) {
    this(name, defaultValue, klass, tab);
    addChangeListener(onChange);
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, String tab, Consumer<V> onChange) {
    this(name, defaultValue, klass, tab);
    addChangeListener(onChange);
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, boolean tunable, String tab) {
    this.defaultValue = defaultValue;

    if (tunable) {
      autoChooser = new SendableChooser<>();
      shuffleboardWidget = Shuffleboard.getTab(tab).add(name, autoChooser);
      autoChooser.setDefaultOption(defaultValue.name(), defaultValue);

      EnumSet<V> myEnums = EnumSet.allOf(klass);
      for (V value : myEnums) {
        autoChooser.addOption(value.name(), value);
      }
    } else {
      shuffleboardWidget = null;
      autoChooser = null;
    }
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, boolean tunable, String tab, Consumer<V> onChange) {
    this(name, defaultValue, klass, tunable, tab);
    addChangeListener(onChange);
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, boolean tunable, Consumer<V> onChange) {
    this(name, defaultValue, klass, tunable);
    addChangeListener(onChange);
  }

  public TunableEnum(String name, V defaultValue, Class<V> klass, boolean tunable) {
    this(name, defaultValue, klass, tunable, "Tunables");
  }

  public TunableEnum<V> setSpot(int x, int y) {
    if (shuffleboardWidget != null) {
      shuffleboardWidget.withPosition(x, y);
    }

    return this;
  }

  /**
   * @return Value as a double
   */
  public V getValue() {
    if (autoChooser != null) {
      return autoChooser.getSelected();
    }
    return defaultValue;
  }

  public void addChangeListener(BooleanObjectConsumer<V> onChange) {
    onChange.accept(true, getValue());
    CommandScheduler.getInstance().getDefaultButtonLoop().bind(
        new Runnable() {
          private V oldValue = getValue();

          @Override
          public void run() {
            V newValue = getValue();

            if (oldValue != newValue) {
              onChange.accept(false, newValue);
              oldValue = newValue;
            }
          }
        });
  }

  public void addChangeListener(Consumer<V> onChange) {
    addChangeListener((isInit, value) -> onChange.accept(value));
  }

}
