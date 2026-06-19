package io.patchpilot.backend.task;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FixTaskService fixTaskService;

    @Test
    void should_list_tasks() throws Exception {
        createTask("delivery-list");

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].id").value(not(nullValue())))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[0].pullRequestUrl").value(nullValue()))
                .andExpect(jsonPath("$.data[0].completedAt").value(nullValue()))
                .andExpect(jsonPath("$.data[0].updatedAt").value(not(nullValue())));
    }

    @Test
    void should_get_task_by_id() throws Exception {
        FixTaskVo task = createTask("delivery-get");

        mockMvc.perform(get("/api/tasks/{id}", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.id()))
                .andExpect(jsonPath("$.data.repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data.issueNumber").value(42))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value(nullValue()))
                .andExpect(jsonPath("$.data.completedAt").value(nullValue()))
                .andExpect(jsonPath("$.data.updatedAt").value(not(nullValue())));
    }

    @Test
    void should_return_404_for_missing_task() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    private FixTaskVo createTask(String deliveryId) {
        return fixTaskService.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        ));
    }
}
