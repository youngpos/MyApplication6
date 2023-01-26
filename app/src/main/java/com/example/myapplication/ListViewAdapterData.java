package com.example.myapplication;

public class ListViewAdapterData {
    private int iStudentNum;
    private String sStudentName;
    private String sAddress;



    public int getiStudentNum(){return iStudentNum;}
    public String getsStudentName(){return sStudentName;}
    public String getsAddress(){return sAddress;}

    public void setiStudentNum(int studentNum){this.iStudentNum= studentNum;}
    public void setsStudentName(String studentName){this.sStudentName = studentName;}
    public void setsAddress(String address){this.sAddress = address;}
}
