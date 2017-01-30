package io.nflow.engine.internal.guice;

import java.util.concurrent.ThreadFactory;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.core.env.Environment;
import org.springframework.core.io.AbstractResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.nflow.engine.internal.config.EngineConfiguration;
import io.nflow.engine.internal.config.NFlow;
import io.nflow.engine.internal.config.WorkflowLifecycle;
import io.nflow.engine.internal.dao.ExecutorDao;
import io.nflow.engine.internal.dao.WorkflowInstanceDao;
import io.nflow.engine.internal.executor.WorkflowInstanceExecutor;
import io.nflow.engine.internal.storage.db.DatabaseConfiguration;
import io.nflow.engine.internal.storage.db.DatabaseInitializer;
import io.nflow.engine.internal.storage.db.H2DatabaseConfiguration;
import io.nflow.engine.internal.storage.db.MysqlDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.OracleDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.PgDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.SQLVariants;
import io.nflow.engine.service.WorkflowDefinitionService;

public class EngineModule extends AbstractModule {

  private final Object metricRegistry;
  private final EngineConfiguration engineConfiguration;

  public EngineModule(final Object metricRegistry) {
    this.metricRegistry = metricRegistry;
    this.engineConfiguration = new EngineConfiguration();
  }

  @Override
  protected void configure() {
    TransactionSynchronizationManager.initSynchronization();
    TransactionSynchronizationManager.setActualTransactionActive(true);

    bind(ExecutorDao.class).in(Singleton.class);
    bind(WorkflowLifecycle.class).in(Singleton.class);
    bind(WorkflowInstanceDao.class).in(Singleton.class);
    bind(WorkflowDefinitionService.class).in(Singleton.class);

    install(new EngineInitModule());
  }

  @Provides
  @Inject
  @NFlow
  @Singleton
  public AbstractResource nflowNonSpringWorkflowsListing(Environment env) {
    return engineConfiguration.nflowNonSpringWorkflowsListing(env);
  }

  @Provides
  @NFlow
  @Singleton
  public ThreadFactory nflowThreadFactory() {
    return engineConfiguration.nflowThreadFactory();
  }

  @Provides
  @Inject
  @Singleton
  public WorkflowInstanceExecutor nflowExecutor(@NFlow ThreadFactory factory, Environment env) {
    return engineConfiguration.nflowExecutor(factory, env);
  }

  @Provides
  @NFlow
  @Singleton
  public ObjectMapper nflowObjectMapper() {
    return engineConfiguration.nflowObjectMapper();
  }

  @Provides
  @NFlow
  @Singleton
  public DataSource nflowDataSource(Environment env) {
    return getDatabaseConfiguration(env).nflowDatasource(env, metricRegistry);
  }

  @Provides
  @NFlow
  @Singleton
  @Inject
  public DatabaseInitializer nflowDatabaseInitializer(@NFlow DataSource dataSource, Environment env) {
    return getDatabaseConfiguration(env).nflowDatabaseInitializer(dataSource, env);
  }

  @Provides
  @NFlow
  @Singleton
  @Inject
  public JdbcTemplate nflowJdbcTemplate(@NFlow DataSource dataSource, @NFlow DatabaseInitializer DatabaseInitializer,
      Environment env) {
    return getDatabaseConfiguration(env).nflowJdbcTemplate(dataSource);
  }

  @Provides
  @NFlow
  @Singleton
  @Inject
  public NamedParameterJdbcTemplate nflowNamedParameterJdbcTemplate(@NFlow DataSource dataSource,
      @NFlow DatabaseInitializer DatabaseInitializer, Environment env) {
    return getDatabaseConfiguration(env).nflowNamedParameterJdbcTemplate(dataSource);
  }

  @Provides
  @NFlow
  @Singleton
  @Inject
  public TransactionTemplate nflowTransactionTemplate(@NFlow DataSource dataSource, Environment env) {
    return getDatabaseConfiguration(env).nflowTransactionTemplate(new DataSourceTransactionManager(dataSource));
  }

  @Provides
  @Singleton
  @Inject
  public SQLVariants nflowSQLVariants(Environment env) {
    return getDatabaseConfiguration(env).sqlVariants();
  }

  private DatabaseConfiguration getDatabaseConfiguration(Environment env) {
    DatabaseConfiguration db;
    String dbtype = env.getProperty("nflow.db.type", String.class);
    switch (dbtype) {
    case "h2":
      db = new H2DatabaseConfiguration();
      break;
    case "mysql":
      db = new MysqlDatabaseConfiguration();
      break;
    case "oracle":
      db = new OracleDatabaseConfiguration();
      break;
    case "postgresql":
      db = new PgDatabaseConfiguration();
      break;
    default:
      throw new RuntimeException("Unknown DB");
    }
    return db;
  }

  class EngineInitModule extends AbstractModule {
    @Override
    protected void configure() {
      requestInjection(this);
    }

    @Inject
    void initPostConstruct(WorkflowInstanceDao workflowInstanceDao, ExecutorDao executorDao,
        WorkflowDefinitionService workflowDefinitionService) throws Exception {
      workflowInstanceDao.findColumnMaxLengths();
      executorDao.findHostMaxLength();
      workflowDefinitionService.postProcessWorkflowDefinitions();
    }

    @Inject
    void initLifeCycleAutoStart(WorkflowLifecycle lifecycle) {
      if (lifecycle.isAutoStartup()) {
        lifecycle.start();
      }
    }

    @Inject
    void initH2TcpServer(Environment env) throws Exception {
      Server server = new H2DatabaseConfiguration().h2TcpServer(env);
      if (server != null) {
        server.start();
      }
    }

    @Inject
    void initH2ConsoleServer(Environment env) throws Exception {
      Server server = new H2DatabaseConfiguration().h2ConsoleServer(env);
      if (server != null) {
        server.start();
      }
    }
  }
}
