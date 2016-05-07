package test;

import dao.Key;
import dao.Table;
import dao.ViewName;

@Table(name = "tableTest")
public class TestClass {
    @Key
    @ViewName(name = "id")
    private int key;
    private String name;
    private boolean theBoolean;

    public TestClass() {
    }

    public int getKey() {
        return key;
    }

    public void setKey(int a) {
        key = a;
    }

    public String getName() {
        return name;
    }

    public void setName(String a) {
        name = a;
    }

    public boolean isTheBoolean() {
        return theBoolean;
    }

    public void setTheBoolean(boolean a){
        theBoolean = a;
    }
}
