package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.Handler;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public class Pipe {
    private List<Handler> handlerList;

    public Pipe() {
        this.handlerList = Lists.newArrayList();
    }

    public void add(Handler handler) {
        handlerList.add(handler);
    }

    public void addFirst(Handler handler) {
        handlerList.add(0, handler);
    }

    public void addNext(int index, Handler handler) {
        if (index > handlerList.size()) {
            handlerList.add(handler);
        } else {
            handlerList.add(index, handler);
        }
    }


    public Object start(Object o) {
        Object result = o;
        for (int i = 0; i < handlerList.size(); i++) {
            result = handlerList.get(i).active(result);
        }
        return o;
    }
}
