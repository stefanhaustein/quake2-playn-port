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
import playn.core.Keyboard;
import playn.core.Mouse;
import playn.core.PlayN;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;

import com.googlecode.playnquake.core.client.Keys;
import com.googlecode.playnquake.core.sys.KBD;
import com.googlecode.playnquake.core.sys.Timer;


public class KBDImpl extends KBD {

  
  @Override
  public void Init() {
    PlayN.keyboard().setListener(new Keyboard.Listener() {
      
      @Override
      public void onKeyUp(Event event) {
        Do_Key_Event(translateKeyCode(event), false);
      }
      
      @Override
      public void onKeyTyped(TypedEvent event) {
       // System.out.println("onTypes " + event.typedChar());
      }
      
      @Override
      public void onKeyDown(Event event) {
        Do_Key_Event(translateKeyCode(event), true);
      }
    });
    
    
    PlayN.mouse().setListener(new Mouse.Listener() {
      
      @Override
      public void onMouseWheelScroll(WheelEvent event) {
        int v = (int) event.velocity();
        while (v < 0) {
          Do_Key_Event(Keys.K_MWHEELUP, true);
          Do_Key_Event(Keys.K_MWHEELUP, false);
          v++;
        } 
        while (v > 0) {
          Do_Key_Event(Keys.K_MWHEELDOWN, true);
          Do_Key_Event(Keys.K_MWHEELDOWN, false);
          v--;
        }
      }
      
      @Override
      public void onMouseUp(ButtonEvent event) {
        mouseButton(event, false);
      }
      
      @Override
      public void onMouseMove(MotionEvent event) {
        mx = (int) event.dx();
        my = (int) event.dy();
      }
      
      @Override
      public void onMouseDown(ButtonEvent event) {
        mouseButton(event, true);
      }
    });
  }

  private void mouseButton(ButtonEvent event, boolean down) {
    switch(event.button()) {
    case Mouse.BUTTON_LEFT:
      Do_Key_Event(Keys.K_MOUSE1, down);
      break;
    case Mouse.BUTTON_RIGHT:
      Do_Key_Event(Keys.K_MOUSE2, down);
      break;
    case Mouse.BUTTON_MIDDLE:
      Do_Key_Event(Keys.K_MOUSE3, down);
      break;
    }
  }
  
  private int translateKeyCode(Event event) {
    switch (event.key()) {
    case ALT: return Keys.K_ALT;
    case AMPERSAND: return '&';
    case ASTERISK: return '*';
    case AT: return '@';
    case BACKSPACE: return Keys.K_BACKSPACE;
    case BACKQUOTE: return '`';
    case BANG: return '!';
    case CIRCUMFLEX: return '^';
    case CONTROL: return Keys.K_CTRL;
    case COMMA: return ',';
    case COLON: return ':';
    case DELETE: return Keys.K_DEL;
    case DOLLAR: return '$';
    case DOWN: return Keys.K_DOWNARROW;
    case DOUBLE_QUOTE: return '"';
    case END: return Keys.K_END;
    case ENTER: return Keys.K_ENTER;
    case ESCAPE: return Keys.K_ESCAPE;
    case EQUALS: return '=';
    case GREATER: return '>';
    case HASH: return '#';
    case INSERT: return Keys.K_INS;
    case HOME: return Keys.K_HOME;
    case LEFT: return Keys.K_LEFTARROW;
    case LEFT_BRACE: return '{';
    case LEFT_BRACKET: return '[';
    case LEFT_PAREN: return '(';
    case MINUS: return '-';
    case MULTIPLY: return '*';
    case NP_ADD: return Keys.K_KP_PLUS;
    case NP_DECIMAL: return '.';
    case NP_DELETE: return Keys.K_KP_DEL;
    case NP_DIVIDE: return Keys.K_KP_SLASH;
    case NP_DOWN: return Keys.K_KP_DOWNARROW;
    case NP_LEFT: return Keys.K_KP_LEFTARROW;
    case NP_MULTIPLY: return '*';
    case NP_RIGHT: return Keys.K_KP_RIGHTARROW;
    case NP_SUBTRACT: return '-';
    case NP_UP: return Keys.K_KP_UPARROW;
    case NP5: return Keys.K_KP_5;
    case PAGE_DOWN: return Keys.K_PGDN;
    case PAGE_UP: return Keys.K_PGUP;
    case PERCENT: return '%';
    case PERIOD: return '.';
    case PLUS: return'+';
    case QUESTION_MARK: return '?';
    case QUOTE: return '\'';
    case RIGHT: return Keys.K_RIGHTARROW;
    case RIGHT_BRACE: return '}';
    case RIGHT_BRACKET: return ']';
    case RIGHT_PAREN: return ')';
    case SEMICOLON: return';';
    case SHIFT: return Keys.K_SHIFT;
    case SLASH: return '/';
    case SPACE: return Keys.K_SPACE;
    case TAB: return Keys.K_TAB;
    case TILDE: return '~';
    case UNDERSCORE: return '_';
    case UP: return Keys.K_UPARROW;
    case VERTICAL_BAR: return '|';
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
    if (o >= Key.NP0.ordinal() && o <= Key.NP9.ordinal()) {
      return o - Key.NP0.ordinal() + '0';
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
    com.googlecode.playnquake.core.client.Key.Event(
        key, down, Timer.Milliseconds());
  }

  @Override
  public void installGrabs() {
    PlayN.mouse().lock();
  }

  @Override
  public void uninstallGrabs() {
    PlayN.mouse().unlock();
  }

}
