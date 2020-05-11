package com.fengdui.wheel.check;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class MyCheckArgument {

    public void guavaCheck() {
        Preconditions.checkArgument(StringUtils.isNotEmpty("ss"), "input is illegal with empty");
    }

    public void javaCheck() {
        Objects.requireNonNull(null, String.format("aggregateContent is null,contentId=%s", 2));
    }

}
