package umc.hackathon.apiPayload.exception.handler;

import umc.hackathon.apiPayload.code.BaseErrorCode;
import umc.hackathon.apiPayload.exception.GeneralException;

public class SubjectHandler extends GeneralException {
    public SubjectHandler(BaseErrorCode code) {
        super(code);
    }
}
