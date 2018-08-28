package schulte.markus.seleniumstandalonechromespringboot.controller;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAControllerIT {

  /**
   * IP of docker0 network on docker-host.
   *
   * In network_mode=bridge, this value should be fixed, see <a href="https://docs.docker.com/engine/userguide/networking/#the-default-bridge-network">docs.docker.com</a>
   */
  private static final String DOCKER0_IP = "172.17.0.1";

  private static final String DOCKER_COMPOSE_YML_FILE
    = "src/test/resources/docker-compose-selenium-standalone-chrome.yml";

  private static final int SELENIUM_HUB_PORT = 4444;

  private static final String SELENIUM_STANDALONE_CHROME_SERVICE_NAME
    = "selenium-standalone-chrome";

  /*@ClassRule
  public static final DockerComposeRule DOCKER_COMPOSE_RULE = DockerComposeRule.builder()
    .file(DOCKER_COMPOSE_YML_FILE)
    .pullOnStartup(true)
    .waitingForService(SELENIUM_STANDALONE_CHROME_SERVICE_NAME,
      HealthChecks.toRespond2xxOverHttp(SELENIUM_HUB_PORT,
        (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")))
    .build();*/

  private static WebDriver driver;

//  @LocalServerPort
  private int port;

  private URL base;

  @BeforeClass
  public static void initWebDriver() throws MalformedURLException {
    ChromeDriverManager.getInstance().setup();

    /*final DockerPort seleniumStandaloneDockerPort = DOCKER_COMPOSE_RULE.containers()
      .container(SELENIUM_STANDALONE_CHROME_SERVICE_NAME).port(SELENIUM_HUB_PORT);*/
    final String remoteSeleniumUrl
      = "http://10.10.11.180:32791/wd/hub";

    driver = new RemoteWebDriver(new URL(remoteSeleniumUrl), DesiredCapabilities.chrome());
  }

  @AfterClass
  public static void closeWebDriver() {
    driver.close();
  }

  @Before
  public void setUp() throws MalformedURLException {
    /*
     * Chrome runs within a docker-container, but the actual Spring Boot application is executed
     * on host. So, Chrome has to test against host-address.
     */
    this.base = new URL("http://oa.huizhaofang.com");
  }

  @Test
  public void getSignn() throws InterruptedException {
    driver.manage().window().fullscreen();
    driver.get(base.toString());

    final WebElement loginid = driver.findElement(By.id("loginid"));
    loginid.sendKeys("0498");
    final WebElement userpassword = driver.findElement(By.id("userpassword"));
    userpassword.sendKeys("amos1983");
    final WebElement login = driver.findElement(By.id("login"));
    login.click();
    final WebElement leftBlockHrmDep = driver.findElement(By.id("leftBlockHrmDep"));
    Assert.assertEquals("平台技术部", leftBlockHrmDep.getText());

    final WebElement Message_undefined = driver.findElement(By.id("Message_undefined"));

    if (Message_undefined.getText().contains("签到")) {
      final WebElement buttonCancel = driver.findElement(By.xpath("//input[contains(@id,'_ButtonCancel_')]"));
      buttonCancel.click();
    }

    final WebElement Message_undefined2 = driver.findElement(By.id("Message_undefined"));
    if (Message_undefined2.getText().contains("成功签到")) {
      Assert.assertTrue(true);
    } else {
      Assert.assertTrue(false);
    }

  }

  @Test
  public void getSignOut() throws InterruptedException {
    driver.manage().window().fullscreen();
    driver.get(base.toString());

    final WebElement loginid = driver.findElement(By.id("loginid"));
    loginid.sendKeys("0498");
    final WebElement userpassword = driver.findElement(By.id("userpassword"));
    userpassword.sendKeys("amos1983");
    final WebElement login = driver.findElement(By.id("login"));
    login.click();
    final WebElement leftBlockHrmDep = driver.findElement(By.id("leftBlockHrmDep"));
    Assert.assertEquals("平台技术部", leftBlockHrmDep.getText());

    final WebElement Message_undefined = driver.findElement(By.id("Message_undefined"));

    if (Message_undefined.getText().contains("上次登录信息")) {
//      List<WebElement> list = driver.findElements(By.xpath("//input[@type='button'][@value='确定']"));
//      List<WebElement> list2 = driver.findElements(By.xpath("//input[contains(@id,'_ButtonCancel_')]"));
      final WebElement buttonCancel = driver.findElement(By.xpath("//input[contains(@id,'_ButtonCancel_')]"));
      buttonCancel.click();
    }

    final WebElement tdSignInfo = driver.findElement(By.xpath("//div[@id='sign_dispan']"));
    ((JavascriptExecutor) driver).executeScript("", tdSignInfo);
    tdSignInfo.click();
    /*Actions action = new Actions(driver);
    action.moveToElement(tdSignInfo).click().perform();*/

    Thread.sleep(2000);

    final WebElement Message_undefined2 = driver.findElement(By.id("Message_undefined"));
    if (Message_undefined2.getText().contains("您确定要签退吗")) {
      final WebElement buttonOK2 = driver.findElement(By.xpath("//input[contains(@id,'_ButtonOK_')]"));
      buttonOK2.click();
    }

    if (Message_undefined2.getText().contains("成功签退")) {
      Assert.assertTrue(true);
    } else {
      Assert.assertTrue(false);
    }

  }
}
