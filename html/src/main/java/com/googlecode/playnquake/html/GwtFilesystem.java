package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Sound;
import playn.core.util.Callback;
import playn.html.HtmlAssets;
import playn.html.TypedArrayHelper;
import sun.awt.EventListenerAggregate;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.googlecode.playnquake.core.tools.AsyncFilesystem;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Blob;
import elemental.html.DOMFileSystem;
import elemental.html.DirectoryEntry;
import elemental.html.DirectoryReader;
import elemental.html.EntriesCallback;
import elemental.html.EntryArray;
import elemental.html.EntryCallback;
import elemental.html.ErrorCallback;
import elemental.html.File;
import elemental.html.FileCallback;
import elemental.html.FileEntry;
import elemental.html.FileError;
import elemental.html.FileReader;
import elemental.html.FileWriter;
import elemental.html.FileWriterCallback;
import elemental.json.Json;
import elemental.json.JsonObject;

import static elemental.client.Browser.getWindow;

public class GwtFilesystem implements AsyncFilesystem{
  
  
  static native DOMFileSystem getFileSystem() /*-{
    return $wnd['quakeFileSystem'];
  }-*/;
  
  @Override
  public void getFile(final String filename, final Callback<ByteBuffer> callback) {
    getFileSystem().getRoot().getFile(filename, JavaScriptObject.createObject(), new EntryCallback() {
      @Override
      public boolean onEntryCallback(elemental.html.Entry entry) {
        FileEntry fileEntry = (FileEntry) entry;
        fileEntry.file(new FileCallback() {
            @Override
            public boolean onFileCallback(File file) {
              final FileReader reader = getWindow().newFileReader();
              reader.addEventListener("DONE", new EventListener() {  // "DONE" Really??? FileReader.DONE is an int....
                @Override
                public void handleEvent(Event evt) {
                  ArrayBuffer buf = (ArrayBuffer) reader.getResult();
                  callback.onSuccess(TypedArrayHelper.wrap(buf));
                }
              });
              reader.readAsArrayBuffer(file);
              return true;
            }
          }, new ErrorCallback() {
            @Override
            public boolean onErrorCallback(FileError error) {
              callback.onFailure(new RuntimeException(error.toString()));
              return false;
            }
          }
        );
        return true; // WTF does that mean?
      }
    });
  }

  static native Blob makeBlob(ArrayBufferView abv) /*-{
    return new Blob([abv]);
  }-*/;
  
  @Override
  public void saveFile(final String filename, final ByteBuffer data, final int offset, final int len,
      final Callback<Void> callback) {
    
    JsonObject o = Json.createObject();
    o.put("create", true);
    
    getFileSystem().getRoot().getFile(filename, o, new EntryCallback() {
      @Override
      public boolean onEntryCallback(elemental.html.Entry entry) {
        FileEntry fileEntry = (FileEntry) entry;
        fileEntry.createWriter(new FileWriterCallback() {
          @Override
          public boolean onFileWriterCallback(FileWriter fileWriter) {
            ArrayBufferView array = ((playn.html.HasArrayBufferView) data).getTypedArray();
            Blob blob = makeBlob(TypedArrays.createUint8Array(array.buffer(), array.byteOffset() + offset, len));
            fileWriter.write(blob);
            callback.onSuccess(null);
            return true;
          }
        }, new ErrorCallback() {
            @Override
            public boolean onErrorCallback(FileError error) {
              callback.onFailure(new RuntimeException(error.toString()));
              return false;
            }
          }
        );
        return true; // WTF does that mean?
      }
    });
  }

  @Override
  public void processFiles(String dirName, final Callback<Entry> processCallback,
      final Callback<Void> readyCallback) {
    getFileSystem().getRoot().getFile(dirName, JavaScriptObject.createObject(), new EntryCallback() {
      @Override
      public boolean onEntryCallback(elemental.html.Entry entry) {
        DirectoryEntry dir = (DirectoryEntry) entry;
        final DirectoryReader reader = dir.createReader();
        reader.readEntries(new EntriesCallback() {
          
          @Override
          public boolean onEntriesCallback(EntryArray entries) {
            if (entries.getLength() == 0) {
              // This assumes that all process callbacks are sync, which they are not. This should really recurse here to avoid this issue.
              readyCallback.onSuccess(null);
            } else {
              for (int i = 0; i < entries.getLength(); i++) {
                elemental.html.Entry item = entries.item(i);
                Entry entry = new Entry();
                entry.directory = item.isDirectory();
                entry.fullPath = item.getFullPath();
                entry.name = item.getName();
                processCallback.onSuccess(entry);
              }
              reader.readEntries(this);
            }
            return true;
          }
        }, new ErrorCallback() {
          @Override
          public boolean onErrorCallback(FileError error) {
            readyCallback.onFailure(new RuntimeException("" + error));
            return false;
          }
        });
        return true;
      }
    });
  }

  @Override
  public Image getImage(String name) {
    String url = getFileSystem().getRoot().toURL() + name;
    return ((HtmlAssets) PlayN.assets()).adaptImage(url);
  }

  @Override
  public Sound getSound(String location) {
    String url = getFileSystem().getRoot().toURL() + location;
    return ((HtmlAssets) PlayN.assets()).adaptSound(url);
  }

}
