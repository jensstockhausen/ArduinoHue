package de.famst.arduino.hue.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArduinoSimulation
{
  private static final Logger LOG = LoggerFactory.getLogger(ArduinoSimulation.class);

  public static void main(String[] args)
  {
    LOG.info("Arduino Simulation is running");
    
    ArduinoClient client = new ArduinoClient();
    
    client.connect();
  }

}
