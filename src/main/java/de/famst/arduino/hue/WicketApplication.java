
package de.famst.arduino.hue;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketApplication extends WebApplication
{
  private static final Logger LOG = LoggerFactory.getLogger(WicketApplication.class);
   
  @Override
  public void init()
  {
    LOG.info("Init");
    
    super.init();
    
    getDebugSettings().setDevelopmentUtilitiesEnabled(true);
    
    getRequestLoggerSettings().setRequestLoggerEnabled(true);
    
    getMarkupSettings().setCompressWhitespace(false);
    getMarkupSettings().setStripWicketTags(true);
    
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    
  }

  @Override
  public Class<? extends Page> getHomePage()
  {
    return HomePage.class;
  }
 
}
  
