package io.nflow.rest.v1.webflux;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.nflow.engine.service.WorkflowExecutorService;
import io.nflow.rest.v1.converter.ListWorkflowExecutorConverter;
import io.nflow.rest.v1.msg.ListWorkflowExecutorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/nflow/v1/workflow-executor", consumes = "application/json", produces = "application/json")
@Api("nFlow workflow executor management")
@Component
public class WorkflowExecutorResource {

  private final WorkflowExecutorService workflowExecutors;
  private final ListWorkflowExecutorConverter converter;

  @Autowired
  public WorkflowExecutorResource(WorkflowExecutorService workflowExecutors, ListWorkflowExecutorConverter converter) {
    this.workflowExecutors = workflowExecutors;
    this.converter = converter;
  }

  @GetMapping
  @ApiOperation(value = "List workflow executors", response = ListWorkflowExecutorResponse.class, responseContainer = "List")
  public Collection<ListWorkflowExecutorResponse> listWorkflowExecutors() {
    return workflowExecutors.getWorkflowExecutors().stream()
        .map(executor -> this.converter.convert(executor))
        .collect(Collectors.toList());
  }
}
