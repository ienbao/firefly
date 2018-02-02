package com.dmsoft.firefly.core.job;

import java.util.concurrent.Callable;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public class Job {
    private String name;

    private Callable c;

    private Pipe pipe;

    /**
     * constructor
     *
     * @param name name
     */
    public Job(String name) {
        this.name = name;
        this.pipe = new Pipe();
    }

    public Callable getCallable() {
        return c;
    }

    public void setCallable(Callable c) {
        this.c = c;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
