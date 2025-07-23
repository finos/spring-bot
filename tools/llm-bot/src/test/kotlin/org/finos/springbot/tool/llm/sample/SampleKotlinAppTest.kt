package org.finos.springbot.tool.llm.sample

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureWebMvc
class SampleKotlinAppTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test task creation`() {
        val createRequest = CreateTaskRequest(title = "Test Task", description = "Test Description")

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest))
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Task"))
                .andExpect(jsonPath("$.data.description").value("Test Description"))
    }

    @Test
    fun `test get all tasks`() {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    fun `test get completed tasks`() {
        mockMvc.perform(get("/api/tasks/completed"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    fun `test get pending tasks`() {
        mockMvc.perform(get("/api/tasks/pending"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    fun `test task utils extension functions`() {
        val tasks =
                listOf(
                        Task("1", "Task 1", "Description 1", completed = true),
                        Task("2", "Task 2", "Description 2", completed = false),
                        Task("3", "Task 3", "Description 3", completed = true)
                )

        val completedTasks = TaskUtils.getCompletedTasks(tasks)
        val pendingTasks = TaskUtils.getPendingTasks(tasks)

        assertEquals(2, completedTasks.size)
        assertEquals(1, pendingTasks.size)
        assertTrue(completedTasks.all { it.completed })
        assertTrue(pendingTasks.all { !it.completed })
    }

    @Test
    fun `test task overdue calculation`() {
        val oldTask =
                Task(
                        id = "old",
                        title = "Old Task",
                        description = "Old Description",
                        completed = false,
                        createdAt = java.time.LocalDateTime.now().minusDays(10)
                )

        val newTask =
                Task(
                        id = "new",
                        title = "New Task",
                        description = "New Description",
                        completed = false,
                        createdAt = java.time.LocalDateTime.now().minusDays(3)
                )

        assertTrue(TaskUtils.isOverdue(oldTask))
        assertFalse(TaskUtils.isOverdue(newTask))
    }
}
