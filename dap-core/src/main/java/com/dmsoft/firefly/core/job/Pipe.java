package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.Handler;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public class Pipe {
    private List<Handler> handlerList;

    /**
     * constructor
     */
    public Pipe() {
        this.handlerList = Lists.newArrayList();
    }

    /**
     * add handler
     *
     * @param handler handler
     */
    public void add(Handler handler) {
        handlerList.add(handler);
    }

    /**
     * add handler at first
     *
     * @param handler handler
     */
    public void addFirst(Handler handler) {
        handlerList.add(0, handler);
    }

    /**
     * add handler at next
     *
     * @param index   index
     * @param handler handler
     */
    public void addNext(int index, Handler handler) {
        if (index > handlerList.size()) {
            handlerList.add(handler);
        } else {
            handlerList.add(index, handler);
        }
    }


    /**
     * method to start handlers
     *
     * @param o first param
     * @return result
     */
    public Object start(Object o) {
        Object result = o;
        for (int i = 0; i < handlerList.size(); i++) {
            result = handlerList.get(i).active(result);
        }
        return o;
    }
}
