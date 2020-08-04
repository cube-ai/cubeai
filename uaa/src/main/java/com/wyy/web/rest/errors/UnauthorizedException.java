package com.wyy.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnauthorizedException extends AbstractThrowableProblem {

    public UnauthorizedException() {
        super(ErrorConstants.UNAUTHORIZED, "Unauthorized", Status.UNAUTHORIZED);
    }
}
