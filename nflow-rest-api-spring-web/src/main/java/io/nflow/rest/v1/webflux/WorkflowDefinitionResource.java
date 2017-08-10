package io.nflow.rest.v1.webflux;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.nflow.engine.internal.dao.WorkflowDefinitionDao;
import io.nflow.engine.service.WorkflowDefinitionService;
import io.nflow.rest.v1.ResourceBase;
import io.nflow.rest.v1.converter.ListWorkflowDefinitionConverter;
import io.nflow.rest.v1.msg.ListWorkflowDefinitionResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/nflow/v1/workflow-definition", produces = "application/json")
@Api("nFlow workflow definition management")
@Component
public class WorkflowDefinitionResource extends ResourceBase {

  private final WorkflowDefinitionService workflowDefinitions;
  private final ListWorkflowDefinitionConverter converter;
  private final WorkflowDefinitionDao workflowDefinitionDao;

  @Autowired
  public WorkflowDefinitionResource(WorkflowDefinitionService workflowDefinitions, ListWorkflowDefinitionConverter converter,
      WorkflowDefinitionDao workflowDefinitionDao) {
    this.workflowDefinitions = workflowDefinitions;
    this.converter = converter;
    this.workflowDefinitionDao = workflowDefinitionDao;
  }

  @GetMapping
  @ApiOperation(value = "List workflow definitions", response = ListWorkflowDefinitionResponse.class, responseContainer = "List",
    notes = "Returns workflow definition(s): all possible states, transitions between states and other setting metadata."
      + "The workflow definition can deployed in nFlow engine or historical workflow definition stored in the database.")
  public List<ListWorkflowDefinitionResponse> listWorkflowDefinitions(
      @RequestParam("type") @ApiParam(value = "Included workflow types") List<String> types) {
    return super.listWorkflowDefinitions(types, this.workflowDefinitions, this.converter, this.workflowDefinitionDao);
  }
}
