package de.famst.arduino.hue.com;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;

@ManagedBean
public class ArduinoTCPServer
{
  private static final Logger LOG = LoggerFactory
      .getLogger(ArduinoTCPServer.class);

  private BlockingQueue<String> queue;
  private SocketThread thread = null;

  public ArduinoTCPServer()
  {
    queue = new ArrayBlockingQueue<String>(100);
    thread = new SocketThread(queue);

    try
    {
      // blick when connected
      queue.put("#SET|000000000\n");
      queue.put("#SET|255000000\n");
      queue.put("#SET|000255000\n");
      queue.put("#SET|000000255\n");
      queue.put("#SET|000000000\n");
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
      LOG.info("Enqueing:" + message);
      queue.put(message);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  public void setColor(Integer valueR, Integer valueG, Integer valueB)
  {
    String message = String.format("#SET|%03d%03d%03d\n", valueR, valueG,
        valueB);

    sendMessage(message);
  }

}
