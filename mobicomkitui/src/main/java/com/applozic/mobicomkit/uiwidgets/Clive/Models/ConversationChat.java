package com.applozic.mobicomkit.uiwidgets.Clive.Models;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by apple on 4/29/17.
 */

public class ConversationChat {

    Map<String,ConversationModel> map=new TreeMap<>();


    public Map<String, ConversationModel> getMap() {
        return map;
    }

    public void setMap(Map<String, ConversationModel> map) {
        this.map = map;
    }
}
