package com.applozic.mobicomkit.uiwidgets.Clive.Models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by apple on 4/29/17.
 */

public class ConversationChat implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<Integer,ConversationModel> map=new TreeMap<>();


    public Map<Integer, ConversationModel> getMap() {
        return map;
    }

    public void setMap(Map<Integer, ConversationModel> map) {
        this.map = map;
    }
}
