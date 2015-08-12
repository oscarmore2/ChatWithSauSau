package io.rong.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.activity.GroupDetailActivity;
import io.rong.app.activity.MainActivity;
import io.rong.app.adapter.GroupListAdapter;
import io.rong.app.model.ApiResult;
import io.rong.app.model.Groups;
import io.rong.app.model.Status;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.ui.WinToast;
import io.rong.app.utils.Constants;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;

/**
 * Created by Bob on 2015/1/25.
 */
public class GroupListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = GroupListFragment.class.getSimpleName();
    private static final int RESULTCODE = 100;
    private ListView mGroupListView;
    private GroupListAdapter mDemoGroupListAdapter;
    private List<ApiResult> mResultList;
    private AbstractHttpRequest<Groups> mGetAllGroupsRequest;
    private AbstractHttpRequest<Status> mUserRequest;
    private UpdateGroupBroadcastReciver mBroadcastReciver;
    private HashMap<String, Group> mGroupMap;
    private ApiResult result;
    private Handler mHandler;
    private LoadingDialog mDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.de_fr_group_list, container, false);
        mGroupListView = (ListView) view.findViewById(R.id.de_group_list);
        mGroupListView.setItemsCanFocus(false);
        mDialog = new LoadingDialog(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_DMEO_GROUP_MESSAGE);
        if (mBroadcastReciver == null) {
            mBroadcastReciver = new UpdateGroupBroadcastReciver();
        }
        getActivity().registerReceiver(mBroadcastReciver, intentFilter);
        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mGroupListView.setOnItemClickListener(this);
        mHandler = new Handler();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        mResultList = new ArrayList<ApiResult>();
        if (DemoContext.getInstance() != null) {
            mGroupMap = DemoContext.getInstance().getGroupMap();
            mGetAllGroupsRequest = DemoContext.getInstance().getDemoApi().getAllGroups(this);
        }
    }

    private class UpdateGroupBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MainActivity.ACTION_DMEO_GROUP_MESSAGE)) {
                initData();
            }
        }
    }

    @Override
    public void onCallApiSuccess(final AbstractHttpRequest request, Object obj) {
        if (mGetAllGroupsRequest == request) {

            if (obj instanceof Groups) {
                final Groups groups = (Groups) obj;

                if (groups.getCode() == 200) {
                    for (int i = 0; i < groups.getResult().size(); i++) {
                        mResultList.add(groups.getResult().get(i));
                    }
                    mDemoGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
                    mGroupListView.setAdapter(mDemoGroupListAdapter);

                    mDemoGroupListAdapter.setOnItemButtonClick(new GroupListAdapter.OnItemButtonClick() {
                        @Override
                        public boolean onButtonClick(int position, View view) {

                            if (mDemoGroupListAdapter == null)
                                return false;

                            result = mDemoGroupListAdapter.getItem(position);

                            if (result == null)
                                return false;

                            if (mGroupMap == null)
                                return false;

                            if (mGroupMap.containsKey(result.getId())) {
                                RongIM.getInstance().startGroupChat(getActivity(), result.getId(), result.getName());
                            } else {

                                if (DemoContext.getInstance() != null) {
                                    if (mDialog != null && !mDialog.isShowing())
                                        mDialog.show();

                                    mUserRequest = DemoContext.getInstance().getDemoApi().joinGroup(result.getId(), GroupListFragment.this);
                                }

                            }
                            return true;
                        }
                    });

                    mDemoGroupListAdapter.notifyDataSetChanged();

                } else {
                    WinToast.toast(getActivity(), groups.getCode());

                }
            }
        } else if (mUserRequest == request) {
            WinToast.toast(getActivity(), "加入群组成功");

            if (result != null) {
                updateAdapter();
                setGroupMap(result, 1);

                RongIM.getInstance().getRongIMClient().joinGroup(result.getId(), result.getName(), new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        if (mDialog != null)
                            mDialog.dismiss();
                        RongIM.getInstance().startGroupChat(getActivity(), result.getId(), result.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                });


            }

        }
    }

    /**
     * 设置群组信息提供者
     *
     * @param result
     * @param i      0,退出；1 加入
     */
    public static void setGroupMap(ApiResult result, int i) {
        if (DemoContext.getInstance() != null) {
            HashMap<String, Group> groupHashMap = DemoContext.getInstance().getGroupMap();
            if (i == 1) {
                if (result.getPortrait() != null)
                    groupHashMap.put(result.getId(), new Group(result.getId(), result.getName(), Uri.parse(result.getPortrait())));
                else
                    groupHashMap.put(result.getId(), new Group(result.getId(), result.getName(), null));
            } else if (i == 0) {
                groupHashMap.remove(result.getId());
            }
            DemoContext.getInstance().setGroupMap(groupHashMap);

        }

    }


    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        Log.e(TAG, "-----------获取群组列表失败 ----");
        if (mUserRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        if (mDemoGroupListAdapter != null) {
            mDemoGroupListAdapter = null;
        }
        if (mBroadcastReciver != null) {
            getActivity().unregisterReceiver(mBroadcastReciver);
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mResultList != null && position != -1 && position < mResultList.size()) {

            Uri uri = Uri.parse("demo://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationSetting")
                    .appendPath(String.valueOf(Conversation.ConversationType.GROUP))
                    .appendQueryParameter("targetId", mResultList.get(position).getId()).build();

            Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
            intent.putExtra("INTENT_GROUP", mResultList.get(position));

            intent.setData(uri);
            startActivityForResult(intent, RESULTCODE);


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.GROUP_JOIN_REQUESTCODE:
            case Constants.GROUP_QUIT_REQUESTCODE:
//                updateAdapter();
                initData();
                break;
        }


    }

    private void updateAdapter() {
        if (mDemoGroupListAdapter != null) {

            mDemoGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
            mGroupListView.setAdapter(mDemoGroupListAdapter);

            mDemoGroupListAdapter.setOnItemButtonClick(new GroupListAdapter.OnItemButtonClick() {
                @Override
                public boolean onButtonClick(int position, View view) {
                    result = mDemoGroupListAdapter.getItem(position);

                    if (result == null)
                        return true;

                    if (mGroupMap.containsKey(result.getId())) {
                        RongIM.getInstance().startGroupChat(getActivity(), result.getId(), result.getName());
                    } else {

                        if (DemoContext.getInstance() != null) {
                            if (result.getNumber().equals("500")) {
                                WinToast.toast(getActivity(), "群组人数已满");
                                return true;
                            }
                            mUserRequest = DemoContext.getInstance().getDemoApi().joinGroup(result.getId(), GroupListFragment.this);
                        }
                    }
                    return true;
                }
            });
        } else {
            mDemoGroupListAdapter.notifyDataSetChanged();
        }

    }


}
