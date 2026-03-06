package com.scnu.gpt.pojo.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DifyStreamResponse(
        @JsonProperty("event") String event,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("workflow_run_id") String workflowRunId,
        @JsonProperty("data") Object data
) {}