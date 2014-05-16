package com.googlecode.playnquake.html;

import java.nio.ByteBuffer;

import playn.core.CanvasImage;
import playn.html.HtmlImage;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Float64Array;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.Tools;
import com.ibm.icu.text.DisplayContext;

import elemental.html.CanvasElement;

public class GwtTools implements Tools {
  static Int8Array wba = TypedArrays.createInt8Array(8);
  static Int32Array wia = TypedArrays.createInt32Array(wba.buffer(), 0, 2);
  static Float32Array wfa = TypedArrays.createFloat32Array(wba.buffer(), 0, 2);
  static Float64Array wda = TypedArrays.createFloat64Array(wba.buffer(), 0, 1);
  
  GwtFilesystem filesystem = new GwtFilesystem();
  
  @Override
  public AsyncFilesystem getFileSystem() {
    return filesystem;
  }

  private static native String atob(String encoded) /*-{
    return $wnd.atob(encoded);
  }-*/;
  
  @Override
  public ByteBuffer convertToPng(CanvasImage image) {
	
    CanvasElement canvasElement = (CanvasElement) ((HtmlImage) image).imageElement();
    String data = canvasElement.toDataURL(null);
    int cut = data.indexOf(',');
    String decoded = atob(data.substring(cut + 1));
    ByteBuffer buf = ByteBuffer.allocate(decoded.length());
    for (int i = 0; i < decoded.length(); i++) {
      buf.put(i, (byte) decoded.charAt(i));
    }
    return buf;
  }

  @Override
  public native void println(String text) /*-{
    $wnd.console.log(text);
    var log = $doc.getElementById("log");
    if (log) {
      log.textContent += text +"\n"
      $doc.getElementById("log-bottom").scrollIntoView();
    }
  }-*/;

  @Override
  public final int floatToIntBits(float f) {
      wfa.set(0, f);
      return wia.get(0);
  }

  @Override
  public final float intBitsToFloat(int i) {
      wia.set(0, i);
      return wfa.get(0);
  }

  @Override
  public void exit(int i) {
    println("exit(" + i + ") requested.");
  }

  @Override
  public void prparationsDone() {
    Element log = Document.get().getElementById("splash");
    Document.get().getBody().removeChild(log);
    Document.get().getElementById("playn-root").getStyle().setVisibility(Visibility.VISIBLE);
  }

}
