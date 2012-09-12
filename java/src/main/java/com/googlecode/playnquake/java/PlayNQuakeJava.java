package com.googlecode.playnquake.java;

import com.googlecode.playnquake.core.PlayNQuake;

import playn.core.PlayN;
import playn.java.JavaPlatform;


public class PlayNQuakeJava {
  
  public static void main(String[] args) {
    JavaPlatform platform = JavaPlatform.register();
    //platform.assets().setPathPrefix("com/googlecode/playnbulletdemo/resources");
    JavaTools tools = new JavaTools();
    
    PlayN.run(new PlayNQuake(tools));
  }
}
