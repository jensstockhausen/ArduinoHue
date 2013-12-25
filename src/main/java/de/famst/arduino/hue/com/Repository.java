package de.famst.arduino.hue.com;

import java.util.List;

import de.famst.arduino.hue.RGBColor;

public interface Repository
{
  public void setColor(RGBColor color);
  
  public RGBColor getColor();
  
  public void setColors(List<RGBColor> colors);
  
  public List<RGBColor> getColors(); 
}
