package frc.lib.tunables;

import java.util.HashMap;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class TunableTab {
    private ShuffleboardTab tab;
    private static HashMap<String, TunableTab> map = new HashMap<>();
    private static final int MAX_WIDTH = 10;
    private static final int MAX_HEIGHT = 5;
    private int xPos = 0;
    private int yPos = 0;

    private TunableTab(String name) {
        tab = Shuffleboard.getTab(name);
    }

    public static TunableTab getTab(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            var tab = new TunableTab(name);
            map.put(name, tab);
            return tab;
        }
    }
    // public TunableDouble addTunableDouble(){

    // }
}
