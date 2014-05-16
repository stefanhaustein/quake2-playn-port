package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Sound;
import playn.core.util.Callback;
import playn.html.HtmlAudio;
import playn.html.TypedArrayHelper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.CountingCallback;

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
  
  static JsonObject CREATE = Json.createObject();
  static {
    CREATE.put("create", true);
  }
  
  class FailureErrorCallback implements ErrorCallback {
    Callback<?> callback;
    String where;
    FailureErrorCallback(String where, final Callback<?> callback) {
      this.callback = callback;
      this.where = where;
    }
    @Override
    public boolean onErrorCallback(FileError error) {
      String text;
      switch(error.getCode()) {
      case FileError.ABORT_ERR:
        text = "ABORT_ERR"; break;
      case FileError.ENCODING_ERR:
        text = "ENCODING_ERR"; break;
      case FileError.INVALID_MODIFICATION_ERR:
        text = "INVALID_MODIFICATION_ERR"; break;
      case FileError.INVALID_STATE_ERR:
        text ="INVALID_STATE_ERR"; break;
      case FileError.NO_MODIFICATION_ALLOWED_ERR:
        text = "NO_MODIFICATION_ALLOWED_ERR"; break;
      case FileError.NOT_FOUND_ERR:
        text ="NOT_FOUND_ERR"; break;
      case FileError.NOT_READABLE_ERR:
        text ="NOT_READABLE_ERR"; break;
      case FileError.PATH_EXISTS_ERR:
        text ="PATH_EXISTS_ERR"; break;
      case FileError.QUOTA_EXCEEDED_ERR:
        text ="QUOTA_EXCEEDED_ERR"; break;
      case FileError.SECURITY_ERR:
        text ="SECURITY_ERR"; break;
      case FileError.SYNTAX_ERR:
        text ="SYNTAX_ERR"; break;
      case FileError.TYPE_MISMATCH_ERR:
        text = "TYPE_MISMATCH_ERR"; break;
      default:
        text = "Unrecognized error";
      }
      
      log("File Error '" + text +"'; code: " + error.getCode()+ " in " + where);
      callback.onFailure(new RuntimeException(error.toString()));
      return false;
    }
  };
  
  static native DOMFileSystem getFileSystem() /*-{
    return $wnd['quakeFileSystem'];
  }-*/;
  
  static native void log(String s) /*-{
   $wnd.console.log(s);
  }-*/;
  
  static native boolean fileSystemReady() /*-{
    return $wnd['quakeFileSystemReady'] == true;
  }-*/;
  
  @Override
  public void getFile(final String filename, final Callback<ByteBuffer> callback) {
    if (!fileSystemReady()) {
      new com.google.gwt.user.client.Timer() {
        @Override
        public void run() {
          getFile(filename, callback);
        }
     }.schedule(4000);
     return;
    }
    
    log("getFile:" + filename);
    getFileSystem().getRoot().getFile(filename, JavaScriptObject.createObject(), new EntryCallback() {
      @Override
      public boolean onEntryCallback(elemental.html.Entry entry) {
        log("onEntryCallback:" + filename);
        FileEntry fileEntry = (FileEntry) entry;
        log ("file url: " + fileEntry.toURL());
        fileEntry.file(new FileCallback() {
            @Override
            public boolean onFileCallback(File file) {
              log("onFileCallback:" + filename);
              final FileReader reader = getWindow().newFileReader();
              reader.setOnloadend(new EventListener() {  // "DONE" Really??? FileReader.DONE is an int....
                @Override
                public void handleEvent(Event evt) {
                  log("handleEvent:" + evt);
                  log("ready state: " +reader.getReadyState());
                  ArrayBuffer arrayBuf = (ArrayBuffer) reader.getResult();
                  log("buffer size:" + arrayBuf.byteLength());
                  ByteBuffer byteBuf = TypedArrayHelper.wrap(arrayBuf);
                  callback.onSuccess(byteBuf);
                }
              });
              reader.readAsArrayBuffer(file);
              return true;
            }
          }, new FailureErrorCallback("getFile(2)", callback));
        return true; // WTF does that mean?
      }
    }, new FailureErrorCallback("getFile(1)", callback));
  }

  static native Blob makeBlob(ArrayBufferView abv) /*-{
    return new Blob([abv]);
  }-*/;


  void createDirs(final DirectoryEntry rootDirEntry, String path, final Callback<DirectoryEntry> callback) {
    log("createDirs: '" + path + "' root: " + rootDirEntry.getFullPath());
    path = path.trim();
    int cut = path.indexOf('/');
    while(cut == 0) {
      path = path.substring(1).trim();
      cut = path.indexOf('/');
    }
    path = path.trim();
    if (path.length() == 0) {
      callback.onSuccess(rootDirEntry);
      return;
    } 
    
    final String dir = cut == -1 ? path : path.substring(0, cut).trim();
    final String remainder = cut == -1 ? "" : path.substring(cut).trim();
    log("creating directory " + dir);
    rootDirEntry.getDirectory(dir, CREATE, new EntryCallback() {
      @Override
      public boolean onEntryCallback(elemental.html.Entry entry) {
        log("calling createDirs recursively");
        createDirs((DirectoryEntry) entry, remainder, callback);
        return true;
      }
    }, new FailureErrorCallback("createDirs", callback));
  }


  @Override
  public void saveFile(final String filename, final ByteBuffer data, final int offset, final int len,
      final Callback<Void> callback) {

    final int cut = filename.lastIndexOf('/');
    log("calling createDirs from saveFile: " + filename);
    createDirs(getFileSystem().getRoot(), cut == -1 ? "" : filename.substring(0, cut), new Callback<DirectoryEntry>() {
      @Override
      public void onSuccess(DirectoryEntry dir) {
        String file = cut == -1 ? filename : filename.substring(cut + 1);
        dir.getFile(file, CREATE, new EntryCallback() {
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
            }, new FailureErrorCallback("saveFile(2)",callback));
            return true; // What does that mean?
          }
        }, new FailureErrorCallback("saveFile(1)", callback));
      }

      @Override
      public void onFailure(Throwable cause) {
        callback.onFailure(cause);
      }
    });
  }

  
  private void processFiles(final DirectoryEntry dir, final Callback<FileTask> processCallback,
      final Callback<Void> readyCallback) {
    log("processFiles(dir): " + dir.toURL());
    final DirectoryReader reader = dir.createReader();
    final CountingCallback countingCallback = new CountingCallback(readyCallback);
    
    reader.readEntries(new EntriesCallback() {
      @Override
      public boolean handleEvent(JsArray<JavaScriptObject> entries) {
        if (entries.length() == 0) {
          // This assumes that all process callbacks are sync, which they are not. This should really recurse here to avoid this issue.
          countingCallback.onSuccess(null);
        } else {
          for (int i = 0; i < entries.length(); i++) {
            elemental.html.Entry entry = (elemental.html.Entry) entries.get(i);
            if (entry.isDirectory()) {
              processFiles((DirectoryEntry) entry, processCallback, countingCallback.addAccess());
            } else {
              FileTask task = new FileTask();
              task.fullPath = entry.getFullPath();
              task.readyCallback = countingCallback.addAccess();
              log("Calling processCallback for " + task.fullPath);
              processCallback.onSuccess(task);
            }
          }
          reader.readEntries(this);
        }
        return true;
      }
    }, new FailureErrorCallback("processFiles(private)", readyCallback));
  }
  
  
  @Override
  public void processFiles(String dirName, final Callback<FileTask> processCallback,
      final Callback<Void> readyCallback) {
    log("processFiles(public): '" + dirName + "'");
    if (dirName.startsWith("/")) {
      dirName = dirName.substring(1);
    }
    if (dirName.equals("") || dirName.equals(".")) {
      processFiles(getFileSystem().getRoot(), processCallback, readyCallback);
    } else {
      getFileSystem().getRoot().getDirectory(dirName, JavaScriptObject.createObject(), new EntryCallback() {
        @Override
        public boolean onEntryCallback(elemental.html.Entry entry) {
          DirectoryEntry dir = (DirectoryEntry) entry;
          processFiles(dir, processCallback, readyCallback);
          return true;
        }
      }, new FailureErrorCallback("processFiles(public)", readyCallback));
    }
  }

  @Override
  public Image getImage(String name) {
    String url = getFileSystem().getRoot().toURL() + name;
    return PlayN.assets().getRemoteImage(url);
  }

  @Override
  public Sound getSound(String location) {
    String url = getFileSystem().getRoot().toURL() + location;
    return ((HtmlAudio) PlayN.audio()).createSound(url);
  }

}
