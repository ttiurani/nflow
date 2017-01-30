package io.nflow.rest.guice;

import java.util.Properties;

import io.nflow.engine.internal.guice.EngineEnvironmentModule;

public class RestApiEnvironmentModule extends EngineEnvironmentModule {

  public RestApiEnvironmentModule(final Properties userProperties, String... classpathPropertiesFiles) {
    super(userProperties, addDefaultPropertiesFiles("nflow-rest-api.properties", classpathPropertiesFiles));
  }
}
