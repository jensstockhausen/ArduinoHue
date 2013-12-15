package de.famst.arduino.hue;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import de.famst.arduino.hue.com.ArduinoTCPServer;

public class HomePage extends WebPage
{
  @SpringBean
  private ArduinoTCPServer arduinoTCPServer;

  private static final long serialVersionUID = 1L;

  private String color00 = "000 000 000";

  private class RGBInputForm extends Form
  {
    private Integer valueR;
    private Integer valueG;
    private Integer valueB;
    
    private Boolean isFading; 
    private Integer valueFade;

    public Integer getValueR()
    {
      return valueR;
    }

    public void setValueR(Integer valueR)
    {
      this.valueR = valueR;
    }

    public Integer getValueG()
    {
      return valueG;
    }

    public void setValueG(Integer valueG)
    {
      this.valueG = valueG;
    }

    public Integer getValueB()
    {
      return valueB;
    }

    public void setValueB(Integer valueB)
    {
      this.valueB = valueB;
    }

    private static final long serialVersionUID = 1L;

    public RGBInputForm(String id)
    {
      super(id);

      valueR = 0;
      valueG = 0;
      valueB = 0;

      setDefaultModel(new CompoundPropertyModel(this));

      add(new NumberTextField<Integer>("valueR").setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new NumberTextField<Integer>("valueG").setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new NumberTextField<Integer>("valueB").setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new Button("applyButton"){

        private static final long serialVersionUID = 1L;
        
        @Override
        public void onSubmit()
        {
          setColor(new RGBColor(getValueR(), getValueG(), getValueB()));
        }        
        
      });
      
      add(new NumberTextField<Integer>("valueFade").setRequired(true)
          .add(new RangeValidator<Integer>(0, 999)));
      
      add(new CheckBox("isFading"));
      
      add(new Button("fadeButton")
      {
        private static final long serialVersionUID = 1L;

        @Override
        public void onSubmit()
        {
          arduinoTCPServer.setFading(isFading, valueFade);
        }
      });

    }


  }

  private class ButtonForm extends Form
  {

    private static final long serialVersionUID = 1L;

    private class ColorButton extends Button
    {
      private static final long serialVersionUID = 1L;

      private RGBColor color = null;

      public ColorButton(String id, RGBColor color)
      {
        super(id);
        this.color = color;
      }

      @Override
      public void onSubmit()
      {
        setColor(color);
      }

    }

    public ButtonForm(String id)
    {
      super(id);

      add(new ColorButton("redButton", new RGBColor(255, 0, 0)));
      add(new ColorButton("greButton", new RGBColor(0, 255, 0)));
      add(new ColorButton("bluButton", new RGBColor(0, 0, 255)));
      add(new ColorButton("offButton", new RGBColor(0, 0, 0)));
    }

  }

  // called by WicketApplication
  public HomePage()
  {
    super();

    setDefaultModel(new CompoundPropertyModel(this));

    Label label = new Label("color00");

    add(label);

    // add(.add(new AttributeModifier("style",
    // "background-color:blue; font-weight:bold")));

    add(new RGBInputForm("inputForm"));
    add(new ButtonForm("buttonForm"));
  }

  public void setColor(RGBColor color)
  {
    arduinoTCPServer.setColor(color);
    color00 = String.format("%03d %03d %03d", color.getR(), color.getG(),
        color.getB());
  }

}
