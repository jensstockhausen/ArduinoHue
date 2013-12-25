package de.famst.arduino.hue.com;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.famst.arduino.hue.RGBColor;

@Service
public class ArduinoTCPServer
{
  private static final Logger LOG = LoggerFactory
      .getLogger(ArduinoTCPServer.class);
  

  private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
  private static SocketThread thread = new SocketThread(queue);

  public ArduinoTCPServer()
  {
    LOG.info("Init ArduinoTCPServer");
  }

  private void sendMessage(String message)
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

  public void setColor(RGBColor color)
  {
    String message = String.format("#SET|%03d%03d%03d\n", color.getR(),
        color.getG(), color.getB());

    sendMessage(message);
  }

  public void setColors(List<RGBColor> colors)
  {
    StringBuilder sb = new StringBuilder();

    sb.append("#SET|");
    for (int i = 0; i < 10; i++)
    {
      sb.append(String.format("%03d%03d%03d", colors.get(i).getR(), colors.get(i).getG(), colors.get(i).getB()));
      
    }
    sb.append("\n");
    
    sendMessage(sb.toString());
  }
  
  public void setFading(Boolean isFading, Integer delay)
  {
    StringBuilder sb = new StringBuilder();
   
    sb.append("#FADE|");
    
    if (isFading)
    {
      sb.append("ON");
    }
    else
    {
      sb.append("OF");
    }
    
    sb.append(String.format("%03d\n", delay));

    sendMessage(sb.toString());    
  }
 
}
