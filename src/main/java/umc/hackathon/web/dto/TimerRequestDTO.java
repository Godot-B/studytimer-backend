package umc.hackathon.web.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class TimerRequestDTO {

    @Getter
    public static class StudyLogDTO {

        LocalDateTime startTime;

        LocalDateTime endTime;
    }
}
