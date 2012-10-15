package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.core.Sound;
import playn.core.util.Callback;
import playn.html.TypedArrayHelper;

import com.google.gwt.typedarrays.client.ArrayBuffer;
import com.googlecode.playnquake.core.tools.AsyncFilesystem;

public class GwtFilesystem implements AsyncFilesystem{
  
  
  @Override
  public void getFile(final String filename, final Callback<ByteBuffer> callback) {
    getFileImpl(filename, new Callback<ArrayBuffer>() {
      @Override
      public void onSuccess(ArrayBuffer result) {
        callback.onSuccess(TypedArrayHelper.wrap(result));
      }

      @Override
      public void onFailure(Throwable cause) {
        callback.onFailure(new RuntimeException());
      }
      
    });
   
  }

  public native void getFileImpl(String filename, Callback<ArrayBuffer> callback) /*-{
    
  }-*/;
  
  
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
    throw new RuntimeException("NYI");
  }

}
