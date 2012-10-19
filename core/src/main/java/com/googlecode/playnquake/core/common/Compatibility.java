/*
 Copyright (C) 1997-2001 Id Software, Inc.

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
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package com.googlecode.playnquake.core.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.googlecode.playnquake.core.render.Model;

public class Compatibility {

	public static String newString(byte[] b) {
		return newString(b, 0, b.length);
	}
	
	public static String newString(byte[] b, int s, int l) {
	  StringBuffer sb = new StringBuffer(l);
      for (int i = 0; i < l; i++) {
          sb.append((char) b[s + i]);
      }
      return sb.toString();
	}
	
	public static String newString(byte[] b, String encoding) throws UnsupportedEncodingException {
		return newString(b);
	}
	
	public static String getOriginatingServerAddress() {
	  System.out.println("Compatibility: getOriginatingServerAddress(): 127.0.0.1 Dummy.");
	  return "127.0.0.1";
	}

	public static void printStackTrace(Throwable e) {
		e.printStackTrace();
	}

	public static void sleep(int i) {
	 // System.out.println("Compatibility.sleep() dummy " +i);
	}

	public static String bytesToString(byte[] data, int len) {
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			chars[i] = (char) data[i];
		}
		return new String(chars);
	}
	
	public static int stringToBytes(String s, byte[] data) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			data[i] = (byte) s.charAt(i);
		}
		return len;
	}
	
    public static String bytesToHex(byte[] data, int len) {
    	char[] hex = new char[len * 2];
    	for (int i = 0; i < len; i++) {
    		int di = data[i];
    		hex[i << 1] = Character.forDigit((di >> 4) & 15, 16);
    		hex[(i << 1) + 1] = Character.forDigit(di & 15, 16);
    	}
    	return new String(hex);
    }
    
    public static int hexToBytes(String hex, byte[] data) {
    	int len = hex.length();
    	for (int i = 0; i < len; i +=2) {
    	  data[i >> 1] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) |
    	     Character.digit(hex.charAt(i + 1), 16));
    	}
    	return len / 2;
    }
    

    public static void copyPartialBuffer(ByteBuffer original, int len, ByteBuffer copy) {
      int limit = original.limit();
      original.limit(original.position() + len);
      copy.put(original);
      copy.position(0);
      original.limit(limit);
    }

    public static ByteBuffer copyPartialBuffer(ByteBuffer original, int len) {
      ByteBuffer copy = ByteBuffer.allocate(len);
      copyPartialBuffer(original, len, copy);
      return copy;
    }

    public static ByteBuffer copyByteArrayToByteBuffe(byte[] data) {
      return copyByteArrayToByteBuffe(data, 0, data.length);
    }

    public static ByteBuffer copyByteArrayToByteBuffe(byte[] data, int start, int len) {
      ByteBuffer copy = ByteBuffer.allocate(len);
      copy.put(data, start, len);
      copy.position(0);
      return copy;
    }
}
