package com.wilson.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wilson
 * @since 2019年03月17日22:58:24
 */
public class EventBus {
    private final String TAG = getClass().getName();
    private static volatile EventBus instance;

    private HashMap<Object, List<SubscribleMethod>> methodCache;

    private Handler handler;

    private ExecutorService cacheExecutorService;

    private EventBus() {
        methodCache = new HashMap<>();
        handler = new Handler();
        cacheExecutorService = Executors.newCachedThreadPool();
    }


    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }


    /**
     * 注册需要监听的class
     *
     * @param object
     */
    public void regist(Object object) {
        synchronized (EventBus.class) {
            List<SubscribleMethod> list = methodCache.get(object);
            if (list == null) {
                list = findSubscribleMethods(object);
                methodCache.put(object, list);
            }
        }
    }

    /**
     * 将class内带有Subscribe注解的方法及参数添加到缓存集合中
     *
     * @param object
     * @return
     */
    private List<SubscribleMethod> findSubscribleMethods(Object object) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class<?> claz = object.getClass();
        //获取当前类的所有方法
        Method[] declaredMethods = claz.getDeclaredMethods();
        while (claz != null) {
            //获取当前类的名字,如果是系统方法，就不在继续找注册的方法，因为我们不能把我们自定义的注解写到系统API里
            String name = claz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            for (Method method : declaredMethods) {
                //获取我们写的Subscribe注解
                Subscribe annotation = method.getAnnotation(Subscribe.class);

                if (annotation != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        Log.e(TAG, "eventbus 只能处理一个参数");
                    }
                    ThreadModel threadModel = annotation.threadMode();

                    SubscribleMethod subscribleMethod = new SubscribleMethod(method, threadModel, parameterTypes[0]);

                    //把注解方法对象添加到list中
                    list.add(subscribleMethod);
                }
            }
            claz = claz.getSuperclass();
        }

        return list;
    }

    /**
     * 发送数据
     *
     * @param param
     */
    public void post(final Object param) {
        Set<Object> set = methodCache.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            List<SubscribleMethod> list = methodCache.get(obj);
            for (final SubscribleMethod method : list) {
                if (method.getParam().isAssignableFrom(param.getClass())) {
                    switch (method.getmThreadModel()) {
                        case Main:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(method, obj, param);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(method, obj, param);
                                    }
                                });
                            }
                            break;

                        case Background:
                            cacheExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(method, obj, param);
                                }
                            });
                            break;
                    }
                }
            }
        }
    }

    public void postStricky(Object param) {
        //TODO
    }

    /**
     * 解除注册
     *
     * @param object
     */
    public void unregister(Object object) {
        if (methodCache.containsKey(object)) {
            methodCache.remove(object);
        }
    }

    /**
     * 调用注册进来的带有Subscribe注解的方法
     *
     * @param subscribleMethod 注解方法对象
     * @param obj              claz
     * @param param            参数
     */
    private void invoke(SubscribleMethod subscribleMethod, Object obj, Object param) {
        Method method1 = subscribleMethod.getmMethod();
        try {
            method1.invoke(obj, param);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
