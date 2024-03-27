package frc.lib.can;

import edu.wpi.first.hal.CANStreamMessage;
import edu.wpi.first.hal.can.CANJNI;
import edu.wpi.first.hal.can.CANStreamOverflowException;
import frc.lib.eventLoops.EventLoops;

public class CanStream {
  private final int maxMessages = 1000;
  private int m_canStreamSession;
  private CANStreamMessage[] m_messageBuffer;

  public CanStream() {
    m_canStreamSession = CANJNI.openCANStreamSession(0, 0, maxMessages);
    m_messageBuffer = new CANStreamMessage[maxMessages];
    for (int i = 0; i < maxMessages; i++) {
      m_messageBuffer[i] = new CANStreamMessage();
    }
    EventLoops.everyLoop.bind(this::poll);
  }

  public void poll() {
    try {
      int numToRead = CANJNI.readCANStreamSession(m_canStreamSession, m_messageBuffer, maxMessages);
      for (int i = 0; i < numToRead; i++) {
        var message = m_messageBuffer[i];
        process(message);
      }
    } catch (CANStreamOverflowException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public void process(CANStreamMessage message) {
    System.out.print("Message ID: " + message.messageID);
    System.out.print(" Timestamp: " + message.timestamp);
    System.out.print(" Length: " + message.length);
    System.out.print(" Data: ");
    for (int i = 0; i < message.data.length; i++) {
      System.out.print(message.data [i]);
    } System.out.println(" ");
    }
}
