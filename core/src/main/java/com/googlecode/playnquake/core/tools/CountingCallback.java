package com.googlecode.playnquake.core.tools;

import playn.core.util.Callback;

/**
 * A callback that wraps a void callback and only calls it when
 * the number of onSuccess calls to this matches the number of 
 * addAccess() calls plus one (one access is implied by construction).
 */
public class CountingCallback   implements Callback<Void> {
  Callback<Void> callback;
  int counter = 1;

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
    // Make extra sure we don't call onSuccess, too.
    counter = Integer.MAX_VALUE;
  }
}
