package com.example.tureguideversion1.Model;

public class EventJoinMemberList {

    private String memberName;
    private String memberPhone;
    private String memberImage;


    public EventJoinMemberList(String memberName, String memberPhone, String memberImage) {
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberImage = memberImage;
    }

    public EventJoinMemberList() {
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public String getMemberImage() {
        return memberImage;
    }
}
