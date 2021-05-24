package com.example.demo.myclass;

public class MyTriple {
    private String obj1, rel, obj2;

    public MyTriple(String obj1, String rel, String obj2){
        this.obj1 = obj1;
        this.rel = rel;
        this.obj2 = obj2;
    }

    @Override
    public boolean equals(Object obj) {
        MyTriple s = (MyTriple)obj;
        String curobj1 = this.obj1;
        String currel = this.rel;
        String curobj2 = this.obj2;
        String tarobj1 = s.obj1;
        String tarrel = s.rel;
        String tarobj2 = s.obj2;
        if(curobj1.equals(tarobj1) && currel.equals(tarrel) && curobj2.equals(tarobj2)) {
            return true;
        }else {
            return false;
        }
    }

    public String getObj1() {
        return obj1;
    }

    public String getRel() {
        return rel;
    }

    public String getObj2() {
        return obj2;
    }
}
