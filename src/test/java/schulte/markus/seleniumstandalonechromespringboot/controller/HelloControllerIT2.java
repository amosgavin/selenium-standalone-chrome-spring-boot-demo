package schulte.markus.seleniumstandalonechromespringboot.controller;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.DriverManagerType;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT2 {

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

  @LocalServerPort
  private int port;

  private URL base;

  @BeforeClass
  public static void initWebDriver() throws MalformedURLException {
    ChromeDriverManager.getInstance(DriverManagerType.CHROME).setup();

    /*final DockerPort seleniumStandaloneDockerPort = DOCKER_COMPOSE_RULE.containers()
      .container(SELENIUM_STANDALONE_CHROME_SERVICE_NAME).port(SELENIUM_HUB_PORT);*/
    final String remoteSeleniumUrl
      = "http://10.10.11.180:32787/wd/hub";

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
    this.base = new URL("http://10.10.11.180:8080");
  }

  @Test
  public void getHello() {
    driver.get(base.toString());

    final WebElement h1Element = driver.findElement(By.id("h1-hello"));
    Assert.assertEquals("Hello world!", h1Element.getText());
  }
}
