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

import playn.core.Image;
import playn.core.PlayN;

import com.googlecode.gwtquake.shared.common.QuakeImage;

public abstract class ImageConverter {

  protected static class image_t {
    int width, height;
    byte[] pix;
  }

//  private static HashMap<String, Converter> converters = new HashMap<String, Converter>();
//
//  public static Converter get(String name) {
//    int idx = name.lastIndexOf('.');
//    if (idx != -1) {
//      return converters.get(name.substring(idx + 1).toLowerCase());
//    }
//    return null;
//  }


  public abstract Image convert(byte[] raw);

  static Image makeImage(image_t source) {
	Image image = PlayN.graphics().createImage(source.width, source.height);
	
	int[] rgba = new int[source.pix.length / 4];
	int ofs = 0;
	for (int i = 0; i < rgba.length; i++) {
	  rgba[i] = ((source.pix[ofs]&255) << 16) |
			    ((source.pix[ofs+1]&255) << 8) |
			    ((source.pix[ofs+2]&255));
	}
	
	image.setRgb(0, 0, source.width, source.height, rgba, 0, source.width);
    return image;
  }

  static Image makePalletizedImage(image_t source) {
    Image image = PlayN.graphics().createImage(source.width, source.height);

    int[] data = new int[source.width * source.height];
    int i = 0;
    for (int y = 0; y < source.height; ++y) {
      for (int x = 0; x < source.width; ++x) {
        int ofs = source.pix[y * source.width + x];
        if (ofs < 0) {
          ofs += 256;
        }

        data[i++] = (ofs == 255) ? 0 : QuakeImage.PALETTE_ARGB[ofs];
      }
    }

    image.setRgb(0, 0, source.width, source.height, data, 0, source.width);
    return image;
  }
}
