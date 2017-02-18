package com.zhanshow.mylibrary.contact;

/**
 * @author weilinhu
 */

public class ContactEntity {


    private String name;
    /** 联系人号码 **/
    private String number;

    public ContactEntity(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
