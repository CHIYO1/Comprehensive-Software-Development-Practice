package com.scnu.gpt.pojo.gpt;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record DifyBlockResponse(//dify阻塞模式数据返回格式
        @JsonProperty("workflow_run_id") String workflowRunId,
        @JsonProperty("task_id") String taskId,
        ResponseData data
) {
    public record ResponseData(
            String id,
            @JsonProperty("workflow_id") String workflowId,
            String status,
            Outputs outputs,    //工作流处理结果
            Object error,
            @JsonProperty("elapsed_time") Double elapsedTime,
            @JsonProperty("total_tokens") Integer totalTokens,
            @JsonProperty("total_steps") Integer totalSteps,
            @JsonProperty("created_at") Long createdAt,
            @JsonProperty("finished_at") Long finishedAt
    ) {}

    public record Outputs(
            @JsonProperty("jsonOutput") JsonNode jsonOutput  // //格式化输出
    ) {}
}
