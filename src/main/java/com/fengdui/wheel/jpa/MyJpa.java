package com.fengdui.wheel.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyJpa {

    @Query(value = "select count(distinct o.userId ) from User o where o.planId=:planId")
    int countDistinctByPlanId(@Param("planId") long planId);
}
