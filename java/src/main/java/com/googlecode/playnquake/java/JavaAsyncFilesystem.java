package com.googlecode.playnquake.java;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import playn.core.PlayN;
import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;

public class JavaAsyncFilesystem implements AsyncFilesystem{

  File root;
  public JavaAsyncFilesystem(String rootPath) {
    this.root = new File(rootPath);
  }
  
  @Override
  public void getFile(String filename, Callback<ByteBuffer> callback) {
    File file = new File(root, filename);
    byte[] data = new byte[(int) file.length()];
    try {
      System.out.println("file: " + file + " size: " + file.length());
      
      new DataInputStream(new FileInputStream(file)).readFully(data);
      ByteBuffer buf = ByteBuffer.wrap(data);
      buf.order(ByteOrder.LITTLE_ENDIAN);
      callback.onSuccess(buf);
    }catch (IOException e) {
      callback.onFailure(e);
    }
  }

  @Override
  public void getDirectory(final String filename, final Callback<String[]> callback) {
    PlayN.invokeLater(new Runnable() {
      @Override
      public void run() {
        callback.onSuccess(new File(root, filename).list());
      }
    });
  }

  @Override
  public void saveFile(String filename, ByteBuffer data, int offset, int len,
      final Callback<Void> callback) {
    File file = new File(root, filename);
    
    byte[] array = new byte[len];
    int pos = data.position();
    data.position(offset);
    data.get(array);
    data.position(pos);
    
    
    try {
      file.getParentFile().mkdirs();
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(array);
      fos.close();
      PlayN.invokeLater(new Runnable() {
        @Override
        public void run() {
          callback.onSuccess(null);
        }
      });
    } catch (IOException e) {
      callback.onFailure(e);
    }
    
    
  }

}
