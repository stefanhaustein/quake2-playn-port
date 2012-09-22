package com.googlecode.playnquake.core;

import java.nio.ByteBuffer;
import java.sql.Savepoint;

import playn.core.Image;
import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.AsyncFilesystem.Entry;
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
		}});
    }
  
  void unpack() {
    tools.println("Unpacking pak0.pak");
    afs.getFile("Install/Data/baseq2/pak0.pak", new Callback<ByteBuffer>() {

      @Override
      public void onSuccess(ByteBuffer result) {
        new PakFile(result).unpack(tools, 
            new Callback<Void>() {

              @Override
              public void onSuccess(Void result) {
                tools.println("pak0.pak successfully unpacked.");
                unpacked();
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

  
  final Callback<Void> readyCallback = new Callback<Void>() {
    @Override
    public void onSuccess(Void result) {
      processed();
    }
    @Override
    public void onFailure(Throwable cause) {
      error("Error processing files", cause);
    }
  };
  CountingCallback countingCallback = new CountingCallback(readyCallback);

  void convert(final Entry file, final ImageConverter converter, final Callback<Void> callback) {
	 afs.getFile(file.fullPath, 
	   new Callback<ByteBuffer>() {
		@Override
		public void onSuccess(ByteBuffer result) {
	      byte[] data = new byte[result.limit()];
          result.get(data);
          Image image = converter.convert(data);
          ByteBuffer png = tools.convertToPng(image);
          afs.saveFile(file.fullPath + ".png", png, 0, png.limit(), callback);
		}
		@Override
		public void onFailure(Throwable cause) {
			callback.onFailure(cause);
		}
	 });
  }
  
  final Callback<Entry> processor = new Callback<Entry>() {
    @Override
    public void onSuccess(Entry result) {
      tools.println("Processing: " + result.fullPath);
      if (result.directory) {
        afs.processFiles(result.fullPath, processor, countingCallback.addAccess());
      } else {
    	ImageConverter converter = null;
    	if (result.name.endsWith(".pcx")) {
    		converter = pcxConverter;
    	} else if (result.name.endsWith(".tga")) {
    		converter = tgaConverter;
    	} else if (result.name.endsWith(".wal")) {
    		converter = walConverter;
    	}
    	if (converter != null) {
            tools.println("Converting: " + result.fullPath);
    		convert(result, converter, countingCallback.addAccess());
    	}
      }
    }
    @Override
    public void onFailure(Throwable cause) {
      tools.println("Error: " + cause);
    }
  };
  
  
  void unpacked() {
    afs.processFiles("", processor, countingCallback.addAccess());
  }

  void processed() {
    tools.println("All files processed!");
  }
}
