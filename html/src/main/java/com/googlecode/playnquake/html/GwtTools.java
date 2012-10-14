package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.Image;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.Tools;

public class GwtTools implements Tools {

  @Override
  public AsyncFilesystem getFileSystem() {
    throw new RuntimeException("NYI");
  }

  @Override
  public ByteBuffer convertToPng(Image image) {
    throw new RuntimeException("NYI");
  }

  @Override
  public native void println(String text) /*-{
    $wnd.console.log(text);
  }-*/;

  @Override
  public float intBitsToFloat(int i) {
    throw new RuntimeException("NYI");
  }

  @Override
  public int floatToIntBits(float f) {
    throw new RuntimeException("NYI");
  }

}
