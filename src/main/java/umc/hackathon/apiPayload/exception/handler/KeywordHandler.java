package umc.hackathon.apiPayload.exception.handler;

import umc.hackathon.apiPayload.code.BaseErrorCode;
import umc.hackathon.apiPayload.exception.GeneralException;

public class KeywordHandler extends GeneralException {
    public KeywordHandler(BaseErrorCode code) {
        super(code);
    }
}
