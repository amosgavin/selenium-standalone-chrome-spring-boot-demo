package schulte.markus.seleniumstandalonechromespringboot.schedule;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: selenium-standalone-chrome-spring-boot-demo
 * @description:
 * @author: young
 * @create: 2018-09-25 19:02
 **/
@JobHandler(value="signOutHandler")
@Component
public class SignOutHandler extends IJobHandler {
  @Autowired
  private SignJob signJob;
  @Override
  public ReturnT<String> execute(String s) throws Exception {
    XxlJobLogger.log("JEDI-JOB, 下班打卡.");
    signJob.signOut();
    return SUCCESS;
  }
}
