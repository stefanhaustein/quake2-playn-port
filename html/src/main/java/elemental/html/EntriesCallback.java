package elemental.html;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


/**
  * 
  */
public interface EntriesCallback {
  //void onEntriesCallback(EntryArray);
  boolean handleEvent(JsArray<JavaScriptObject> entries);
//  
//  static class Foo {
//	  native void bar() /*-{ @adslklsk::sjakska; }-*/;
//  }
}
