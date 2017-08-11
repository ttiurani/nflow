package io.nflow.netty;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import io.nflow.netty.config.NflowNettyConfiguration;
import io.nflow.server.spring.NflowStandardEnvironment;

public class StartNFlow {

  private final Class<?> springBootMainClass;

  public StartNFlow(Class<?> springBootMainClass) {
    this.springBootMainClass = springBootMainClass;
  }

  public void startNetty(String[] args) {
    final SpringApplication application = new SpringApplication(springBootMainClass, NflowNettyConfiguration.class);
    final ApplicationArguments arguments = new DefaultApplicationArguments(args);
    final Map<String, Object> argsMap = new HashMap<>();
    arguments.getOptionNames().forEach(optionName -> {
      argsMap.put(optionName, arguments.getOptionValues(optionName));
    });
    arguments.getNonOptionArgs().forEach(arg -> {
      argsMap.put(arg, null);
    });
    final ConfigurableEnvironment env = new NflowStandardEnvironment(argsMap);
    application.setEnvironment(env);
    application.run(args);
  }

}
