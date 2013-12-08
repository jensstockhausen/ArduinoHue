package de.famst.arduino.hue;

public class RGBColor
{

  private Integer r = 0;
  private Integer g = 0;
  private Integer b = 0;
  
  public RGBColor()
  {
  }
  
  public RGBColor(Integer r, Integer g, Integer b)
  {
    super();
    this.r = r;
    this.g = g;
    this.b = b;
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
  
}
