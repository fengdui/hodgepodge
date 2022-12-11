package main.java.com.hodgepodge.framework.java8;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StreamDemo {

    public void mapToSum() {
        Map<Long, Integer> res = Maps.newHashMap();
        Optional.ofNullable(res).orElse(Maps.newHashMap())
                .values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }
}
