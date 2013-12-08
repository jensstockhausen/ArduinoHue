package de.famst.arduino.hue.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/info")
public class RestInfo {

	@GET
	public String getInfo() 
	{
 		String result = "Hello REST\n";
		return result;
	}
	
}
