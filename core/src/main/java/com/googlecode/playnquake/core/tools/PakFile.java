/*
Copyright (C) 2010 Copyright 2010 Google Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;

import playn.core.util.Callback;

public class PakFile {
  static final int SIZE = 64;
  static final int NAME_SIZE = 56;
  private static final int MAX_FILES_IN_PACK = 4096;
  private static final int IDPAKHEADER = 
      (('K' << 24) + ('C' << 16) + ('A' << 8) + 'P');

  private ByteBuffer packhandle;
  private int numpackfiles;
  
  public PakFile(ByteBuffer packhandle) {
    this.packhandle = packhandle;
    
    int ident = packhandle.getInt();
    int dirofs = packhandle.getInt();
    int dirlen = packhandle.getInt();

    if (ident != IDPAKHEADER) {
       throw new RuntimeException("Data is not a packfile. ident: " + Integer.toHexString(ident));
    }
  
    numpackfiles = dirlen / SIZE;

    if (numpackfiles > MAX_FILES_IN_PACK) {
        throw new RuntimeException("This pakfile has " + numpackfiles + " files");
    }

    packhandle.position(dirofs);
  }
  

  public void unpack(final Tools tools, final Callback<Void> readyCallback) {
    if (numpackfiles-- == 0) {
      readyCallback.onSuccess(null);
      return;
    }
    
    byte[] tmpText = new byte[NAME_SIZE];
    packhandle.get(tmpText);

    String name = new String(tmpText).trim();
    tools.println("Unpacking " + name);

    int filepos = packhandle.getInt();
    int filelen = packhandle.getInt();

    tools.getFileSystem().saveFile(name, packhandle, filepos, filelen, new Callback<Void>() {
      @Override
      public void onSuccess(Void result) {
        unpack(tools, readyCallback);
      }

      @Override
      public void onFailure(Throwable cause) {
        readyCallback.onFailure(cause);
      }
   });
  }
}
