package frc.lib;

import edu.wpi.first.wpilibj.event.EventLoop;

public class EventLoops {
    public static final IsolatedEventLoop oncePerSec = new IsolatedEventLoop();
    public static final IsolatedEventLoop oncePerMin = new IsolatedEventLoop();
}
