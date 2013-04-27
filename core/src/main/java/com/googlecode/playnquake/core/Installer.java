package com.googlecode.playnquake.core;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.PlayN;
import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.AsyncFilesystem.FileTask;
import com.googlecode.playnquake.core.tools.CountingCallback;
import com.googlecode.playnquake.core.tools.ImageConverter;
import com.googlecode.playnquake.core.tools.PCXConverter;
import com.googlecode.playnquake.core.tools.PakFile;
import com.googlecode.playnquake.core.tools.TGAConverter;
import com.googlecode.playnquake.core.tools.Tools;
import com.googlecode.playnquake.core.tools.WALConverter;

public class Installer {
  
  Tools tools;
  AsyncFilesystem afs;
  Callback<Void> doneCallback;
  ImageConverter pcxConverter = new PCXConverter();
  ImageConverter tgaConverter = new TGAConverter();
  ImageConverter walConverter = new WALConverter();
  StringBuilder imageSizes;

  Installer(Tools tools, Callback<Void> doneCallback) {
    this.tools = tools;
    this.afs = tools.getFileSystem();
    this.doneCallback = doneCallback;
  }
  
  void error(String msg, Throwable cause) {
    tools.println(msg);
    doneCallback.onFailure(cause);
  }
  
  void run() {
    afs.getFile("/models/items/ammo/slugs/medium/tris.md2", 
      new Callback<ByteBuffer>() {
        @Override
        public void onSuccess(ByteBuffer result) {
          unpacked();
        }

        @Override
        public void onFailure(Throwable cause) {
          unpack();
        }
      }
    );
  }


  void unpack() {
    tools.println("Unpacking pak0.pak");
    afs.getFile("install/data/baseq2/pak0.pak", new Callback<ByteBuffer>() {

      @Override
      public void onSuccess(ByteBuffer result) {
        new PakFile(result).unpack(tools, 
            new Callback<Void>() {

              @Override
              public void onSuccess(Void result) {
                tools.println("pak0.pak successfully unpacked.");
                // forcing convert here instead of calling unpacked
                // because image sizes may stick in local storage
                convert();
              }

              @Override
              public void onFailure(Throwable cause) {
                error("Error unpacking pak0.pak", cause);
              }
            });
        }

      @Override
      public void onFailure(Throwable cause) {
        error("Error accessing pak0.pak", cause);
      }
    });
  }
  
  void unpacked() {
    if (PlayN.storage().getItem("imageSizes") != null) {
      converted();
    } else {
      convert();
    }
  }


  void convert() {
    tools.println("Converting Images");
    imageSizes = new StringBuilder();
    afs.processFiles("", processor, new Callback<Void>() {
      @Override
      public void onSuccess(Void result) {
        PlayN.storage().setItem("imageSizes", imageSizes.toString());
        converted();
      }
      @Override
      public void onFailure(Throwable cause) {
        error("Error processing files", cause);
      }
    });
  }


  void convert(final FileTask task, final ImageConverter converter) {
    afs.getFile(task.fullPath, 
        new Callback<ByteBuffer>() {
          @Override
          public void onSuccess(ByteBuffer result) {
            CanvasImage image = converter.convert(result);
            imageSizes.append(task.fullPath + "," + (int) image.width() + "," + (int) image.height() + "\n");
            ByteBuffer png = tools.convertToPng(image);
            
            String path = task.fullPath;
            
            afs.saveFile(path.toLowerCase() + ".png", png, 0, png.limit(), task.readyCallback);
          }
          @Override
          public void onFailure(Throwable cause) {
            task.readyCallback.onFailure(cause);
          }
    });
  }
  
  final Callback<FileTask> processor = new Callback<FileTask>() {
    @Override
    public void onSuccess(FileTask result) {
      ImageConverter converter = null;
      String lowerName = result.fullPath.toLowerCase();
      if (lowerName.endsWith(".pcx")) {
        converter = pcxConverter;
      } else if (lowerName.endsWith(".tga")) {
        converter = tgaConverter;
      } else if (lowerName.endsWith(".wal")) {
        converter = walConverter;
      } else {
        tools.println("Skipping: " + result.fullPath);
        result.readyCallback.onSuccess(null);
        return;
      }
      tools.println("Converting: " + result.fullPath);
      convert(result, converter);
    }

    @Override
    public void onFailure(Throwable cause) {
      tools.println("Error: " + cause);
      cause.printStackTrace();
    }
  };
  
  
  

  void converted() {
    tools.println("All files processed!");
    tools.prparationsDone();
    doneCallback.onSuccess(null);
  }
}
