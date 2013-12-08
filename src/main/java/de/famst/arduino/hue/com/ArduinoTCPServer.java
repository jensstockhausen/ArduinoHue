package de.famst.arduino.hue.com;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ArduinoTCPServer
{
  private BlockingQueue<String> queue;
  private SocketThread thread = null;

  public ArduinoTCPServer()
  {
    queue = new ArrayBlockingQueue<String>(100);
    thread = new SocketThread(queue);

    try
    {
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");

    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void sendMessage(String message)
  {
    try
    {
      queue.put(message);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

}
