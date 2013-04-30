package com.googlecode.playnquake.html;

//import com.google.gwt.core.client.GWT;
//import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.googlecode.playnquake.core.PlayNQuake;
import com.googlecode.playnquake.core.tools.Tools;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

public class PlayNQuakeHtml extends HtmlGame {

  @Override
  public void start() {
    
    
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    config.experimentalFullscreen = true;
    config.transparentCanvas = false;
    config.mode = HtmlPlatform.Mode.WEBGL;
    HtmlPlatform platform = HtmlPlatform.register(config);
    final Tools tools = new GwtTools();

    /*
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable t) {
        tools.println("" + t);
        for (StackTraceElement e : t.getStackTrace()) {
          tools.println("" + e);
        }
      } 
    });
   */
    
    PlayN.run(new PlayNQuake(tools));
  }

}
