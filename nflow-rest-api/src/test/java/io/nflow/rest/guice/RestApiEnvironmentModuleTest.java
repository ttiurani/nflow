package io.nflow.rest.guice;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.springframework.core.env.Environment;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RestApiEnvironmentModuleTest {

  @Test
  public void testRestApiEnvironmentModule() {
    Injector injector = Guice.createInjector(new RestApiEnvironmentModule(null));
    Environment env = injector.getInstance(Environment.class);
    assertThat(env.getProperty("nflow.rest.cors.enabled"), is("true"));
    assertThat(env.getProperty("nflow.rest.allow.origin"), is("*"));
    assertThat(env.getProperty("nflow.rest.allow.headers"),
        is("X-Requested-With, Content-Type, Origin, Referer, User-Agent, Accept"));
  }

  @Test
  public void testRestApieEnvironmentModuleWithCustomizedProperties() {
    Properties p = new Properties();
    p.put("nflow.rest.cors.enabled", "false");
    p.put("nflow.rest.allow.origin", "*nitor.fi");
    Injector injector = Guice.createInjector(new RestApiEnvironmentModule(p));
    Environment env = injector.getInstance(Environment.class);
    assertThat(env.getProperty("nflow.rest.cors.enabled"), is("false"));
    assertThat(env.getProperty("nflow.rest.allow.origin"), is("*nitor.fi"));
    assertThat(env.getProperty("nflow.rest.allow.headers"),
        is("X-Requested-With, Content-Type, Origin, Referer, User-Agent, Accept"));
  }

  @Test
  public void testRestApiEnvironmentModuleWithClasspathFile() {
    Injector injector = Guice.createInjector(new RestApiEnvironmentModule(null, "nflow-rest-api-test.properties"));
    Environment env = injector.getInstance(Environment.class);
    assertThat(env.getProperty("nflow.rest.cors.enabled"), is("false"));
    assertThat(env.getProperty("nflow.rest.allow.origin"), is("*nitor.fi"));
    assertThat(env.getProperty("nflow.rest.allow.headers"),
        is("X-Requested-With, Content-Type, Origin, Referer, User-Agent, Accept"));
  }
}
