/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.playnquake.core;

import playn.core.Key;
import playn.core.Keyboard.Listener;
import playn.core.PlayN;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;

import com.googlecode.gwtquake.shared.client.Keys;
import com.googlecode.gwtquake.shared.sys.KBD;
import com.googlecode.gwtquake.shared.sys.Timer;


public class KBDImpl extends KBD {

  
  @Override
  public void Init() {
    PlayN.keyboard().setListener(new Listener() {
      
      @Override
      public void onKeyUp(Event event) {
        Do_Key_Event(translateKeyCode(event), false);
      }
      
      @Override
      public void onKeyTyped(TypedEvent event) {
        System.out.println("onTypes " + event.typedChar());
        
      }
      
      @Override
      public void onKeyDown(Event event) {
        Do_Key_Event(translateKeyCode(event), true);
      }
    });
  }

  private int translateKeyCode(Event event) {
    switch (event.key()) {
    case ALT: return Keys.K_ALT;
    case BACKSPACE: return Keys.K_BACKSPACE;
    case CONTROL: return Keys.K_CTRL;
    case DELETE: return Keys.K_DEL;
    case DOWN: return Keys.K_DOWNARROW;
    case END: return Keys.K_END;
    case ENTER: return Keys.K_ENTER;
    case ESCAPE: return Keys.K_ESCAPE;
    case INSERT: return Keys.K_INS;
    case HOME: return Keys.K_HOME;
    case LEFT: return Keys.K_LEFTARROW;
    case PAGE_DOWN: return Keys.K_PGDN;
    case PAGE_UP: return Keys.K_PGUP;
    case RIGHT: return Keys.K_RIGHTARROW;
    case SHIFT: return Keys.K_SHIFT;
    case SPACE: return Keys.K_SPACE;
    case TAB: return Keys.K_TAB;
    case UP: return Keys.K_UPARROW;
    }
    int o = event.key().ordinal();
    if (o >= Key.F1.ordinal() && o <= Key.F12.ordinal()) {
      return o - Key.F1.ordinal() + Keys.K_F1;
    }
    if (o >= Key.A.ordinal() && o <= Key.Z.ordinal()) {
      return o - Key.A.ordinal() + 'a';
    }
    if (o >= Key.K0.ordinal() && o <= Key.K9.ordinal()) {
      return o - Key.K0.ordinal() + '0';
    }
    return 0;
  }
  
  @Override
  public void Update() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void Close() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void Do_Key_Event(int key, boolean down) {
    com.googlecode.gwtquake.shared.client.Key.Event(
        key, down, Timer.Milliseconds());
  }

  @Override
  public void installGrabs() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void uninstallGrabs() {
    // TODO Auto-generated method stub
    
  }

}
