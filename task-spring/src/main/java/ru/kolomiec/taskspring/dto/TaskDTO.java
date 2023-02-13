package ru.kolomiec.taskspring.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/*TODO пока можель простая можно обходиться одним классом и мапингом в JSON
Когда она разростется это приведет к необходимости написать нечто нетривиальное для умного мапинга
или таки перейти к DTO там где это реально понадобится.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskDTO {

    @NotBlank(message = "can not be blank")
    @NotNull(message = "can not be null")
    @Size(min = 5, max = 100, message = "should be by 5 to 100 symbols")
    private String taskName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toDoTime;
}
