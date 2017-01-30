package io.nflow.rest.guice;

import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.nflow.engine.internal.config.NFlow;
import io.nflow.rest.config.RestConfiguration;

public class RestApiModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Provides
  @Named(RestConfiguration.REST_OBJECT_MAPPER)
  public ObjectMapper nflowRestObjectMapper(@NFlow ObjectMapper nflowObjectMapper) {
    RestConfiguration restConfiguration = new RestConfiguration();
    return restConfiguration.nflowRestObjectMapper(nflowObjectMapper);
  }
}
