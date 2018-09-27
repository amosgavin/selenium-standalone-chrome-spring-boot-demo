package schulte.markus.seleniumstandalonechromespringboot.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @program: selenium-standalone-chrome-spring-boot-demo
 * @description:
 * @author: young
 * @create: 2018-08-31 17:09
 **/
public class ImageUtils {
  /**
   * 裁剪图片：去掉黑边
   */
  public static BufferedImage clipImage(BufferedImage srcImage) {
    return srcImage.getSubimage(1, 1, srcImage.getWidth() - 2, srcImage.getHeight() - 2);
  }

  /**
   * 灰度化
   */
  public static BufferedImage grayImage(BufferedImage srcImage) {
    return copyImage(srcImage, new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY));
  }

  /**
   * 二值化
   */
  public static BufferedImage binaryImage(BufferedImage srcImage) {
    return copyImage(srcImage, new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY));
  }

  public static BufferedImage copyImage(BufferedImage srcImage, BufferedImage destImage) {
    for (int y = 0; y < srcImage.getHeight(); y++) {
      for (int x = 0; x < srcImage.getWidth(); x++) {
        destImage.setRGB(x, y, srcImage.getRGB(x, y));
      }
    }
    return destImage;
  }

  public static BufferedImage removeInterference(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        boolean c = true;
        // 这个像素块上下左右是不是都是黑色的,如果是,这个像素当作黑色的
        int roundWhiteCount = 0;
        if (isBlackColor(image, x + 1, y + 1))
          roundWhiteCount++;
        if (isBlackColor(image, x + 1, y - 1))
          roundWhiteCount++;
        if (isBlackColor(image, x - 1, y + 1))
          roundWhiteCount++;
        if (isBlackColor(image, x - 1, y - 1))
          roundWhiteCount++;
        if (roundWhiteCount >= 4) {
          c = false;
        }

        if (!isBlackColor(image, x, y) && c) {
          image.setRGB(x, y, 0xFFFFFFFF); //argb:AARRGGBB
        }
      }
    }
    return image;
  }

  private static boolean isBlackColor(BufferedImage image, int x, int y) {
    // 检查这个像素块是不是边缘的
    if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
      return false;
    }

    int pixel = image.getRGB(x, y);

    return
      // R
      (pixel & 0xFF0000) >> 16 < 30
        // G
        && (pixel & 0xFF00) >> 8 < 30
        // B
        && (pixel & 0xFF) < 30;
  }

  public static BufferedImage allBlack(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        // 不是纯白就填黑
        if ((image.getRGB(x, y) & 0xFFFFFF) != (new Color(255, 255, 255).getRGB() & 0xFFFFFF)) {
          image.setRGB(x, y, 0xFF000000);
        }
      }
    }
    return image;
  }
}
