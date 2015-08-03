package io.rong.app.fragment.setting;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.rong.app.R;
import io.rong.imkit.fragment.DispatchResultFragment;

/**
 * Created by Bob on 15/7/30.
 */
public class RongSettingFragment extends DispatchResultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rong_setting,container,false);

        return view;
    }

    @Override
    protected void initFragment(Uri uri) {

    }
}
