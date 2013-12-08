package de.famst.arduino.hue;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import de.famst.arduino.hue.com.ArduinoTCPServer;

public class HomePage extends WebPage
{
  @SpringBean
  private ArduinoTCPServer arduinoTCPServer;
  
  
  private static final long serialVersionUID = 1L;

  private class RGBInputForm extends Form
  {
    private Integer valueR;
    private Integer valueG;
    private Integer valueB;
    
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
      
      add(new TextField<Integer>("valueR", Integer.class).setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new TextField<Integer>("valueG", Integer.class).setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new TextField<Integer>("valueB", Integer.class).setRequired(true)
          .add(new RangeValidator<Integer>(0, 255)));

      add(new Button("applyButton"));
      
//      {
//        private static final long serialVersionUID = 1L;
//
//        @Override
//        public void onSubmit()
//        {
//          @SuppressWarnings("unused")
//          Integer red = getValueR();
//        }
//      }.setDefaultFormProcessing(false));

    }

    @Override
    protected void onSubmit()
    { 
      arduinoTCPServer.setColor(getValueR(), getValueG(), getValueB());
    }

  }

  // called by WicketApplication
  public HomePage()
  {
    super();

    add(new RGBInputForm("inputForm"));

  }

}
