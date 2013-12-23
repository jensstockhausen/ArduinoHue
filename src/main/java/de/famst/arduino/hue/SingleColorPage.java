package de.famst.arduino.hue;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.form.slider.Slider;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;

import de.famst.arduino.hue.com.ArduinoTCPServer;

public class SingleColorPage extends WebPage
{
  private static final long serialVersionUID = 1L;
  
  @SpringBean
  private ArduinoTCPServer arduinoTCPServer;

  public SingleColorPage()
  {
    Form<Integer> form = new Form<Integer>("rgb_slider_form");
    this.add(form);

    // FeedbackPanel //
    form.add(new JQueryFeedbackPanel("feedback").setOutputMarkupId(true));

    // Sliders //
    TextField<Integer> inputR = new TextField<Integer>("inputR",
        new Model<Integer>(0), Integer.class);
    form.add(inputR);
    final Slider sliderR = new Slider("sliderR", inputR.getModel(), inputR); 
    
    sliderR.setRangeValidator(new RangeValidator<Integer>(0, 255));
    form.add(sliderR.setMin(0).setMax(255));

    TextField<Integer> inputG = new TextField<Integer>("inputG",
        new Model<Integer>(0), Integer.class);
    form.add(inputG);
    final Slider sliderG = new Slider("sliderG", inputG.getModel(), inputG); 
    
    sliderG.setRangeValidator(new RangeValidator<Integer>(0, 255));
    form.add(sliderG.setMin(0).setMax(255));
    
    
    TextField<Integer> inputB = new TextField<Integer>("inputB",
        new Model<Integer>(0), Integer.class);
    form.add(inputB);
    final Slider sliderB = new Slider("sliderB", inputB.getModel(), inputB); 
    
    sliderB.setRangeValidator(new RangeValidator<Integer>(0, 255));
    form.add(sliderB.setMin(0).setMax(255));
    

    form.add(new AjaxButton("button")
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form)
      {
        SingleColorPage.this.apply(sliderR, sliderG, sliderB);
        target.add(form);
      }

      @Override
      protected void onError(AjaxRequestTarget target, Form<?> form)
      {
        target.add(form.get("feedback"));
      }
    });
  }
  
  
  private void apply(Slider sliderR,Slider sliderG,Slider sliderB)
  {
    this.info("Set Color: (" 
        + sliderR.getModelObject() + ","
        + sliderG.getModelObject() + ","
        + sliderB.getModelObject() + ")"
    );
    
    RGBColor color = new RGBColor(sliderR.getModelObject(), sliderG.getModelObject(), sliderB.getModelObject());
    
    arduinoTCPServer.setColor(color);
    
  }
  

}
