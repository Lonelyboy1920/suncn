package com.suncn.ihold_zxztc.bean;

/**
 * EventBus消息事件类
 */
public class EventBusCarrier {
    private int eventType; //区分事件的类型
    private Object object;  //事件的实体类

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
