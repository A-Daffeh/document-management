package com.adoustar.documentmanagement.event;

import com.adoustar.documentmanagement.entity.UserEntity;
import com.adoustar.documentmanagement.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity user;
    private EventType type;
    private Map<?, ?> data;
}
