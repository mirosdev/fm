package com.spring.fm.service.interfaces;

import com.spring.fm.dto.ScheduleItemDto;
import com.spring.fm.dto.ScheduleItemPayload;

import java.util.List;
import java.util.UUID;

public interface IScheduleItemService {
    ScheduleItemDto create(ScheduleItemPayload scheduleItemPayload, UUID userUuid);
    ScheduleItemDto update(ScheduleItemPayload scheduleItemPayload, UUID uuid, UUID userUuid);
    List<ScheduleItemDto> findBetweenTimestamps(String startAt, String endAt, UUID userUuid);
}
