package com.wilson.eventbus;

import java.lang.reflect.Method;

public class SubscribleMethod {
    private Method mMethod;
    private ThreadModel mThreadModel;
    private Class<?> param;


    public Method getmMethod() {
        return mMethod;
    }

    public void setmMethod(Method mMethod) {
        this.mMethod = mMethod;
    }

    public ThreadModel getmThreadModel() {
        return mThreadModel;
    }

    public void setmThreadModel(ThreadModel mThreadModel) {
        this.mThreadModel = mThreadModel;
    }

    public Class<?> getParam() {
        return param;
    }

    public void setParam(Class<?> param) {
        this.param = param;
    }

    public SubscribleMethod(Method mMethod, ThreadModel mThreadModel, Class<?> param) {
        this.mMethod = mMethod;
        this.mThreadModel = mThreadModel;
        this.param = param;

    }
}
