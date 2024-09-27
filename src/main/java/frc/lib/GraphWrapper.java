package frc.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Robot24.Constants;

public class GraphWrapper {
    private String name;
    private GenericEntry graph;
    private String tab;

    public GraphWrapper(String name, String tab) {
        this.name = name;
        if (Constants.isGraphsEnabled) {
            this.graph = Shuffleboard.getTab(tab).add(name,
                    0).withWidget(BuiltInWidgets.kGraph).getEntry();
        }
    }

    public void setDouble(double value) {
        if (graph != null)
            graph.setDouble(value);
    }
}
