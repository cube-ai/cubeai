package com.wyy.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ForbiddenException extends AbstractThrowableProblem {

    public ForbiddenException() {
        super(ErrorConstants.FOBIDDEN, "Forbidden", Status.FORBIDDEN);
    }
}
