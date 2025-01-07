package umc.hackathon.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.hackathon.apiPayload.code.BaseErrorCode;
import umc.hackathon.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}

