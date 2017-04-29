package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.api.conversation.database.ConversationDatabaseService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.Clive.RatingDialog;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationListView;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.QuickConversationAdapter;
import com.applozic.mobicomkit.uiwidgets.instruction.InstructionUtil;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devashish on 10/2/15.
 */
public class MobiComQuickConversationFragment extends Fragment implements SearchListFragment ,RatingDialog.OnFragmentInteractionListener{

    public static final String QUICK_CONVERSATION_EVENT = "quick_conversation";
    protected ConversationListView listView = null;
    protected ImageButton fabButton;
    protected TextView emptyTextView;
    protected Button startNewButton;
    protected SwipeRefreshLayout swipeLayout;
    protected int listIndex;
    protected Map<String, Message> latestMessageForEachContact = new HashMap<String, Message>();
    protected List<Message> messageList = new ArrayList<Message>();
    protected QuickConversationAdapter conversationAdapter = null;
    protected boolean loadMore = false;
    protected SyncCallService syncCallService;
    private Long minCreatedAtTime;
    private DownloadConversation downloadConversation;
    private BaseContactService baseContactService;
    private Toolbar toolbar;
    private MessageDatabaseService messageDatabaseService;
    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;
    private ProgressBar progressBar;
    ConversationUIService conversationUIService;
    AlCustomizationSettings alCustomizationSettings;
    String searchString;
    String repID = "";
    String customerID = "";
    String[] menuItems = null;
    Conversation conversationTopicDetails;


    public ConversationListView getListView() {
        return listView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems = new String[]  {"Delete Chat","Mark as Resolved","Rate this chat"};
        String jsonString = FileUtils.loadSettingsJsonFile(getActivity().getApplicationContext());
        if(!TextUtils.isEmpty(jsonString)){
            alCustomizationSettings = (AlCustomizationSettings)GsonUtils.getObjectFromJson(jsonString,AlCustomizationSettings.class);
        }else {
            alCustomizationSettings = new AlCustomizationSettings();
        }
        syncCallService = SyncCallService.getInstance(getActivity());
        conversationAdapter = new QuickConversationAdapter(getActivity(),
                messageList, null);
        conversationAdapter.setAlCustomizationSettings(alCustomizationSettings);
        conversationUIService = new ConversationUIService(getActivity());
        baseContactService = new AppContactService(getActivity());
        messageDatabaseService = new MessageDatabaseService(getActivity());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MobiComUserPreference.getInstance(getActivity()).setDeviceTimeOffset(DateUtils.getTimeDiffFromUtc());
            }
        });
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        setHasOptionsMenu(true);


        try{
            //baseContactService.
          //getResources().getStringArray(R.array.conversation_options_menu);

        }catch (Exception e){e.printStackTrace();}

        //onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            onResume();
        }
        else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View list = inflater.inflate(R.layout.mobicom_message_list, container, false);
        listView = (ConversationListView) list.findViewById(R.id.messageList);
        listView.setBackgroundColor(getResources().getColor(R.color.conversation_list_all_background));
        listView.setScrollToBottomOnSizeChange(Boolean.FALSE);
        /*8toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setClickable(false);**/
        fabButton = (ImageButton) list.findViewById(R.id.fab_start_new);
        loading = true;
        LinearLayout individualMessageSendLayout = (LinearLayout) list.findViewById(R.id.individual_message_send_layout);
        LinearLayout extendedSendingOptionLayout = (LinearLayout) list.findViewById(R.id.extended_sending_option_layout);

        individualMessageSendLayout.setVisibility(View.GONE);
        extendedSendingOptionLayout.setVisibility(View.GONE);

        View spinnerLayout = inflater.inflate(R.layout.mobicom_message_list_header_footer, null);
        progressBar = (ProgressBar) spinnerLayout.findViewById(R.id.load_more_progressbar);
        listView.addFooterView(spinnerLayout);

        //spinner = (ProgressBar) spinnerLayout.findViewById(R.id.spinner);
        emptyTextView = (TextView) list.findViewById(R.id.noConversations);
        emptyTextView.setTextColor(Color.parseColor(alCustomizationSettings.getNoConversationLabelTextColor().trim()));

        // startNewButton = (Button) spinnerLayout.findViewById(R.id.start_new_conversation);

        fabButton.setVisibility(alCustomizationSettings.isStartNewFloatingButton() ? View.VISIBLE : View.GONE);

        swipeLayout = (SwipeRefreshLayout) list.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView.setLongClickable(true);
        registerForContextMenu(listView);

        return list;
    }

    protected View.OnClickListener startNewConversation() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MobiComKitActivityInterface) getActivity()).startContactActivityForResult();
            }
        };
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Conversation conversation = null;


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        if (messageList.size() <= position) {
            return;
        }
        Message message = messageList.get(position);
        menu.setHeaderTitle(R.string.conversation_options);
       // message.


        try {

            conversation = ConversationDatabaseService.getInstance(getActivity()).getConversationByConversationId(message.getConversationId());
            JSONObject jObject  = new JSONObject(conversation.getTopicDetail());
            repID = jObject.getString("value1");
            customerID = jObject.getString("key2");



        }catch (Exception e){

            e.printStackTrace();
        }

       // if(ApplozicApplication.cosappUserID == repID)

          //  menuItems = getResources().getStringArray(R.array.conversation_options_menuRep);
        //else



        boolean isUserPresentInGroup = false;
        boolean isChannelDeleted = false;
        Channel channel = null;
        if (message.getGroupId() != null) {
            channel = ChannelService.getInstance(getActivity()).getChannelByChannelKey(message.getGroupId());
            if(channel != null){
                isChannelDeleted = channel.isDeleted();
            }
            isUserPresentInGroup =  ChannelService.getInstance(getActivity()).processIsUserPresentInChannel(message.getGroupId());
        }

        try {


            conversationTopicDetails = ConversationDatabaseService.getInstance(getActivity()).getConversationByConversationId(BroadcastService.staticconversationID);
        }catch (Exception e){

            e.printStackTrace();
        }



        try {

            String TopicDetails = conversationTopicDetails.getTopicDetail();

            JSONObject jObject  = new JSONObject(TopicDetails);
            repID = jObject.getString("key1");
            customerID = jObject.getString("key2");
            Log.d("USERIDS","Rep ID "+ repID + " customer ID" +ApplozicApplication.cosappUserID);

           /** if(customerID.equals(ApplozicApplication.cosappUserID)){

                Log.d("checkRep","its not a rep chat");
                RatingDialog addRepsDialog=new RatingDialog().newInstance("","");
                showDialog(addRepsDialog,"checkout");
            }else {
                Log.d("checkRep","its a rep chat");

            }**/

        }catch (Exception e){

            e.printStackTrace();
        }


        for (int i = 0; i < menuItems.length; i++) {

            if(menuItems[i].equals("Mark as Resolved") && customerID.equals(ApplozicApplication.cosappUserID)){

              continue;
            }


            if(menuItems[i].equals("Rate this chat") && !customerID.equals(ApplozicApplication.cosappUserID)){

                continue;
            }

          /**  if ((message.getGroupId() == null || channel != null && Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) &&  (menuItems[i].equals("Delete group") ||
                    menuItems[i].equals("Exit group"))) {
                continue;
            }



            if (menuItems[i].equals("Exit group") && (isChannelDeleted  || !isUserPresentInGroup)) {
                continue;
            }

            if (menuItems[i].equals("Delete group") &&  ( isUserPresentInGroup || !isChannelDeleted)) {
                continue;
            }**/

         /**   if (menuItems[i].equals("Rate this Chat") && (customerID  == repID)) {
                continue;
            }


            if (menuItems[i].equals("Mark as Resolved") && (customerID  != repID)) {
                continue;
            }**/

            menu.add(Menu.NONE, i, i, menuItems[i]);

        }



    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);





    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        Log.d("IDS","REP ID : "+repID + "CUS ID :  "+customerID  );

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if (messageList.size() <= position) {
            return true;
        }
        Message message = messageList.get(position);

        Channel channel = null;
        Contact contact = null;

        if (message.getGroupId() != null) {
            channel = ChannelDatabaseService.getInstance(getActivity()).getChannelByChannelKey(message.getGroupId());


        } else {
            contact = baseContactService.getContactById(message.getContactIds());
        }

       // Conversation conversation = ConversationDatabaseService.getInstance(getActivity()).getConversationByConversationId(message.getConversationId());

//       Log.d("condetails",conversation.getTopicDetail().toString() + "conversation Topic id"+conversation.getTopicId()) ;

       // Conversation conversation = new Conversation(message.getConversationId());
        Log.d("Contact Items","Conversation id " +message.getConversationId());


        Log.d("Selected Menu",menuItems[item.getItemId()].toString());

        switch (menuItems[item.getItemId()].toString()){

            case "Delete Chat"     :
                if(channel!=null && channel.isDeleted()){
                    conversationUIService.deleteGroupConversation(channel);
                }else{
                    conversationUIService.deleteConversationThread(contact, channel);
                }
                break;

            case "Mark as Resolved":

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {



                            }
                        });
                alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.setMessage("Are you sure you want to mark Chat as resolved ? ");
                alertDialog.setCancelable(true);
                alertDialog.create().show();

                break;

            case "Rate this chat":

                RatingDialog addRepsDialog = new RatingDialog().newInstance("", "");
                showDialog(addRepsDialog, "checkout");
                break;

            default:
                return super.onContextItemSelected(item);
        }


        //{"Delete Chat","Mark as Resolved","Rate this chat"};
      /**
        switch (item.getItemId()) {


            case 0:
                if(channel!=null && channel.isDeleted()){
                    conversationUIService.deleteGroupConversation(channel);
                }else{
                    conversationUIService.deleteConversationThread(contact, channel);
                }
                break;
            case 1:


                RatingDialog addRepsDialog = new RatingDialog().newInstance("", "");
                showDialog(addRepsDialog, "checkout");

               // conversationUIService.deleteGroupConversation(channel);
                break;
            case 2:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {



                            }
                        });
                alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.setMessage("Are you sure you want to mark Chat as resolved ? ");
                alertDialog.setCancelable(true);
                alertDialog.create().show();

                //conversationUIService.channelLeaveProcess(channel);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        **/


        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

       /** if (!alCustomizationSettings.isStartNewButton()) {
            menu.removeItem(R.id.start_new);
        }else {
            menu.findItem(R.id.start_new).setVisible(true);
        }
        if (!alCustomizationSettings.isStartNewGroup()) {
            menu.removeItem(R.id.conversations);
        }else {
            menu.findItem(R.id.conversations).setVisible(true);
        }
        menu.findItem(R.id.refresh).setVisible(true);
        if(alCustomizationSettings.isProfileOption()){
            menu.findItem(R.id.applozicUserProfile).setVisible(true);
        }
        if(alCustomizationSettings.isMessageSearchOption()){
            menu.findItem(R.id.menu_search).setVisible(true);
        }
        if(alCustomizationSettings.isBroadcastOption()){
            menu.findItem(R.id.broadcast).setVisible(true);
        }**/
    }

    public void addMessage(final Message message) {
        if(getActivity() == null){
            return;
        }
        final Context context = getActivity();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.processContactIds(context);
                Message recentMessage;
                if (message.getGroupId() != null) {
                    recentMessage = latestMessageForEachContact.get(ConversationUIService.GROUP + message.getGroupId());
                } else {
                    recentMessage = latestMessageForEachContact.get(message.getContactIds());
                }

                if (recentMessage != null && message.getCreatedAtTime() >= recentMessage.getCreatedAtTime()) {
                    messageList.remove(recentMessage);
                } else if (recentMessage != null) {
                    return;
                }
                if (message.getGroupId() != null) {
                    latestMessageForEachContact.put(ConversationUIService.GROUP + message.getGroupId(), message);
                } else {
                    latestMessageForEachContact.put(message.getContactIds(), message);
                }
                messageList.add(0, message);
                conversationAdapter.notifyDataSetChanged();
                //listView.smoothScrollToPosition(messageList.size());
                listView.setSelection(0);
                emptyTextView.setVisibility(View.GONE);
                emptyTextView.setText(alCustomizationSettings.getNoConversationLabel());
                // startQNewButton.setVisibility(View.GONE);
            }
        });
    }

    public void updateLastMessage(String keyString, String userId) {
        for (Message message : messageList) {
            if (message.getKeyString() != null && message.getKeyString().equals(keyString)) {
                List<Message> lastMessage;
                if(message.getGroupId() != null){
                    lastMessage =  messageDatabaseService.getLatestMessageByChannelKey(message.getGroupId());
                }else {
                    lastMessage  = messageDatabaseService.getLatestMessage(message.getContactIds());
                }
                if (lastMessage.isEmpty()) {
                    removeConversation(message, userId);
                } else {
                    deleteMessage(lastMessage.get(0), userId);
                }
                break;
            }
        }
    }

    public String getLatestContact() {
        if (messageList != null && !messageList.isEmpty()) {
            Message message = messageList.get(0);
            return message.getTo();
        }
        return null;
    }

    public void updateChannelName() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    conversationAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    public void deleteMessage(final Message message, final String userId) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message recentMessage;
                if (message.getGroupId() != null) {
                    recentMessage = latestMessageForEachContact.get(ConversationUIService.GROUP + message.getGroupId());
                } else {
                    recentMessage = latestMessageForEachContact.get(message.getContactIds());
                }
                if (recentMessage != null && message.getCreatedAtTime() <= recentMessage.getCreatedAtTime()) {
                    if (message.getGroupId() != null) {
                        latestMessageForEachContact.put(ConversationUIService.GROUP + message.getGroupId(), message);
                    } else {
                        latestMessageForEachContact.put(message.getContactIds(), message);
                    }

                    messageList.set(messageList.indexOf(recentMessage), message);

                    conversationAdapter.notifyDataSetChanged();
                    if (messageList.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        // startNewButton.setVisibility(applozicSetting.isStartNewButtonVisible() ? View.VISIBLE : View.GONE);
                    }
                }
            }
        });
    }

    public void updateLatestMessage(Message message, String userId) {
        deleteMessage(message, userId);
    }

    public void removeConversation(final Message message, final String userId) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getGroupId() != null && message.getGroupId() != 0) {
                    latestMessageForEachContact.remove(ConversationUIService.GROUP + message.getGroupId());
                } else {
                    latestMessageForEachContact.remove(message.getContactIds());
                }
                messageList.remove(message);
                conversationAdapter.notifyDataSetChanged();
                checkForEmptyConversations();
            }
        });
    }

    public void removeConversation(final Contact contact, final Integer channelKey, String response) {

        if ("success".equals(response)) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Message message = null;
                    if (channelKey != null && channelKey != 0) {
                        message = latestMessageForEachContact.get(ConversationUIService.GROUP + channelKey);
                    } else {
                        message = latestMessageForEachContact.get(contact.getUserId());
                    }
                    if(message == null){
                        return;
                    }
                    messageList.remove(message);
                    if (channelKey != null && channelKey != 0) {
                        latestMessageForEachContact.remove(ConversationUIService.GROUP + channelKey);
                    } else {
                        latestMessageForEachContact.remove(contact.getUserId());
                    }
                    conversationAdapter.notifyDataSetChanged();
                    checkForEmptyConversations();
                }
            });
        } else {
            if (!Utils.isInternetAvailable(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.you_need_network_access_for_delete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.delete_conversation_failed), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void checkForEmptyConversations() {
        boolean isLodingConversation = (downloadConversation != null && downloadConversation.getStatus() == AsyncTask.Status.RUNNING);
        if (latestMessageForEachContact.isEmpty() && !isLodingConversation) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(alCustomizationSettings.getNoConversationLabel());
            //startNewButton.setVisibility(applozicSetting.isStartNewButtonVisible() ? View.VISIBLE : View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            // startNewButton.setVisibility(View.GONE);
        }
    }

    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public void onPause() {
        super.onPause();
        listIndex = listView.getFirstVisiblePosition();
        BroadcastService.currentUserId = null;
        if(listView != null){
            BroadcastService.lastIndexForChats = listView.getFirstVisiblePosition();
        }
        if(conversationAdapter != null){
            conversationAdapter.contactImageLoader.setPauseWork(false);
            conversationAdapter.channelImageLoader.setPauseWork(false);
        }
    }

    @Override
    public void onResume() {
        //Assigning to avoid notification in case if quick conversation fragment is opened....
        /*8toolbar.setTitle(ApplozicApplication.TITLE);
        toolbar.setSubtitle("");**/
        BroadcastService.selectMobiComKitAll();
        super.onResume();
        latestMessageForEachContact.clear();
        messageList.clear();
        if (listView != null) {
            if (listView.getCount() > BroadcastService.lastIndexForChats) {
                listView.setSelection(BroadcastService.lastIndexForChats);
            } else {
                listView.setSelection(0);
            }
        }
        downloadConversations(false,searchString);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                SyncMessages syncMessages = new SyncMessages();
                syncMessages.execute();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //FlurryAgent.logEvent(QUICK_CONVERSATION_EVENT);
        listView.setAdapter(conversationAdapter);
        // startNewButton.setOnClickListener(startNewConversation());
        fabButton.setOnClickListener(startNewConversation());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (conversationAdapter != null) {
                    conversationAdapter.contactImageLoader.setPauseWork(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING);
                    conversationAdapter.channelImageLoader.setPauseWork(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        if (!messageList.isEmpty()) {
                            loading = false;
                        }
                        previousTotalItemCount = totalItemCount;
                        currentPage++;
                    }
                }
                if ((totalItemCount - visibleItemCount) == 0) {
                    return;
                }
                if (totalItemCount <= 5) {
                    return;
                }
                if (loadMore && !loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    DownloadConversation downloadConversation = new DownloadConversation(view, false, firstVisibleItem, visibleItemCount, totalItemCount);
                    AsyncTaskCompat.executeParallel(downloadConversation);
                    loading = true;
                }
            }
        });
    }


    public void downloadConversations() {
        downloadConversations(false,null);
    }

    public void downloadConversations(boolean showInstruction,String searchString) {
        minCreatedAtTime = null;
        downloadConversation = new DownloadConversation(listView, true, 1, 0, 0, showInstruction,searchString);
        AsyncTaskCompat.executeParallel(downloadConversation);
        if(conversationAdapter != null){
            conversationAdapter.searchString = searchString;
        }
    }

    public void updateLastSeenStatus(final String userId) {
        if(getActivity() == null){
            return;
        }
        if (!alCustomizationSettings.isOnlineStatusMasterList()) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = latestMessageForEachContact.get(userId);
                    if (message != null) {
                        int index = messageList.indexOf(message);
                        View view = listView.getChildAt(index - listView.getFirstVisiblePosition());
                        if (view != null) {
                            TextView onlineTextView = (TextView) view.findViewById(R.id.onlineTextView);
                            Contact contact = baseContactService.getContactById(userId);
                            onlineTextView.setVisibility(contact != null && contact.isOnline() ? View.VISIBLE : View.GONE);
                        }
                    }
                } catch (Exception ex) {
                    Log.w("AL", "Exception while updating online status.");
                }
            }
        });

    }

    public void updateConversationRead(final String currentId, final boolean isGroup) {
        if (getActivity() == null) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = null;
                    if (isGroup) {
                        message = latestMessageForEachContact.get(ConversationUIService.GROUP + currentId);
                    } else {
                        message = latestMessageForEachContact.get(currentId);
                    }

                    if (message != null) {
                        int index = messageList.indexOf(message);
                        if (index != -1) {
                            View view = listView.getChildAt(index - listView.getFirstVisiblePosition());
                            if (view != null) {
                                TextView unreadCountTextView = (TextView) view.findViewById(R.id.unreadSmsCount);
                                unreadCountTextView.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.w("AL", "Exception while updating Unread count...");
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public class DownloadConversation extends AsyncTask<Void, Integer, Long> {

        private AbsListView view;
        private int firstVisibleItem;
        private int amountVisible;
        private int totalItems;
        private boolean initial;
        private boolean showInstruction;
        private List<Message> nextMessageList = new ArrayList<Message>();
        private Context context;
        private boolean loadMoreMessages;
        private String searchString;

        public DownloadConversation(AbsListView view, boolean initial, int firstVisibleItem, int amountVisible, int totalItems, boolean showInstruction,String searchString) {
            this.context = getActivity();
            this.view = view;
            this.initial = initial;
            this.firstVisibleItem = firstVisibleItem;
            this.amountVisible = amountVisible;
            this.totalItems = totalItems;
            this.showInstruction = showInstruction;
            this.searchString = searchString;
        }

        public DownloadConversation(AbsListView view, boolean initial, int firstVisibleItem, int amountVisible, int totalItems) {
            this(view, initial, firstVisibleItem, amountVisible, totalItems, false,null);
            loadMoreMessages = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(progressBar != null && loadMoreMessages){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                swipeLayout.setEnabled(true);
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(true);
                    }
                });
            }
        }

        protected Long doInBackground(Void... voids) {
            if (initial) {
                nextMessageList = syncCallService.getLatestMessagesGroupByPeople(searchString);
                if (!nextMessageList.isEmpty()) {
                    minCreatedAtTime = nextMessageList.get(nextMessageList.size() - 1).getCreatedAtTime();
                }
            } else if (!messageList.isEmpty()) {
                listIndex = firstVisibleItem;
                Long createdAt = messageList.isEmpty() ? null : messageList.get(messageList.size() - 1).getCreatedAtTime();
                minCreatedAtTime = (minCreatedAtTime == null ? createdAt : Math.min(minCreatedAtTime, createdAt));
                nextMessageList = syncCallService.getLatestMessagesGroupByPeople(minCreatedAtTime,searchString);
            }

            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            if(!loadMoreMessages){
                swipeLayout.setEnabled(true);
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                });
            }

            if(!TextUtils.isEmpty(searchString)){
                messageList.clear();
                latestMessageForEachContact.clear();

            }

            for (Message currentMessage : nextMessageList) {
                if (currentMessage.isSentToMany()) {
                    continue;
                }
                Message recentSms;
                if (currentMessage.getGroupId() != null) {
                    recentSms = latestMessageForEachContact.get(ConversationUIService.GROUP + currentMessage.getGroupId());
                } else {
                    recentSms = latestMessageForEachContact.get(currentMessage.getContactIds());
                }

                if (recentSms != null) {
                    if (currentMessage.getCreatedAtTime() >= recentSms.getCreatedAtTime()) {
                        if (currentMessage.getGroupId() != null) {
                            latestMessageForEachContact.put(ConversationUIService.GROUP + currentMessage.getGroupId(), currentMessage);
                        } else {
                            latestMessageForEachContact.put(currentMessage.getContactIds(), currentMessage);
                        }

                        Log.d("Current message", "message" + currentMessage);
                        messageList.remove(recentSms);
                        messageList.add(currentMessage);
                    }
                } else {
                    if (currentMessage.getGroupId() != null) {
                        latestMessageForEachContact.put(ConversationUIService.GROUP + currentMessage.getGroupId(), currentMessage);
                    } else {
                        latestMessageForEachContact.put(currentMessage.getContactIds(), currentMessage);
                    }

                    messageList.add(currentMessage);
                }
            }
            if(progressBar != null && loadMoreMessages){
                progressBar.setVisibility(View.GONE);
            }
            conversationAdapter.notifyDataSetChanged();
            if (initial) {
                emptyTextView.setVisibility(messageList.isEmpty() ? View.VISIBLE : View.GONE);
                if(!TextUtils.isEmpty(searchString) && messageList.isEmpty()){
                    emptyTextView.setText(alCustomizationSettings.getNoSearchFoundForChatMessages());
                }else if(TextUtils.isEmpty(searchString) && messageList.isEmpty()) {
                    emptyTextView.setText(alCustomizationSettings.getNoConversationLabel());
                }
                if (!messageList.isEmpty()) {
                    if (listView != null) {
                        if (listView.getCount() > BroadcastService.lastIndexForChats) {
                            listView.setSelection(BroadcastService.lastIndexForChats);
                            BroadcastService.lastIndexForChats = 0;
                        } else {
                            listView.setSelection(0);
                        }
                    }
                }
            } else {
                if(!loadMoreMessages){
                    listView.setSelection(firstVisibleItem);
                }
            }
            /*if (isAdded()) {
                //Utils.isNetworkAvailable(getActivity(), errorMessage);
                if (!Utils.isInternetAvailable(getActivity())) {
                    String errorMessage = getResources().getString(R.string.internet_connection_not_available);
                    ((MobiComKitActivityInterface) getActivity()).showErrorMessageView(errorMessage);
                }
            }*/

            if (context != null && showInstruction) {
                InstructionUtil.showInstruction(context, R.string.instruction_open_conversation_thread, MobiComKitActivityInterface.INSTRUCTION_DELAY, BroadcastService.INTENT_ACTIONS.INSTRUCTION.toString());
            }
            if(!nextMessageList.isEmpty()){
                loadMore = true;
            }
        }
    }
    private class SyncMessages extends AsyncTask<Void, Integer, Long>{
        SyncMessages(){
        }
        @Override
        protected Long doInBackground(Void... params) {
            syncCallService.syncMessages(null);
            return 1l;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.searchString = newText;
        if (TextUtils.isEmpty(newText)) {
            downloadConversations(false,null);
        } else {
            downloadConversations(false,newText);
        }
        return true;
    }

    void showDialog(DialogFragment dialogFragment, String tag) {


        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev =  getActivity().getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {

            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, tag);
    }
}