package com.sonali.kidtrac;

import java.io.Serializable;

public class ChildrenDetailsBean implements Serializable {
    private String childName, childId;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
