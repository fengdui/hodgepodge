package com.fengdui.framwork.ruleEngine;

import groovy.lang.GroovyClassLoader;

public class Groovy {

    public void getRule(String scriptContent) {
        try {
            GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader());
            Class<?> clazz = groovyClassLoader.parseClass(scriptContent);
            clazz.newInstance();
        } catch (Exception e) {
        }
    }
}
