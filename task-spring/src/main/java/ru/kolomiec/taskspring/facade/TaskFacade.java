package ru.kolomiec.taskspring.facade;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.kolomiec.taskspring.dto.TaskDTO;
import ru.kolomiec.taskspring.entity.Task;

//TODO ИМХО конкретно лишний класс.
@Component
@RequiredArgsConstructor
public class TaskFacade {

    private final ModelMapper modelMapper;

    public Task fromTaskDTOToTask(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    public TaskDTO fromTaskToTaskDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
