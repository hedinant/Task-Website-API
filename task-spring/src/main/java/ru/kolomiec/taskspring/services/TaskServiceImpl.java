package ru.kolomiec.taskspring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolomiec.taskspring.aspects.ServiceLog;
import ru.kolomiec.taskspring.dto.TaskDTO;
import ru.kolomiec.taskspring.entity.PersonDetailsSecurityEntity;
import ru.kolomiec.taskspring.entity.Task;
import ru.kolomiec.taskspring.exceptions.customexceptions.EmptyPersonTasksException;
import ru.kolomiec.taskspring.exceptions.customexceptions.PersonHaveNotSuchTaskException;
import ru.kolomiec.taskspring.facade.TaskFacade;
import ru.kolomiec.taskspring.repository.TaskRepository;
import ru.kolomiec.taskspring.services.interfaces.PersonService;
import ru.kolomiec.taskspring.services.interfaces.TaskService;
import ru.kolomiec.taskspring.util.TimeUtil;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ServiceLog
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final PersonService personService;
    private final TaskFacade taskFacade;
    @Override
    public List<Task> getAllTaskByUserId(Long id) {
        // TODO странный метод, скореевсего будет загружен Person и у него можно взять таски.
        return taskRepository.findAllByOwnerId(id).orElseThrow(() -> new EmptyPersonTasksException("you have not tasks"));
    }

    @Override
    public List<Task> getAllTaskByPersonUsername(String username) {
        // TODO странный метод, лучше идти по id он бычно есть под руками.
        List<Task> personTasks = taskRepository.findAllByOwnerUsername(username).get();
        checkListTaskIsEmpty(personTasks);
        return personTasks;
    }

    @Override
    @Transactional
    public void saveTaskToPerson(PersonDetailsSecurityEntity authenticatedPerson, TaskDTO taskDTO) {
        Task newTaskToPerson = taskFacade.fromTaskDTOToTask(taskDTO);
        // TODO в  authenticatedPerson   private final Person person;
        // если нужен объект в контексте транзакции его лучше достать по id
        // хотя хиб увидев person не в транзакции как Owner пропишет связь.
        newTaskToPerson.setOwner(personService.findByUsername(authenticatedPerson.getUsername()));
        // TODO тут легко затереть данные в базе про росте сложности модели из за того что их не таскали на клиента.
        // я бы сразу читал в транзакции и менял. .save все таки в основном синоним persist те при создании нового объекта.
        taskRepository.save(newTaskToPerson);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteTaskOwnedByPerson(PersonDetailsSecurityEntity authenticatedPerson, Long taskId) {
        checkTaskIsOwnerByPerson(authenticatedPerson, taskId);
        taskRepository.deleteById(taskId);
    }

    // TODO tasks due time now
    public List<Task> getAllTasksWhichToDoTimeIsCurrentTime() {
        List<Task> tasksWithCurrentDate = getAllTasksWhichToDoTimeIsCurrentDate();
        List<Task> tasksWithCurrentTime = new ArrayList<>();
        String currentTime = TimeUtil.getCurrentTimeInHoursAndMinutesFormat();
        DateTimeFormatter hoursAndMinutesFormat = DateTimeFormatter.ofPattern("HH:mm");
        if (tasksWithCurrentDate != null) {
            for (Task t : tasksWithCurrentDate) {
                //TODO надо работать с интервалами времени.
                if (hoursAndMinutesFormat.format(t.getToDoTime()).equals(currentTime))
                    tasksWithCurrentTime.add(t);
            }
        }
        return tasksWithCurrentTime;
    }

    public List<Task> getAllTasksWhichToDoTimeIsCurrentDate() {
        String currentDate = TimeUtil.getCurrentDate();
        return taskRepository.findAllTasksWhichToDoTimeIsCurrentDate(currentDate).orElse(null);
    }

    private void checkTaskIsOwnerByPerson(PersonDetailsSecurityEntity authenticatedPerson, Long taskId) {
        taskRepository.findTaskByOwnerUsernameAndTaskId(authenticatedPerson.getUsername(), taskId).orElseThrow(() -> {
            return new PersonHaveNotSuchTaskException("you have not such task");
        });
    }

    private void checkListTaskIsEmpty(List<Task> personTasks) {
        if (personTasks.isEmpty()) throw new EmptyPersonTasksException("you have not tasks");
    }
}
