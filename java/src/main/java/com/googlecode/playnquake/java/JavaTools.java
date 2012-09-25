package com.googlecode.playnquake.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

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
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(javaImage.img, "PNG", baos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    byte[] data = baos.toByteArray();
    return ByteBuffer.wrap(data);
  }

  @Override
  public void println(String text) {
    System.out.println(text);
  }



}
