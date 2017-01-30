package io.nflow.rest.guice;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import io.nflow.engine.internal.config.NFlow;
import io.nflow.rest.config.RestConfiguration;

public class RestApiModuleTest {

  @Test
  public void testRestApiConfiguration() {
    AbstractModule mockEngineModule = new AbstractModule() {
      @Override
      protected void configure() {
      }

      @Provides
      @NFlow
      public ObjectMapper nflowObjectMapper() {
        return new ObjectMapper();
      }
    };

    Injector injector = Guice.createInjector(mockEngineModule, new RestApiModule());

    ObjectMapper restMapper = injector
        .getInstance(Key.get(ObjectMapper.class, Names.named(RestConfiguration.REST_OBJECT_MAPPER)));
    assertThat(restMapper.getSerializationConfig().hasMapperFeatures(WRITE_DATES_AS_TIMESTAMPS.getMask()), is(true));
  }
}
