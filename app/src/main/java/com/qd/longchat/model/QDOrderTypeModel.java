package com.qd.longchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/26 δΈε1:54
 */
public class QDOrderTypeModel {

    /**
     * type_id : 100
     * type_name : ε¬ε
     * type_code : GG
     */

    @SerializedName("id")
    private String typeId;
    @SerializedName("name")
    private String typeName;
    @SerializedName("code")
    private String typeCode;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
