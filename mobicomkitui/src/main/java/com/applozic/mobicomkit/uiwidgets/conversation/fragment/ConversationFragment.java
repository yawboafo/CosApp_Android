package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.MultimediaOptionsGridView;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobicomMultimediaPopupAdapter;
import com.applozic.mobicommons.commons.core.utils.LocationUtils;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class ConversationFragment extends MobiComConversationFragment implements SearchListFragment {

    private static final String TAG = "ConversationFragment";
    private MultimediaOptionsGridView popupGrid;
    InputMethodManager inputMethodManager;
    public static final int ATTCHMENT_OPTIONS = 6;

    private List<String> attachmentKey = new ArrayList<>();
    private List<String> attachmentText = new ArrayList<>();
    private List<String> attachmentIcon = new ArrayList<>();

    public ConversationFragment() {
        this.messageIntentClass = MessageIntentService.class;
    }

    public ConversationFragment(Contact contact, Channel channel,Integer conversationId) {
        this.messageIntentClass = MessageIntentService.class;
        this.contact = contact;
        this.channel = channel;
        this.currentConversationId = conversationId;
    }

    public ConversationFragment(Contact contact, Channel channel,Integer conversationId,String searchString) {
        this.messageIntentClass = MessageIntentService.class;
        this.contact = contact;
        this.channel = channel;
        this.currentConversationId = conversationId;
        this.searchString=searchString;

        if (searchString != null) {
            SyncCallService.refreshView=true;
        }
    }

    public void attachLocation(Location mCurrentLocation) {
        String address = LocationUtils.getAddress(getActivity(), mCurrentLocation);
        if (!TextUtils.isEmpty(address)) {
            address = "Address: " + address + "\n";
        } else {
            address = "";
        }
        this.messageEditText.setText(address + "http://maps.google.com/?q=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.title = ApplozicApplication.TITLE;
        this.conversationService = new MobiComConversationService(getActivity());
        hideExtendedSendingOptionLayout = true;

        View view = super.onCreateView(inflater, container, savedInstanceState);
        populateAttachmentOptions();

        if(alCustomizationSettings.isHideAttachmentButton()){

            attachButton.setVisibility(View.GONE);
            messageEditText.setPadding(20,0,0,0);
        }
        sendType.setSelection(1);

        messageEditText.setHint(R.string.enter_mt_message_hint);

        multimediaPopupGrid.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.secret_message_timer_array, R.layout.mobiframework_custom_spinner);

        adapter.setDropDownViewResource(R.layout.mobiframework_custom_spinner);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multimediaPopupGrid.setVisibility(View.GONE);
            }
        });


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (contact != null && !contact.isBlocked() || channel != null) {
                    if (attachmentLayout.getVisibility() == View.VISIBLE) {
                        Toast.makeText(getActivity(), R.string.select_file_count_limit, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(channel !=null){
                    String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                    if(!TextUtils.isEmpty(userId)){
                        Contact withUserContact = appContactService.getContactById(userId);
                        if(withUserContact.isBlocked()){
                            userBlockDialog(false,withUserContact,true);
                        }else {
                            processAttachButtonClick(view);
                        }
                    }else {
                        processAttachButtonClick(view);
                    }
                }else if(contact != null ){
                    if(contact.isBlocked()) {
                        userBlockDialog(false,contact,false);
                    }else {
                        processAttachButtonClick(view);
                    }
                }
            }
        });
        return view;
    }

    @Override
    protected void processMobiTexterUserCheck() {

    }

    public void updateTitle() {
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ApplozicApplication.TITLE);
        super.updateTitle();
    }

    public void hideMultimediaOptionGrid() {
        if (multimediaPopupGrid.getVisibility() == View.VISIBLE) {
            multimediaPopupGrid.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            conversationAdapter.getFilter().filter(null);
        } else {
            conversationAdapter.getFilter().filter(newText);
        }
        return true;
    }


    void processAttachButtonClick(View view){
        MobicomMultimediaPopupAdapter mobicomMultimediaPopupAdapter = new MobicomMultimediaPopupAdapter(getActivity(),attachmentIcon ,attachmentText );
        mobicomMultimediaPopupAdapter.setAlCustomizationSettings(alCustomizationSettings);
        multimediaPopupGrid.setAdapter(mobicomMultimediaPopupAdapter);

        int noOfColumn = (attachmentKey.size()== ATTCHMENT_OPTIONS)?3 :attachmentKey.size();
        multimediaPopupGrid.setNumColumns(noOfColumn);
        multimediaPopupGrid.setVisibility(View.VISIBLE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        MultimediaOptionsGridView itemClickHandler = new MultimediaOptionsGridView(getActivity(), multimediaPopupGrid);
        itemClickHandler.setMultimediaClickListener(attachmentKey);

    }

    private void populateAttachmentOptions() {

        String [] allKeys = getResources().getStringArray(R.array.multimediaOptions_without_price_key);
        String [] allValues = getResources().getStringArray(R.array.multimediaOptions_without_price_text);
        String [] allIcons = getResources().getStringArray(R.array.multimediaOptionIcons_without_price);

        Map<String,Boolean> maps = alCustomizationSettings.getAttachmentOptions();

        for(int index=0; index < allKeys.length ; index++) {

            String  key =allKeys[index];
            if( maps==null || maps.get(key)==null || maps.get(key) ){
                attachmentKey.add(key);
                attachmentText.add(allValues[index]);
                attachmentIcon.add(allIcons[index]);
            }
        }

    }
}