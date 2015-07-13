package io.rong.app.provider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.rong.app.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.widget.InputView;
import io.rong.imkit.widget.provider.InputProvider;

/**
 * Created by Bob on 15/6/23.
 */
public class InputTestProvider extends InputProvider.MainInputProvider{

    public InputTestProvider(RongContext context) {
        super(context);
    }

    /**
     * 生成切换是需要的图标。
     *
     * @param context   页面上下文。
     * @return  切换时候的图标。
     */
    @Override
    public Drawable obtainSwitchDrawable(Context context) {

        return context.getResources().getDrawable(R.drawable.rc_ic_keyboard);
    }

    /**
     * 生成主输入区 View。
     *
     * @param inflater  生成View所需 LayoutInflater 。
     * @param parent    生成页面父容器。
     * @param inputView 所属 InputView 。
     * @return  主输入区 View。
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, InputView inputView) {

        return null;
    }
    /**
     * 激活时调用。
     *
     * @param context   页面上下文。
     */
    @Override
    public void onActive(Context context) {

    }
    /**
     * 在失去激活时调用。
     *
     * @param context   页面上下文。
     */
    @Override
    public void onInactive(Context context) {

    }
    /**
     * @param context   页面上下文。
     */
    @Override
    public void onSwitch(Context context) {

    }
}
