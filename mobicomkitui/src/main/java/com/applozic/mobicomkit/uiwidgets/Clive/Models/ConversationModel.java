package com.applozic.mobicomkit.uiwidgets.Clive.Models;

import java.io.Serializable;

/**
 * Created by apple on 4/29/17.
 */

public class ConversationModel implements Serializable{
    private static final long serialVersionUID = 2L;


    String chat_session;
    Integer conversationid;
    String repID;
    String customerID;
    String companyID;
    boolean markedAsread;
    boolean rated;

    public String getChat_session() {
        return chat_session;
    }

    public void setChat_session(String chat_session) {
        this.chat_session = chat_session;
    }

    public Integer getConversationid() {
        return conversationid;
    }

    public void setConversationid(Integer conversationid) {
        this.conversationid = conversationid;
    }

    public String getRepID() {
        return repID;
    }

    public void setRepID(String repID) {
        this.repID = repID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public boolean isMarkedAsread() {
        return markedAsread;
    }

    public void setMarkedAsread(boolean markedAsread) {
        this.markedAsread = markedAsread;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}
