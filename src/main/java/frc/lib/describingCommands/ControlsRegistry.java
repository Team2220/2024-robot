package frc.lib.describingCommands;

import java.util.ArrayList;
import java.util.HashMap;

public class ControlsRegistry {
    private static HashMap<String, HashMap <String, ArrayList<String>>> map=new HashMap<>();
    public static void registerControl(String triggerName, String when, String whatItDoes){
        map.putIfAbsent(triggerName, new HashMap<>());
        var inter = map.get (triggerName);

        inter.putIfAbsent(when, new ArrayList<>());
        var interinter = inter.get(when);
        interinter.add (whatItDoes);
    }
}
