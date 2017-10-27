package io.nflow.rest.v1.config;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nflow.rest.config.RestConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class RestConfigurationTest {

  RestConfiguration configuration;

  @Before
  public void setup() {
    configuration = new RestConfiguration();
  }

  @Test
  public void nflowRestObjectMapperInstantiated() {
    ObjectMapper restMapper = configuration.nflowRestObjectMapper(new ObjectMapper());
    // FIXME: It seems that setting is(false) is only testing the default value of Jackson,
    //        as changing in RestConfiguration.java the value to true does not make this fail. This
    //        test needs to be done somehow differently.
    assertThat(restMapper.getSerializationConfig().hasMapperFeatures(WRITE_DATES_AS_TIMESTAMPS.getMask()), is(false));
  }
}
