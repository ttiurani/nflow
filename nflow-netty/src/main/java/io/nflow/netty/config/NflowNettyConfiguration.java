package io.nflow.netty.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.nflow.engine.internal.config.NFlow;
import io.nflow.rest.config.RestConfiguration;

@Configuration
@Import(value = { RestConfiguration.class })
@EnableTransactionManagement
@ComponentScans({@ComponentScan("io.nflow.engine"), @ComponentScan("io.nflow.rest.v1.springweb")})
public class NflowNettyConfiguration {

  @Bean
  public PlatformTransactionManager transactionManager(@NFlow DataSource nflowDataSource)  {
    return new DataSourceTransactionManager(nflowDataSource);
  }

}
