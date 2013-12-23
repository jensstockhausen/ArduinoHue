package de.famst.arduino.hue;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.ui.form.slider.AjaxSlider;
import com.googlecode.wicket.jquery.ui.form.slider.Slider.Range;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;

import de.famst.arduino.hue.com.ArduinoTCPServer;

public class SingleColorPage extends WebPage
{
  private static final long serialVersionUID = 1L;
  
  @SpringBean
  private ArduinoTCPServer arduinoTCPServer;
  
  // Models //
  private final Model<String> model;
 
  public SingleColorPage()
  {
    this.model = new Model<String>("#336699");
    this.init();
  }
  
  private void init()
  {
    final Form<Void> form = new Form<Void>("form");
    this.add(form);

    // FeedbackPanel //
    final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
    form.add(feedback.setOutputMarkupId(true));

    // Color panel //
    final EmptyPanel colorPanel = new EmptyPanel("color");
    colorPanel.add(this.newBackgroundAttributeModifier());
    form.add(colorPanel.setOutputMarkupId(true));

    // Color Slider(s) //
    form.add(new ColorPicker("picker", this.model) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onColorChanged(AjaxRequestTarget target)
      {
        // change the color of the color-panel //
        colorPanel.add(SingleColorPage.this.newBackgroundAttributeModifier());
        target.add(colorPanel);

        SingleColorPage.this.info(this);
        target.add(feedback);
      }
    });
  }

  private Behavior newBackgroundAttributeModifier()
  {
    return AttributeModifier.replace("style", "background-color: " + this.model.getObject());
  }

  private void info(Component component)
  {
    String hexColor = this.model.getObject();
    
    
    this.info("Setting Color: " + hexColor);
    
    RGBColor color = new RGBColor();
    
    //#acc948
    color.setR(Integer.parseInt(hexColor.substring(1, 3), 16));
    color.setG(Integer.parseInt(hexColor.substring(3, 5), 16));
    color.setB(Integer.parseInt(hexColor.substring(5, 7), 16));

    arduinoTCPServer.setColor(color);
  }

 
  
//  private void apply(Slider sliderR,Slider sliderG,Slider sliderB)
//  {
//    this.info("Set Color: (" 
//        + sliderR.getModelObject() + ","
//        + sliderG.getModelObject() + ","
//        + sliderB.getModelObject() + ")"
//    );
//    
//    RGBColor color = new RGBColor(sliderR.getModelObject(), sliderG.getModelObject(), sliderB.getModelObject());
//    
//    arduinoTCPServer.setColor(color);
//    
//  }
  
  
  
  abstract class ColorPicker extends Fragment
  {
    private static final long serialVersionUID = 1L;
    private static final int INDEX_R = 1; //#RRxxxx
    private static final int INDEX_G = 3; //#xxGGxx
    private static final int INDEX_B = 5; //#xxxxBB

    private final IModel<Integer> modelR;
    private final IModel<Integer> modelG;
    private final IModel<Integer> modelB;

    public ColorPicker(String id, IModel<String> model)
    {
      super(id, "color-picker", SingleColorPage.this, model);

      this.modelR = this.newColorModel(INDEX_R);
      this.modelG = this.newColorModel(INDEX_G);
      this.modelB = this.newColorModel(INDEX_B);

      this.init();
    }

    private void init()
    {
      this.add(this.newAjaxSlider("r", this.modelR)); // Slider: Red
      this.add(this.newAjaxSlider("g", this.modelG)); // Slider: Green
      this.add(this.newAjaxSlider("b", this.modelB)); // Slider: Blue
    }

    /**
     * Updates the model with the new color.
     * @param target
     * @param form
     */
    private void changeColor(AjaxRequestTarget target)
    {
      Integer r = this.modelR.getObject();
      Integer g = this.modelG.getObject();
      Integer b = this.modelB.getObject();

      this.setDefaultModelObject(String.format("#%02x%02x%02x", r, g, b));
      this.onColorChanged(target);
    }

    // Events //
    /**
     * Event which will be fired when the color has been changed.
     * @param target the {@link AjaxRequestTarget}
     */
    protected abstract void onColorChanged(AjaxRequestTarget target);

    // Factories //
    /**
     * Gets a new {@link AjaxSlider} for the specified color model
     * @param id the markup id
     * @param model the (R|G|B) color model
     * @return the {@link AjaxSlider}
     */
    private AjaxSlider newAjaxSlider(String id, IModel<Integer> model)
    {
      AjaxSlider slider = new AjaxSlider(id, model) {

        private static final long serialVersionUID = 1L;
        
        @Override
        public void onValueChanged(AjaxRequestTarget target, Form<?> form)
        {
          ColorPicker.this.changeColor(target);
        }

      };

      return slider.setRange(Range.MIN).setMax(255);
    }

    /**
     * Gets a new one-color-model based on the rdb-color-model<br/>
     * The code is not defensive (ie: no check on string length)
     */
    private IModel<Integer> newColorModel(int index)
    {
      String color = this.getDefaultModelObjectAsString().substring(index, index + 2);
      return new Model<Integer>(Integer.parseInt(color, 16));
    }
  }
  

}
