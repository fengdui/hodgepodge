package com.fengdui.tool.check;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MyCheckArgument {

    public static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public void guavaCheck() {
        Preconditions.checkArgument(StringUtils.isNotEmpty("ss"), "input is illegal with empty");
    }

    public void javaCheck() {
        Objects.requireNonNull(null, String.format("aggregateContent is null,contentId=%s", 2));
    }

    public <T> void validatorCheck(T obj) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(obj, new Class[0]);
        if (CollectionUtils.isNotEmpty(violations)) {
            throw new RuntimeException(violations.stream().map(ConstraintViolation::getMessage).distinct().collect(Collectors.joining(",")));
        }
    }


}
