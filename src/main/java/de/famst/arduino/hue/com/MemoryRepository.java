package de.famst.arduino.hue.com;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.famst.arduino.hue.RGBColor;

@Service
public class MemoryRepository implements Repository
{
  private static final Logger LOG = LoggerFactory
      .getLogger(MemoryRepository.class);

  private ArrayList<RGBColor> colors;

  public MemoryRepository()
  {
    LOG.info("CTOR");

    colors = new ArrayList<RGBColor>();
    
    for (int i = 0; i < 10; i++)
    {
      colors.add(new RGBColor(0, 0, 0));
    }
  }

  @Override
  public void setColor(RGBColor color)
  {
    LOG.info("Storing " + color.toString());
    
    for (int i = 0; i < 10; i++)
    {
      colors.set(i, color);
    }
  }

  @Override
  public RGBColor getColor()
  {
    LOG.info("loading " + colors.get(0).toString());
    
    return colors.get(0);
  }

  @Override
  public void setColors(List<RGBColor> colors)
  {
    if (colors.size() == 10)
    {
      for (int i = 0; i < 10; i++)
      {
        colors.set(i, colors.get(i));
      }
    }
  }

  @Override
  public List<RGBColor> getColors()
  {
    return colors;
  }

}
