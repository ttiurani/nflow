package com.nitorcreations.nflow.engine.workflow.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitorcreations.nflow.engine.domain.StateExecutionImpl;
import com.nitorcreations.nflow.engine.workflow.Mutable;
import com.nitorcreations.nflow.engine.workflow.data.WorkflowStateMethod.StateParameter;

@Component
public class ObjectStringMapper {
  private final ObjectMapper mapper;

  @Inject
  public ObjectStringMapper(@Named("nflow-ObjectMapper") ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  public Object[] createArguments(StateExecutionImpl execution,
      WorkflowStateMethod method) {
    Object[] args = new Object[method.params.length + 1];
    args[0] = execution;
    StateParameter[] params = method.params;
    for (int i = 1; i <= params.length; i++) {
      StateParameter param = params[i - 1];
      String value = execution.getVariable(param.key);
      if (value == null) {
        Object def = param.nullValue;
        if (def instanceof Constructor) {
          try {
            def = ((Constructor<Object>) def).newInstance();
          } catch (InstantiationException | IllegalAccessException
              | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate default value for " + param.key, e);
          }
        }
        args[i] = def;
      } else if (String.class.equals(param.type)) {
        args[i] = value;
      } else {
        JavaType type = mapper.getTypeFactory().constructType(param.type);
        try {
          args[i] = mapper.readValue(value, type);
        } catch (IOException e) {
          throw new RuntimeException("Failed to deserialize value for " + param.key, e);
        }
      }
      if (param.mutable) {
        args[i] = new Mutable<>(args[i]);
      }
    }
    return args;
  }

  @SuppressWarnings("unchecked")
  public void storeArguments(StateExecutionImpl execution,
      WorkflowStateMethod method, Object[] args) {
    StateParameter[] params = method.params;
    for (int i = 0; i < params.length; i++) {
      StateParameter param = params[i];
      if (param.readOnly) {
        continue;
      }
      Object value = args[i + 1];
      if (value == null) {
        continue;
      }
      String sVal;
      if (param.mutable) {
        value = ((Mutable<Object>) value).val;
      }
      if (String.class.equals(param.type)) {
        sVal = (String) value;
      } else {
        try {
          sVal = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to serialize value for " + param.key, e);
        }
      }
      execution.setVariable(param.key, sVal);
    }
  }

}
