package de.famst.arduino.hue;

import java.io.Serializable;

public class RGBColor implements Serializable
{
  private static final long serialVersionUID = 1L;

  private Integer r = 0;
  private Integer g = 0;
  private Integer b = 0;

  public RGBColor()
  {
  }

  public RGBColor(Integer r, Integer g, Integer b)
  {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public RGBColor(String hexValue)
  {
    r = Integer.parseInt(hexValue.substring(1, 1 + 2), 16);
    g = Integer.parseInt(hexValue.substring(3, 3 + 2), 16);
    b = Integer.parseInt(hexValue.substring(5, 5 + 2), 16);
  }

  public Integer getR()
  {
    return r;
  }

  public void setR(Integer r)
  {
    this.r = r;
  }

  public Integer getG()
  {
    return g;
  }

  public void setG(Integer g)
  {
    this.g = g;
  }

  public Integer getB()
  {
    return b;
  }

  public void setB(Integer b)
  {
    this.b = b;
  }

  @Override
  public String toString()
  {
    return "RGBColor [r=" + r + ", g=" + g + ", b=" + b + "]";
  }

  public String toHex()
  {
    return String.format("#%02x%02x%02x", r, g, b);
  }

}
