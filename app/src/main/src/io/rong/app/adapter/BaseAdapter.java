package io.rong.app.adapter;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected List<T> dataSet;

//    protected Handler handler = new Handler();

    protected Context mContext;

    private ListView mListView;

    public BaseAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public BaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.dataSet = data;
    }

    public Context getContext() {
        return mContext;
    }

    public void addData(T data) {
        this.dataSet.add(data);
    }

    public void addData(Collection<T> data) {
        this.dataSet.addAll(data);
    }

    public void addData(int index, Collection<T> data) {
        this.dataSet.addAll(index, data);
    }

    public void removeData(Collection<T> data) {
        this.dataSet.removeAll(data);
    }

    public void removeAll() {
        if (this.dataSet != null) {
            this.dataSet.clear();
        }
    }

    public void remove(T data) {
        this.dataSet.remove(data);
    }

    public void remove(int position) {
        dataSet.remove(position);
    }

    public List<T> subData(int index, int count) {
        return this.dataSet.subList(index, index + count);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public T getItem(int position) {
        return dataSet.get(position);
    }

    public void setItem(int position, T obj) {
        dataSet.set(position, obj);
    }

    public void addItem(int position, T obj) {
        dataSet.add(position, obj);
    }

    public void addItems(int position, Collection<T> data) {
        this.dataSet.addAll(position, data);
    }


    public void setListView(ListView listView) {
        this.mListView = listView;
    }


    public void notifyDataSetChanged(int position) {

        if (mListView != null) {


            int firstVisiblePosition = mListView.getFirstVisiblePosition();
            int lastVisiblePosition = mListView.getLastVisiblePosition();

            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                getView(position, mListView.getChildAt(position - firstVisiblePosition), mListView);
            }

        }

    }



}
