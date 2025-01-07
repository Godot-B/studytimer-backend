package umc.hackathon.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.hackathon.apiPayload.code.BaseErrorCode;
import umc.hackathon.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 과목 관련 에러
    SUBJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "SUBJECT4001", "과목이 없습니다."),
//    SUBJECT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SUBJECT4002", "같은 이름의 완료하지 않은 과목이 존재합니다."),

    // 날짜 관련 에러
    DATEPLAN_NOT_FOUND(HttpStatus.BAD_REQUEST, "DATEPLAN4001", "관련된 날짜가 아닙니다."),
    DATEPLAN_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "DATEPLAN4002", "같은 날짜에 계획이 이미 있습니다."),
    GONGSIN_APPEARED(HttpStatus.BAD_REQUEST, "DATEPLAN4003", "당신을 공부의 신으로 임명합니다.(연속 공부 24시간을 넘긴 자)"),

    // 과목 키워드 관련 에러
    KEYWORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "SUBJECT4001", "키워드가 없습니다."),
    KEYWORD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SUBJECT4002", "키워드가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
