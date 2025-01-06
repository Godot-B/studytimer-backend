package umc.hackathon.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class DatePlanRequestDTO {

    @Getter
    public static class SetGoalDTO {

        @NotNull
        Integer goalHour;

        @NotNull
        Integer goalMinute;       // 목표 공부 시간 (분 단위)
    }
}
