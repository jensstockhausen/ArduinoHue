package de.famst.arduino.hue.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArduinoClient
{
  private static final Logger LOG = LoggerFactory
      .getLogger(ArduinoClient.class);

  private Socket socket;

  private BufferedReader inFromServer;

  public ArduinoClient()
  {
  }

  public void connect()
  {
    try
    {
      LOG.info("Try to connect");

      Boolean isConnected = false;

      while (!isConnected)
      {
        try
        {
          socket = new Socket("127.0.0.1", 8888);
          isConnected = true;
        }
        catch (ConnectException e)
        {
          LOG.info("Try reconnect");
          try
          {
            Thread.sleep(5 * 1000);
          }
          catch (InterruptedException ex)
          {
            ex.printStackTrace();
          }
        }
      }

      inFromServer = new BufferedReader(new InputStreamReader(
          socket.getInputStream()));

      LOG.info("Socket is connected");
      pollData();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void pollData()
  {
    while (socket.isConnected())
    {
      try
      {
        if (inFromServer.ready())
        {
          LOG.info(inFromServer.readLine());
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

}
