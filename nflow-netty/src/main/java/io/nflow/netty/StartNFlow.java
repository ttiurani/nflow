package io.nflow.netty;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import io.nflow.netty.config.NflowNettyConfiguration;
import io.nflow.server.spring.NflowStandardEnvironment;
import reactor.ipc.netty.http.server.HttpServer;

public class StartNFlow {

  public static final String DEFAULT_SERVER_CONFIGURATION = "application.properties";
  public static final String DEFAULT_SERVER_IP = "0.0.0.0";
  public static final Integer DEFAULT_SERVER_PORT = 12012;

  private final Class<?> springMainClass;

  public StartNFlow(Class<?> springMainClass) {
    this.springMainClass = springMainClass;
  }

  public ApplicationContext startNetty(final boolean createDatabase, final boolean autoStartNflow) throws IOException {
    return this.startNetty(new String[] {}, createDatabase, autoStartNflow, DEFAULT_SERVER_CONFIGURATION);
  }

  public ApplicationContext startNetty(final boolean createDatabase, final boolean autoStartNflow,
      final String mainConfigurationClasspath) throws IOException {
    return this.startNetty(new String[] {}, createDatabase, autoStartNflow, mainConfigurationClasspath);
  }

  public ApplicationContext startNetty(final String[] args, final boolean createDatabase,
      final boolean autoStartNflow) throws IOException {
    return this.startNetty(args, createDatabase, autoStartNflow, DEFAULT_SERVER_CONFIGURATION);
  }

  public ApplicationContext startNetty(final String[] args, final boolean createDatabase,
      final boolean autoStartNflow, final String mainConfigurationClasspath) throws IOException {
    final String[] customArgs = Arrays.copyOf(args, args.length + 2);
    customArgs[customArgs.length-2] = "--nflow.db.create_on_startup=" + createDatabase;
    customArgs[customArgs.length-1] = "--nflow.autostart=" + autoStartNflow;
    return this.startNetty(customArgs, mainConfigurationClasspath);
  }

  public ApplicationContext startNetty(final String[] args, final String mainConfigurationClasspath) throws IOException {
    final Map<String, Object> argsMap = new HashMap<>();

    // Add optional arguments to map, using Spring's helper class
    final SimpleCommandLinePropertySource arguments = new SimpleCommandLinePropertySource(args);
    Arrays.asList(arguments.getPropertyNames()).forEach(optionName -> {
      argsMap.put(optionName, arguments.getProperty(optionName));
    });

    // Add also properties that are not optional, i.e. don't start with "--"
    Arrays.asList(args).stream().filter(value -> !value.startsWith("--")).forEach(value -> argsMap.put(value, null));

    // Create context
    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    final NflowStandardEnvironment env = new NflowStandardEnvironment(argsMap);
    final ResourcePropertySource mainConfiguration = new ResourcePropertySource(mainConfigurationClasspath);
    env.getPropertySources().addLast(mainConfiguration);
    context.setEnvironment(env);
    context.register(DelegatingWebFluxConfiguration.class, NflowNettyConfiguration.class, this.springMainClass);
    context.refresh();

    // Start netty
    final HttpHandler handler = WebHttpHandlerBuilder.applicationContext(context).build();
    final ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
    HttpServer.create(
        mainConfiguration.containsProperty("server.ip") ? (String) mainConfiguration.getProperty("server.ip")
            : DEFAULT_SERVER_IP,
        mainConfiguration.containsProperty("server.port") ? Integer.valueOf((String) mainConfiguration.getProperty("server.port"))
            : DEFAULT_SERVER_PORT)
        .newHandler(adapter)
        .block();

    return context;
  }
}