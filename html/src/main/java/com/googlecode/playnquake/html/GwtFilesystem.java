package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.core.Sound;
import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;

public class GwtFilesystem implements AsyncFilesystem{

  @Override
  public void getFile(String filename, Callback<ByteBuffer> callback) {
    throw new RuntimeException("NYI");
  }

  @Override
  public void saveFile(String filename, ByteBuffer data, int offet, int len,
      Callback<Void> callback) {
    throw new RuntimeException("NYI");
  }

  @Override
  public void processFiles(String dirName, Callback<Entry> processCallback,
      Callback<Void> readyCallback) {
    throw new RuntimeException("NYI");
  }

  @Override
  public Image getImage(String name) {
    throw new RuntimeException("NYI");
  }

  @Override
  public Sound getSound(String location) {
    // TODO Auto-generated method stub
    return null;
  }

}
