package umc.hackathon.apiPayload.exception.handler;

import umc.hackathon.apiPayload.code.BaseErrorCode;
import umc.hackathon.apiPayload.exception.GeneralException;

public class DatePlanHandler extends GeneralException {
    public DatePlanHandler(BaseErrorCode code) {
        super(code);
    }
}
