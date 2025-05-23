package ru.practicum.explorewithme.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.events.dto.RequestMethod;

@Data
@Builder
public class NewCommentDto {
    @NotBlank(groups = {RequestMethod.Create.class})
    @Size(min = 20, max = 7000, groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String text;
}
