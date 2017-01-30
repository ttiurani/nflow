package io.nflow.tests.guice;

import java.util.Properties;

import org.junit.Assume;
import org.junit.BeforeClass;

import io.nflow.engine.internal.config.Profiles;

public class WorkflowWithEngineModuleH2Test extends WorkflowWithEngineModuleAbstractTest {

  @BeforeClass
  public static void setup() {
    String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
    Assume.assumeTrue(activeProfiles == null || activeProfiles.contains(Profiles.H2));
  }

  @Override
  protected Properties getEngineConfigurationProperties() {
    Properties p = new Properties();
    p.setProperty("nflow.db.type", "h2");
    p.setProperty("nflow.executor.thread.count", "1");
    p.setProperty("nflow.db.h2.url", "jdbc:h2:mem:workflowwithenginemoduletest;TRACE_LEVEL_FILE=4;DB_CLOSE_DELAY=-1");
    p.setProperty("nflow.db.h2.tcp.port", "");
    p.setProperty("nflow.db.h2.console.port", "");
    return p;
  }
}
