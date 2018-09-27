package schulte.markus.seleniumstandalonechromespringboot.controller;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import schulte.markus.seleniumstandalonechromespringboot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Tess4jTest {

  @Test
  public void testTess4j()  {
    ITesseract instance = new Tesseract();
    File imgDir = new File("/data/tess/img_data");

    /**
     *  获取项目根路径，例如： D:\IDEAWorkSpace\tess4J
     */
    File directory = new File("/Users/macos/Documents/YY/workspace/java/selenium-standalone-chrome-spring-boot-demo/");
    String courseFile = null;
    try {
      courseFile = directory.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //设置训练库的位置
//    instance.setDatapath("/Users/macos/Documents/YY/workspace/java/selenium-standalone-chrome-spring-boot-demo/tessdata");

    instance.setLanguage("eng");//chi_sim ：简体中文， eng	根据需求选择语言库

    //对img_data文件夹中的每个验证码进行识别
    //文件名即正确的结果
    for (File imgFile : imgDir.listFiles()) {
      //输出图片文件名，即正确识别结果
      System.out.println("ImgFile: "+imgFile.getAbsolutePath());
      try {
        //该例子输入的是文件，也可输入BufferedImage
        String ocrResult = instance.doOCR(imgFile);

        //输出识别结果
        System.out.println("OCR Result: " + ocrResult);
      } catch (TesseractException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testImg() throws IOException {
    BufferedImage originImage = ImageIO.read(new File("/data/tess/img_data/verifyCode.jpg"));
    BufferedImage processedImage = null;
    processedImage = ImageUtils.clipImage(originImage);
    ImageIO.write(processedImage, "JPEG", new File("/data/tess/img_data/output1.jpg"));
    processedImage = ImageUtils.grayImage(originImage);
    ImageIO.write(processedImage, "JPEG", new File("/data/tess/img_data/output2.jpg"));
    processedImage = ImageUtils.binaryImage(originImage);
    ImageIO.write(processedImage, "JPEG", new File("/data/tess/img_data/output.jpg"));
  }


}
