
package com.example.listefilm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Rating extends SugarRecord {

    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("Value")
    @Expose
    private String value;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
