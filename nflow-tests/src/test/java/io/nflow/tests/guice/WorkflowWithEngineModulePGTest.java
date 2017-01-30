package io.nflow.tests.guice;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.V9_5_0;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;

import io.nflow.engine.internal.config.Profiles;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

public class WorkflowWithEngineModulePGTest extends WorkflowWithEngineModuleAbstractTest {

  private static PostgresProcess process;
  private static String dbUrl = null;

  @Override
  protected Properties getEngineConfigurationProperties() {
    Properties p = new Properties();
    p.setProperty("nflow.db.type", "postgresql");
    p.setProperty("nflow.executor.thread.count", "1");
    if (dbUrl != null) {
      p.setProperty("nflow.db.postgresql.url", dbUrl);
    }
    return p;
  }

  @BeforeClass
  public static void setup() throws IOException {
    String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
    Assume.assumeTrue(activeProfiles == null || activeProfiles.contains(Profiles.POSTGRESQL));
    if (activeProfiles == null) {
      PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
      PostgresConfig config = new PostgresConfig(V9_5_0, new AbstractPostgresConfig.Net(),
          new AbstractPostgresConfig.Storage("nflow"), new AbstractPostgresConfig.Timeout(),
          new AbstractPostgresConfig.Credentials("nflow", "nflow"));
      PostgresExecutable exec = runtime.prepare(config);
      process = exec.start();
      dbUrl = "jdbc:postgresql://" + config.net().host() + ":" + config.net().port() + "/nflow";
    }
  }

  @AfterClass
  public static void stopDB() {
    if (process != null) {
      process.stop();
    }
  }
}
