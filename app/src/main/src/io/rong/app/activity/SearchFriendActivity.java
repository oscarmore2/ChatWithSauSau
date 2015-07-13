package io.rong.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.adapter.SearchFriendAdapter;
import io.rong.app.model.ApiResult;
import io.rong.app.model.Friends;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.utils.Constants;
import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

/**
 * Created by Bob on 2015/3/26.
 */
public class SearchFriendActivity extends BaseApiActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText mEtSearch;
    private Button mBtSearch;
    private ListView mListSearch;
    private AbstractHttpRequest<Friends> searchHttpRequest;
    private List<ApiResult> mResultList;
    private SearchFriendAdapter adapter;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_search);
        initView();
        initData();

    }


    protected void initView() {
        getSupportActionBar().setTitle(R.string.public_account_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mEtSearch = (EditText) findViewById(R.id.de_ui_search);
        mBtSearch = (Button) findViewById(R.id.de_search);
        mListSearch = (ListView) findViewById(R.id.de_search_list);
        mResultList = new ArrayList<>();
        mDialog = new LoadingDialog(this);

    }

    protected void initData() {
        mBtSearch.setOnClickListener(this);
        mListSearch.setOnItemClickListener(this);
    }

    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {
        Log.e("", "------onCallApiSuccess-user.getCode() == 200)--=======---" );
        if (searchHttpRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
            if (mResultList.size() > 0)
                mResultList.clear();
            if (obj instanceof Friends) {
                final Friends friends = (Friends) obj;

                if (friends.getCode() == 200) {
                    if (friends.getResult().size() > 0) {
                        for (int i = 0; i < friends.getResult().size(); i++) {
                            mResultList.add(friends.getResult().get(i));
                            Log.e("", "------onCallApiSuccess-user.getCode() == 200)-----" + friends.getResult().get(0).getId().toString());
                        }
                            adapter = new SearchFriendAdapter(mResultList, SearchFriendActivity.this);
                            mListSearch.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                    }

                }
            }
        }

    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        if (searchHttpRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
            Log.e("", "------onCallApiSuccess-user.============onCallApiFailure()--");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtSearch)) {
            String userName = mEtSearch.getText().toString();
            if (DemoContext.getInstance() != null) {
                searchHttpRequest = DemoContext.getInstance().getDemoApi().searchUserByUserName(userName, this);

            }

            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.PERSONAL_REQUESTCODE) {
            Intent intent = new Intent();
            this.setResult(Constants.SEARCH_REQUESTCODE, intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent in = new Intent(this, DePersonalDetailActivity.class);

        in.putExtra("SEARCH_USERID", mResultList.get(position).getId());
        in.putExtra("SEARCH_USERNAME", mResultList.get(position).getUsername());
        in.putExtra("SEARCH_PORTRAIT", mResultList.get(position).getPortrait());
        startActivityForResult(in, Constants.SEARCH_REQUESTCODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
