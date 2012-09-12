package com.googlecode.playnquake.java;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.java.JavaImage;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.Tools;

public class JavaTools implements Tools {

  JavaAsyncFilesystem fileSystem = new JavaAsyncFilesystem("data");
  
  @Override
  public AsyncFilesystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public ByteBuffer convertToPng(Image image) {
    JavaImage javaImage = (JavaImage) image;

    throw new RuntimeException("NYI");
  }

  @Override
  public void println(String text) {
    System.out.println(text);
  }



}
