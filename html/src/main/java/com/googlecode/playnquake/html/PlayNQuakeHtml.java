package com.googlecode.playnquake.html;

import com.googlecode.playnquake.core.PlayNQuake;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

public class PlayNQuakeHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform platform = HtmlPlatform.register();
    PlayN.run(new PlayNQuake(new GwtTools()));
  }

}
