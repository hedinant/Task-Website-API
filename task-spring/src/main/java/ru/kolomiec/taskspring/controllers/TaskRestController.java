package ru.kolomiec.taskspring.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kolomiec.taskspring.dto.ResponseMessageDTO;
import ru.kolomiec.taskspring.dto.TaskDTO;
import ru.kolomiec.taskspring.entity.PersonDetailsSecurityEntity;
import ru.kolomiec.taskspring.entity.Task;
import ru.kolomiec.taskspring.services.interfaces.TaskService;

import java.util.List;

@Tag(name = "Task Controller", description = "Controllers which allows add/get/delete tasks")
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;
    @GetMapping("/all-tasks")
    @Operation(summary = "return all task which owner by auth person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returns tasks"),
            @ApiResponse(responseCode = "400", description = "person does not have tasks")
    })
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal PersonDetailsSecurityEntity authPerson) {
        //TODO это оформляется короче через @ResponseBody. 400ка тут вроде пока не кидается.
        // лучше идти по id
        return ResponseEntity.ok(taskService.getAllTaskByPersonUsername(authPerson.getUsername()));
    }

    @PostMapping("/add-task")
    @Operation(summary = "add task to auth person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task is added"),
            @ApiResponse(responseCode = "400", description = "not valid task")
    })
    // TODO стоит разделить create и update. Это обычно даже разные методы PUT и POST
    public ResponseEntity<ResponseMessageDTO> addNewTaskToPerson(@RequestBody @Valid TaskDTO taskDTO,
                                                                 @AuthenticationPrincipal PersonDetailsSecurityEntity authPerson) {
        System.out.println(taskDTO.toString());
        taskService.saveTaskToPerson(authPerson, taskDTO);
        return ResponseEntity.ok(new ResponseMessageDTO("new task is saved"));
    }

    @DeleteMapping("/{taskId}/delete-task")
    @Operation(summary = "delete task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete task by id"),
            @ApiResponse(responseCode = "400", description = "person dont have task with such id")
    })
    public ResponseEntity<ResponseMessageDTO> deletePersonTask(@AuthenticationPrincipal PersonDetailsSecurityEntity authPerson,
                                                               @PathVariable("taskId") Long taskId) {
        taskService.deleteTaskOwnedByPerson(authPerson, taskId);
        // TODO "task with taskId %d is deleted".formatted(taskId)
        return ResponseEntity.ok(new ResponseMessageDTO(new StringBuilder("task with taskId ").append(taskId).append("is deleted").toString()));
    }
}
