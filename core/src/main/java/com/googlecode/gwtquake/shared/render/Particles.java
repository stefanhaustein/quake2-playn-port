package com.googlecode.gwtquake.shared.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import playn.gl11emulation.GL11;
import playn.gl11emulation.MeshBuilder;

import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.QuakeImage;
import com.googlecode.gwtquake.shared.util.Lib;
import com.googlecode.gwtquake.shared.util.Math3D;

public class Particles {

  static final byte[][] dottexture = {
    {0,0,0,0,0,0,0,0},
    {0,0,1,1,0,0,0,0},
    {0,1,1,1,1,0,0,0},
    {0,1,1,1,1,0,0,0},
    {0,0,1,1,0,0,0,0},
    {0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0},
  };

  private static ByteBuffer colorByteArray = Lib.newByteBuffer(
      Constants.MAX_PARTICLES * Lib.SIZEOF_INT, ByteOrder.LITTLE_ENDIAN);
  public static FloatBuffer vertexArray = Lib.newFloatBuffer(Constants.MAX_PARTICLES * 3);
  public static int[] colorTable = new int[256];
  public static IntBuffer colorArray = colorByteArray.asIntBuffer();

  public static void setColorPalette(int[] palette) {
    for (int i = 0; i < 256; i++) {
      colorTable[i] = palette[i] & 0x00FFFFFF;
    }
  }

  public static ByteBuffer getColorAsByteBuffer() {
    return colorByteArray;
  }

  /**
   * GL_DrawParticles
   */
  static void GL_DrawParticles(int num_particles) {
    float origin_x, origin_y, origin_z;

    Math3D.VectorScale(GlState.vup, 1.5f, GlState.up);
    Math3D.VectorScale(GlState.vright, 1.5f, GlState.right);

    Images.GL_Bind(GlState.r_particletexture.texnum);
    GlState.gl.glDepthMask(false); // no z buffering
    GlState.gl.glEnable(GL11.GL_BLEND);
    Images.GL_TexEnv(GL11.GL_MODULATE);

    GlState.meshBuilder.begin(MeshBuilder.Mode.TRIANGLES, MeshBuilder.OPTION_COLOR | MeshBuilder.OPTION_TEXTURE);

    FloatBuffer sourceVertices = Particles.vertexArray;
    IntBuffer sourceColors = Particles.colorArray;
    float scale;
    int color;
    for (int j = 0, i = 0; i < num_particles; i++) {
      origin_x = sourceVertices.get(j++);
      origin_y = sourceVertices.get(j++);
      origin_z = sourceVertices.get(j++);

      // hack a scale up to keep particles from disapearing
      scale = (origin_x - GlState.r_origin[0]) * GlState.vpn[0]
          + (origin_y - GlState.r_origin[1]) * GlState.vpn[1]
          + (origin_z - GlState.r_origin[2]) * GlState.vpn[2];

      scale = (scale < 20) ? 1 : 1 + scale * 0.004f;

      color = sourceColors.get(i);

      GlState.meshBuilder.color4ub((byte) ((color) & 0xFF),
          (byte) ((color >> 8) & 0xFF), (byte) ((color >> 16) & 0xFF),
          (byte) ((color >>> 24)));
      // first vertex
      GlState.meshBuilder.texCoord2f(0.0625f, 0.0625f);
      GlState.meshBuilder.vertex3f(origin_x, origin_y, origin_z);
      // second vertex
      GlState.meshBuilder.texCoord2f(1.0625f, 0.0625f);
      GlState.meshBuilder.vertex3f(origin_x + GlState.up[0] * scale, origin_y
          + GlState.up[1] * scale, origin_z + GlState.up[2] * scale);
      // third vertex
      GlState.meshBuilder.texCoord2f(0.0625f, 1.0625f);
      GlState.meshBuilder.vertex3f(origin_x + GlState.right[0] * scale, origin_y
          + GlState.right[1] * scale, origin_z + GlState.right[2] * scale);
    }
    GlState.meshBuilder.end(GlState.gl);

    GlState.gl.glDisable(GL11.GL_BLEND);
    GlState.gl.glColor4f(1, 1, 1, 1);
    GlState.gl.glDepthMask(true); // back to normal Z buffering
    Images.GL_TexEnv(GL11.GL_REPLACE);
  }

  /**
   * R_DrawParticles
   */
  static void draw() {
    if (GlConfig.gl_ext_pointparameters.value != 0.0f
        && GlState.qglPointParameterfEXT) {

      // gl.glEnableClientState(GLAdapter.GL_VERTEX_ARRAY);
      GlState.gl.glVertexPointer(3, GL11.GL_FLOAT, 0, Particles.vertexArray);
      GlState.gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
      GlState.gl.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, Particles.getColorAsByteBuffer());

      GlState.gl.glDepthMask(false);
      GlState.gl.glEnable(GL11.GL_BLEND);
      GlState.gl.glDisable(GL11.GL_TEXTURE_2D);
      GlState.gl.glPointSize(GlConfig.gl_particle_size.value);

      GlState.gl.glDrawArrays(GL11.GL_POINTS, 0,
          GlState.r_newrefdef.num_particles);

      GlState.gl.glDisableClientState(GL11.GL_COLOR_ARRAY);
      // gl.glDisableClientState(GLAdapter.GL_VERTEX_ARRAY);

      GlState.gl.glDisable(GL11.GL_BLEND);
      GlState.gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlState.gl.glDepthMask(true);
      GlState.gl.glEnable(GL11.GL_TEXTURE_2D);

    } else {
      GL_DrawParticles(GlState.r_newrefdef.num_particles);
      throw new RuntimeException("Old particle stuff");
    }
  }

  static void R_InitParticleTexture() {
    int x, y;
    byte[] data = new byte[8 * 8 * 4];

    //
    // particle texture
    //
    for (x = 0; x < 8; x++) {
      for (y = 0; y < 8; y++) {
        data[y * 32 + x * 4 + 0] = (byte) 255;
        data[y * 32 + x * 4 + 1] = (byte) 255;
        data[y * 32 + x * 4 + 2] = (byte) 255;
        data[y * 32 + x * 4 + 3] = (byte) (Particles.dottexture[x][y] * 255);

      }
    }
    GlState.r_particletexture = Images.GL_LoadPic("***particle***", data, 8, 8,
        QuakeImage.it_sprite, 32);

    //
    // also use this for bad textures, but without alpha
    //
    for (x = 0; x < 8; x++) {
      for (y = 0; y < 8; y++) {
        data[y * 32 + x * 4 + 0] = (byte) (Particles.dottexture[x & 3][y & 3] * 255);
        data[y * 32 + x * 4 + 1] = 0; // dottexture[x&3][y&3]*255;
        data[y * 32 + x * 4 + 2] = 0; // dottexture[x&3][y&3]*255;
        data[y * 32 + x * 4 + 3] = (byte) 255;
      }
    }
    GlState.r_notexture = Images.GL_LoadPic("***r_notexture***", data, 8, 8,
        QuakeImage.it_wall, 32);
  }


}
