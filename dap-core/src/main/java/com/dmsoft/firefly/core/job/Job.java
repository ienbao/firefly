package com.dmsoft.firefly.core.job;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public class Job {
    String name;

    private Pipe pipe;

    public Job(String name) {
        this.name=name;
        this.pipe = new Pipe();
    }

    public void startPipeInThread(Object o){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              Object result = pipe.start(o);
            }
        });
        thread.start();
    }

    public Pipe getPipe() {
        return pipe;
    }
}
