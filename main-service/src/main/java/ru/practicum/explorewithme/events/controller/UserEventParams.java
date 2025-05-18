package ru.practicum.explorewithme.events.controller;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserEventParams {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private Boolean onlyAvailable;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private EventSortEnum sort;
    private Integer from;
    private Integer size;

    public enum EventSortEnum {
        EVENT_DATE,
        VIEWS
    }
}
