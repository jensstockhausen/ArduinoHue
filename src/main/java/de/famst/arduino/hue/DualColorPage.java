package de.famst.arduino.hue;

import java.util.ArrayList;

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
import de.famst.arduino.hue.com.Repository;

public class DualColorPage extends WebPage
{
  private static final long serialVersionUID = 1L;

  @SpringBean
  private ArduinoTCPServer arduinoTCPServer;

  @SpringBean
  private Repository repos;

  // Models //
  private final Model<String> model_top;
  private final Model<String> model_bottom;

  public DualColorPage()
  {
    RGBColor color = repos.getColor();

    this.model_top = new Model<String>(color.toHex());
    this.model_bottom = new Model<String>(color.toHex());
    this.init();
  }

  private void init()
  {

    // FeedbackPanel //
    final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");

    
    
    // TOP
    {
      final Form<Void> form = new Form<Void>("form_top");
      this.add(form);

      // Color panel //
      final EmptyPanel colorPanel = new EmptyPanel("color_top");
      colorPanel.add(this.newBackgroundAttributeModifierTop());
      form.add(colorPanel.setOutputMarkupId(true));

      // Color Slider(s) //
      form.add(new ColorPicker("picker_top", this.model_top)
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected void onColorChanged(AjaxRequestTarget target)
        {
          // change the color of the color-panel //
          colorPanel.add(DualColorPage.this.newBackgroundAttributeModifierTop());
          target.add(colorPanel);
          target.add(feedback);

          DualColorPage.this.info(this);
        }
      });
    }

    // Bottom
    {
      final Form<Void> form = new Form<Void>("form_bottom");
      this.add(form);

      form.add(feedback.setOutputMarkupId(true));

      // Color panel //
      final EmptyPanel colorPanel = new EmptyPanel("color_bottom");
      colorPanel.add(this.newBackgroundAttributeModifierBottom());
      form.add(colorPanel.setOutputMarkupId(true));

      // Color Slider(s) //
      form.add(new ColorPicker("picker_bottom", this.model_bottom)
      {

        private static final long serialVersionUID = 1L;

        @Override
        protected void onColorChanged(AjaxRequestTarget target)
        {
          // change the color of the color-panel //
          colorPanel.add(DualColorPage.this
              .newBackgroundAttributeModifierBottom());
          target.add(colorPanel);
          target.add(feedback);

          DualColorPage.this.info(this);
        }
      });
    }

  }

  private Behavior newBackgroundAttributeModifierTop()
  {
    return AttributeModifier.replace("style", "background-color: "
        + this.model_top.getObject());
  }

  private Behavior newBackgroundAttributeModifierBottom()
  {
    return AttributeModifier.replace("style", "background-color: "
        + this.model_bottom.getObject());
  }

  private void info(Component component)
  {
    String hexColorTop = this.model_top.getObject();
    String hexColorBottom = this.model_bottom.getObject();

    this.info("Setting Color:  from" + hexColorBottom + " to " + hexColorTop);
    
    RGBColor top = new RGBColor(hexColorTop);
    RGBColor bot = new RGBColor(hexColorBottom);

    ArrayList<RGBColor> colors = new ArrayList<RGBColor>();
    
    for (int i=0;i<10;i++)
    {
      Integer r = (int)(i * (top.getR().doubleValue() - bot.getR().doubleValue())/9.0 + bot.getR().doubleValue());
      Integer g = (int)(i * (top.getG().doubleValue() - bot.getG().doubleValue())/9.0 + bot.getG().doubleValue());
      Integer b = (int)(i * (top.getB().doubleValue() - bot.getB().doubleValue())/9.0 + bot.getB().doubleValue());
      
      colors.add(new RGBColor(r,g,b));
    }
    
    arduinoTCPServer.setColors(colors);
  }

  abstract class ColorPicker extends Fragment
  {
    private static final long serialVersionUID = 1L;
    private static final int INDEX_R = 1; // #RRxxxx
    private static final int INDEX_G = 3; // #xxGGxx
    private static final int INDEX_B = 5; // #xxxxBB

    private final IModel<Integer> modelR;
    private final IModel<Integer> modelG;
    private final IModel<Integer> modelB;

    public ColorPicker(String id, IModel<String> model)
    {
      super(id, "color-picker", DualColorPage.this, model);

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
     * 
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
     * 
     * @param target
     *          the {@link AjaxRequestTarget}
     */
    protected abstract void onColorChanged(AjaxRequestTarget target);

    // Factories //
    /**
     * Gets a new {@link AjaxSlider} for the specified color model
     * 
     * @param id
     *          the markup id
     * @param model
     *          the (R|G|B) color model
     * @return the {@link AjaxSlider}
     */
    private AjaxSlider newAjaxSlider(String id, IModel<Integer> model)
    {
      AjaxSlider slider = new AjaxSlider(id, model)
      {

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
      String color = this.getDefaultModelObjectAsString().substring(index,
          index + 2);
      return new Model<Integer>(Integer.parseInt(color, 16));
    }
  }

}
