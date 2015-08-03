package io.rong.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.adapter.BlackMultiChoiceAdapter;
import io.rong.app.adapter.FriendListAdapter;
import io.rong.app.model.Friend;
import io.rong.app.ui.DePinnedHeaderListView;
import io.rong.app.ui.DeSwitchGroup;
import io.rong.app.ui.DeSwitchItemView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Bob on 2015/3/26.
 */
@SuppressWarnings("ALL")
public class BlackListFragment extends Fragment implements DeSwitchGroup.ItemHander, View.OnClickListener, TextWatcher, FriendListAdapter.OnFilterFinished, AdapterView.OnItemClickListener {

    private static final String TAG = BlackListFragment.class.getSimpleName();
    protected BlackMultiChoiceAdapter mAdapter;
    private DePinnedHeaderListView mListView;
    private DeSwitchGroup mSwitchGroup;
    /**
     * 好友list
     */
    protected List<Friend> mFriendsList;
    protected List<UserInfo> mUserInfoList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.de_list_address, null);

        mListView = (DePinnedHeaderListView) view.findViewById(R.id.de_ui_friend_list);
        mSwitchGroup = (DeSwitchGroup) view.findViewById(R.id.de_ui_friend_message);

        mListView.setPinnedHeaderView(LayoutInflater.from(this.getActivity()).inflate(R.layout.de_item_friend_index,
                mListView, false));

        mListView.setFastScrollEnabled(false);

        mListView.setOnItemClickListener(this);
        mSwitchGroup.setItemHander(this);

        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        ArrayList<UserInfo> userInfos = null;

        //获取好友列表
        if (RongIM.getInstance().getRongIMClient() != null) {
            RongIM.getInstance().getRongIMClient().getBlacklist(new RongIMClient.GetBlacklistCallback() {
                @Override
                public void onSuccess(String[] userIds) {
                    mUserInfoList = DemoContext.getInstance().getUserInfoList(userIds);

                    mFriendsList = new ArrayList<Friend>();

                    if (mUserInfoList != null) {
                        for (UserInfo userInfo : mUserInfoList) {
                            Friend friend = new Friend();
                            friend.setNickname(userInfo.getName());
                            friend.setPortrait(userInfo.getPortraitUri() + "");
                            friend.setUserId(userInfo.getUserId());
                            mFriendsList.add(friend);
                        }
                    }
                    mFriendsList = sortFriends(mFriendsList);
                    mAdapter = new BlackMultiChoiceAdapter(getActivity(), mFriendsList);
                    mListView.setAdapter(mAdapter);
                    fillData();

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
        }


        super.onViewCreated(view, savedInstanceState);
    }


    private final void fillData() {

        mAdapter.setAdapterData(mFriendsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v instanceof DeSwitchItemView) {
            CharSequence tag = ((DeSwitchItemView) v).getText();

            if (mAdapter != null && mAdapter.getSectionIndexer() != null) {
                Object[] sections = mAdapter.getSectionIndexer().getSections();
                int size = sections.length;

                for (int i = 0; i < size; i++) {
                    if (tag.equals(sections[i])) {
                        int index = mAdapter.getPositionForSection(i);
                        mListView.setSelection(index + mListView.getHeaderViewsCount());
                        break;
                    }
                }
            }
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object tagObj = view.getTag();
        if (tagObj != null && tagObj instanceof BlackMultiChoiceAdapter.ViewHolder) {
            final BlackMultiChoiceAdapter.ViewHolder viewHolder = (BlackMultiChoiceAdapter.ViewHolder) tagObj;
            mAdapter.onItemClick(viewHolder.friend.getUserId());

            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("从黑名单中删除");
            dialog.setItems(new String[]{"删除", "取消"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            if (RongIM.getInstance()!=null)
                                RongIM.getInstance().getRongIMClient().removeFromBlacklist(viewHolder.friend.getUserId(), new RongIMClient.RemoveFromBlacklistCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e(TAG, "----onSuccess--");
                                        if(mFriendsList!= null) {
                                            Friend friend = new Friend(viewHolder.friend.getUserId(),viewHolder.friend.getNickname(),viewHolder.friend.getPortrait());
                                            mFriendsList.remove(friend);
                                            fillData();
                                        }
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        Log.e(TAG,"----onError--");
                                    }
                                });

                            break;
                        case 1:

                            break;
                    }
                }
            });
            dialog.show();
            return;
        }

    }

    @Override
    public void onDestroyView() {
        if (mAdapter != null) {
            mAdapter.destroy();
            mAdapter = null;
        }
        super.onDestroyView();
    }

    /**
     * 好友数据排序
     *
     * @param friends 好友 List
     * @return 排序后的好友 List
     */
    private ArrayList<Friend> sortFriends(List<Friend> friends) {

        String[] searchLetters = getResources().getStringArray(R.array.de_search_letters);

        HashMap<String, ArrayList<Friend>> userMap = new HashMap<String, ArrayList<Friend>>();

        ArrayList<Friend> friendsArrayList = new ArrayList<Friend>();

        for (Friend friend : friends) {

            String letter = new String(new char[]{friend.getSearchKey()});

            if (userMap.containsKey(letter)) {
                ArrayList<Friend> friendList = userMap.get(letter);
                friendList.add(friend);
            } else {
                ArrayList<Friend> friendList = new ArrayList<Friend>();
                friendList.add(friend);
                userMap.put(letter, friendList);
            }

        }

        for (int i = 0; i < searchLetters.length; i++) {
            String letter = searchLetters[i];
            ArrayList<Friend> fArrayList = userMap.get(letter);
            if (fArrayList != null) {
                friendsArrayList.addAll(fArrayList);
            }
        }

        return friendsArrayList;
    }

    @Override
    public void onFilterFinished() {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


}
