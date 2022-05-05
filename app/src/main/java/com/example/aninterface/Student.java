package com.example.aninterface;

public class Student {
    private String Id_student,Name,Phone,Late,Date;

    public Student(){
    }
    public Student (String Id,String Name,String Phone,String Late,String Date){
        this.Id_student=Id;
        this.Name=Name;
        this.Phone=Phone;
        this.Late=Late;
        this.Date=Date;

    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLate() {
        return Late;
    }

    public void setLate(String late) {
        Late = late;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId_student() {
        return Id_student;
    }

    public void setId_student(String id_student) {
        Id_student = id_student;
    }

}
