package com.googlecode.playnquake.core.tools;

import playn.core.util.Callback;

public class CountingCallback   implements Callback<Void> {
  Callback<Void> callback;
  int counter;
    
  public CountingCallback(Callback<Void> callback) {
    this.callback = callback;
  }
    
  public CountingCallback addAccess() {
    counter++;
    return this;
  }
    
  @Override
  public void onSuccess(Void result) {
    counter--;
    System.out.println("count: " + counter);
    if (counter == 0) {
      callback.onSuccess(result);
    }
  }

  @Override
  public void onFailure(Throwable cause) {
    callback.onFailure(cause);
  }
}
