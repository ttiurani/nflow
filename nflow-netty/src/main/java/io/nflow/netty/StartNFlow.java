package io.nflow.netty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import io.nflow.netty.config.NflowNettyConfiguration;
import io.nflow.server.spring.NflowStandardEnvironment;

public class StartNFlow {

  private final Class<?> springBootMainClass;

  public StartNFlow(Class<?> springBootMainClass) {
    this.springBootMainClass = springBootMainClass;
  }

  public Environment startNetty(final String[] args, final boolean createDatabase, final boolean autoStartNflow) {
    final String[] customArgs = Arrays.copyOf(args, args.length + 2);
    customArgs[customArgs.length-2] = "--nflow.db.create_on_startup=" + createDatabase;
    customArgs[customArgs.length-1] = "--nflow.autostart=" + autoStartNflow;
    return this.startNetty(customArgs);
  }

  public Environment startNetty(final String[] args) {
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
    return env;
  }

}
