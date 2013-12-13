package de.famst.arduino.hue.com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketThread implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(SocketThread.class);

  private Integer port;
  private ServerSocket server;
  private Thread thread;

  private Socket connectionSocket = null;
  private BufferedReader inFromClient = null;
  private DataOutputStream outToClient = null;

  private Boolean keepRunning;

  private final BlockingQueue<String> queue;

  public SocketThread(BlockingQueue<String> queue)
  {
    LOG.info("Init SocketThread for Arduino");

    port = 8888;
    this.queue = queue;

    try
    {
      server = new ServerSocket(port);

      LOG.info("Server opened at " + server.getInetAddress() + ":"
          + server.getLocalPort());

      keepRunning = true;

      try
      {
        StringBuilder sb = new StringBuilder();

        sb.append("#SET|");
        for (int i = 0; i < 10*3; i++)
        {
          sb.append("000");
        }
        sb.append("\n");

        String empty = sb.toString();

        queue.put(empty);

        for (int i = 0; i < 10; i++)
        {
          sb.replace(8 + i * 9, 8 + i * 9 + 3, "255");
          queue.put(sb.toString());
        }

        queue.put(empty);

      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }

      thread = new Thread(this, "SocketThread");
      thread.start();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void run()
  {
    while (keepRunning)
    {
      LOG.info("Starting Thread");

      connectionSocket = waitForConnection();
      setupConnection(connectionSocket);

      LOG.info("Starting Communication");

      Boolean doReconnect = false;

      while ((keepRunning) && (connectionSocket.isConnected())
          && (!doReconnect))
      {
        try
        {

          if (inFromClient.ready())
          {
            LOG.info("Start reading from client");
            String clientSentence = inFromClient.readLine();
            LOG.info("Received: " + clientSentence);
          }

          if (queue.size() > 0)
          {
            String message = queue.take();
            outToClient.writeBytes(message);
            LOG.info("Written Message:" + message);
          }

        }
        catch (IOException e)
        {
          e.printStackTrace();
          doReconnect = true;
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }

      }

    }
  }

  private void setupConnection(Socket socket)
  {
    try
    {
      LOG.info("Setup Input");
      inFromClient = new BufferedReader(new InputStreamReader(
          socket.getInputStream()));

      LOG.info("Setup Output");
      outToClient = new DataOutputStream(socket.getOutputStream());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private Socket waitForConnection()
  {
    Socket client = null;

    try
    {
      LOG.info("Waiting for client to connect");

      client = server.accept();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    LOG.info("Client connected: " + client.getInetAddress() + ":"
        + client.getLocalPort());

    return client;
  }

}
