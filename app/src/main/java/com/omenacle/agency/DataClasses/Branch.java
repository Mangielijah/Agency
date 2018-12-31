package com.omenacle.agency.DataClasses;

public class Branch {

    public String getA_k() {
        return a_k;
    }

    public void setA_k(String a_k) {
        this.a_k = a_k;
    }
    //agency key
    private String a_k;
    //email
    private String e;
    //password
    private  String p;
    //branch Key
    private String k;
    //Branch
    private String b;


    public Branch(){

    }

    public Branch(String a_k, String e, String p, String k, String b) {
        this.a_k = a_k;
        this.e = e;
        this.p = p;
        this.k = k;
        this.b = b;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
