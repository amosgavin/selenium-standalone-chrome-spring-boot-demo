package schulte.markus.seleniumstandalonechromespringboot.controller;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import schulte.markus.seleniumstandalonechromespringboot.schedule.SignJob;

import javax.annotation.Resource;
import java.net.MalformedURLException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAControllerIT2 {

  @Resource
  private SignJob signJob;
  @Test
  public void getSignIn() throws MalformedURLException, InterruptedException {
    signJob.signIn();

  }

  @Test
  public void getSignOut() throws MalformedURLException, InterruptedException {
    signJob.signOut();
  }
}
