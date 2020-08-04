package com.wyy.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class VerifyCodeErrorException extends AbstractThrowableProblem {

    public VerifyCodeErrorException() {
        super(ErrorConstants.VERIFY_CODE_ERROR_TYPE, "verify-code-error", Status.BAD_REQUEST);
    }
}
