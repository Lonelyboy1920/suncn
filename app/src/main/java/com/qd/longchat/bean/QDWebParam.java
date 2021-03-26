package com.qd.longchat.bean;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/17 下午3:52
 */

public class QDWebParam {

    @SerializedName("fun")
    private String funName;
    @JsonAdapter(MyAdapter.class)
    private Object params;
    private String callback;

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public class MyAdapter implements JsonDeserializer {

        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                return json.getAsJsonObject();
            } else {
                return json.getAsString();
            }
        }
    }
}
