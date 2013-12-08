package de.famst.arduino.hue.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/command")
public class ArduinoCommand 
{
	@GET
	public String getCommand()
	{
		return "#SET|000090180270000090180270000090180270123\n";
	}
	
	
}
