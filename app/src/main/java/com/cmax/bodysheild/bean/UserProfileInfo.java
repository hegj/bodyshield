package com.cmax.bodysheild.bean;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class UserProfileInfo {

    /**
     * id : 10
     * name : hhh
     * password : 123456
     * mobile : 18665661164
     * email :
     * sex : 1
     * age : 10
     * birthday : 1481632368034
     * createTime : 1481632368034
     */

    private int id;
    private String name;
    private String password;
    private String mobile;
    private String email;
    private int sex;
    private int age;
    private long birthday;
    private long createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
