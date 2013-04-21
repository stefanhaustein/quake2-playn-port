package com.googlecode.playnquake.html;

import com.googlecode.playnquake.core.PlayNQuake;
import com.googlecode.playnquake.core.tools.Tools;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

public class PlayNQuakeHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform platform = HtmlPlatform.register();
    Tools tools = new GwtTools();
    tools.println("Starting game");
    PlayN.run(new PlayNQuake(tools));
  }

}
