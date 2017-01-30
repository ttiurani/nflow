package io.nflow.tests.guice;

import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.nflow.engine.internal.config.WorkflowLifecycle;
import io.nflow.engine.internal.guice.EngineEnvironmentModule;
import io.nflow.engine.internal.guice.EngineModule;
import io.nflow.engine.service.WorkflowDefinitionService;
import io.nflow.engine.service.WorkflowInstanceService;
import io.nflow.engine.workflow.instance.WorkflowInstance;
import io.nflow.tests.demo.DemoWorkflow;
import io.nflow.tests.demo.DemoWorkflow.State;

public abstract class WorkflowWithEngineModuleAbstractTest {

  @Test
  public void testWorkflowWithGuice() {
    Properties props = getEngineConfigurationProperties();
    Injector injector = Guice.createInjector(new EngineEnvironmentModule(props), new EngineModule(null));

    WorkflowDefinitionService workflowDefinitionService = injector.getInstance(WorkflowDefinitionService.class);
    workflowDefinitionService.addWorkflowDefinition(new DemoWorkflow());

    WorkflowInstanceService workflowInstanceService = injector.getInstance(WorkflowInstanceService.class);

    WorkflowInstance instance = new WorkflowInstance.Builder().setType("demo").setState("begin").setNextActivation(DateTime.now())
        .build();
    int id = workflowInstanceService.insertWorkflowInstance(instance);

    while (!instance.state.equals(State.done.name())) {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      instance = workflowInstanceService.getWorkflowInstance(id);
    }

    injector.getInstance(WorkflowLifecycle.class).stop();
  }

  protected abstract Properties getEngineConfigurationProperties();
}
