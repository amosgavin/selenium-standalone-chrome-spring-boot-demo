package schulte.markus.seleniumstandalonechromespringboot.schedule;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.DriverManagerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;
import schulte.markus.seleniumstandalonechromespringboot.utils.ChineseCalendarUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @program: selenium-standalone-chrome-spring-boot-demo
 * @description:
 * @author: young
 * @create: 2018-08-27 19:23
 **/
@Slf4j
@Component
public class SignJob {
  private static final String remoteSeleniumUrl = "http://10.10.11.180:32769/wd/hub";
  private static final String baseUrl = "http://oa.huizhaofang.com";
  private static final String loginId = "0498";
  private static final String password = "";
  private static final long millisMin = 0L;
  private static final long millisMax = 15*60*1000L;
  private static final String loginInfoTip = "上次登录信息";
  private static final String buttonOKXpack = "//input[contains(@id,'_ButtonOK_')]";
  private static final String buttonCancelXpack = "//input[contains(@id,'_ButtonCancel_')]";
  private static final String draghandleXpack = "//tr[contains(@id,'_Draghandle_')]";

  private long randomLong() {
    long min = 1000;
    long max = 2000;
    return RandomUtils.nextLong(min, max);
  }

  private boolean isHoliday(LocalDateTime date) {
    DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    LocalDateTime date = LocalDateTime.now();
//    date = date.plusDays(3L);
    try {
      log.info(date.format(formate));
      return ChineseCalendarUtils.isHoliday(date.format(formate));
    } catch (Exception e) {
      log.info("{}", e);
    }
    return false;
  }

  private void sleep() throws InterruptedException {
    if (isHoliday(LocalDateTime.now())) {
      log.info("今天假期！");
      return;
    } else if (isHoliday(LocalDateTime.now().plusDays(1))) {
      log.info("明天是假期，提前1小时打卡");
//      Thread.sleep(randomLong());
    } else if (isHoliday(LocalDateTime.now().plusDays(-1))) {
      log.info("第一天上班，打卡");
      Thread.sleep(60*60*1000);
      Thread.sleep(20*60*1000);
      Thread.sleep(randomLong());
    } else {
      log.info("正常上班，打卡");
      Thread.sleep(60*60*1000);
      Thread.sleep(RandomUtils.nextLong(millisMin, millisMax));
    }
  }

  private WebDriver login() throws MalformedURLException, InterruptedException {
    ChromeDriverManager.getInstance(DriverManagerType.CHROME).setup();

    WebDriver driver = new RemoteWebDriver(new URL(remoteSeleniumUrl), DesiredCapabilities.chrome());

    URL base = new URL(baseUrl);
    driver.manage().window().fullscreen();
    log.info("fullscreen");
    driver.get(base.toString());

    final WebElement loginid = driver.findElement(By.id("loginid"));
    loginid.sendKeys(loginId);
    final WebElement userpassword = driver.findElement(By.id("userpassword"));
    userpassword.sendKeys(password);
    Thread.sleep(randomLong());
    final WebElement login = driver.findElement(By.id("login"));
    login.click();
    log.info("登录成功");
    Thread.sleep(randomLong());
    final WebElement leftBlockHrmDep = driver.findElement(By.id("leftBlockHrmDep"));
    log.info(leftBlockHrmDep.getText());
//      Assert.assertTrue("平台技术部".equals(leftBlockHrmDep.getText()) || "支付产品".equals(leftBlockHrmDep.getText()) || "支付技术".equals(leftBlockHrmDep.getText()));
    return driver;
  }

  private boolean daka(WebDriver driver) throws InterruptedException {
    Thread.sleep(randomLong());
    final List<WebElement> Message_undefineds = driver.findElements(By.id("Message_undefined"));
    WebElement Message_undefined = null;
    if (null!=Message_undefineds&&Message_undefineds.size()>0) {
      Message_undefined = Message_undefineds.get(Message_undefineds.size()-1);
      log.info(Message_undefined.getText());
    }

      if (null != Message_undefined && StringUtils.isNoneBlank(Message_undefined.getText())) {
        if (Message_undefined.getText().contains(loginInfoTip)) {
          final WebElement buttonCancel = driver.findElement(By.xpath(buttonCancelXpack));
          buttonCancel.click();
          log.info("上次登录信息click");
        } else if (Message_undefined.getText().contains("今天是工作日，现在要签到吗")) {
          final WebElement buttonOK = driver.findElement(By.xpath(buttonOKXpack));
          buttonOK.click();
          log.info("今天是工作日，现在要签到吗click");
        } else if (Message_undefined.getText().contains("您确定要签退吗")) {
          final WebElement buttonOK = driver.findElement(By.xpath(buttonOKXpack));
          buttonOK.click();
          log.info("您确定要签退吗click");
        } else if (Message_undefined.getText().contains("成功签到")) {
          Assert.assertTrue(true);
          log.info("成功签到");
          return false;
        } else if (Message_undefined.getText().contains("成功签退")) {
          Assert.assertTrue(true);
          log.info("成功签退");
          return false;
        }
      } else {
        log.info("失败签退");
        return false;
      }
    return true;
  }

//  @Scheduled(cron = "0 19 8 * * ?")
  public void signIn() throws MalformedURLException, InterruptedException {
    sleep();
    WebDriver driver = null;
    try {
      driver = login();

      boolean isSuccess = true;
      do {
        isSuccess = daka(driver);
      } while (isSuccess);

    } finally {
      if (null!=driver) {
        driver.close();
      }
    }
  }

//  @Scheduled(cron = "0 1 18 * * ?")
  public void signOut() throws MalformedURLException, InterruptedException {
    sleep();
    WebDriver driver = null;
    try {
      driver = login();

      final WebElement tdSignInfo = driver.findElement(By.xpath("//div[@id='sign_dispan']"));
      log.info(tdSignInfo.getText());

      ((JavascriptExecutor) driver).executeScript("", tdSignInfo);
      tdSignInfo.click();

      boolean isSuccess = true;
      do {
        isSuccess = daka(driver);
      } while (isSuccess);

    } finally {
      if (null!=driver) {
        driver.close();
      }
    }
  }

}
