package com.alixlp.scan.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 1.继承 RecyclerView.Adapter
 * 2. 绑定 ViewHolder
 * 3. 实现Adapter 的相关方法
 * <p>
 * <p>
 * 使用
 * mRecyclerView = findViewById(R.id.recycler_view);
 * //  RecyclerView
 * LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
 * mRecyclerView.setLayoutManager(linearLayoutManager);
 * <p>
 * // 适配器
 * mBaseAdapter = new BaseRecyclerViewAdapter<MacLogs>(mMacListLogs, R.layout.item, this) {
 *
 * @Override protected void onBindViewHolder(MyViewHolder holder, MacLogs model, int position) {
 * holder.text(R.id.safety, model.getSafety());
 * holder.text(R.id.name, model.getName());
 * holder.image(R.id.icon, getIcon(model.getIcon()));
 * }
 * };
 * mRecyclerView.setAdapter(mBaseAdapter);
 */
/**
 * 使用
 *         mRecyclerView = findViewById(R.id.recycler_view);
 *         //  RecyclerView
 *         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
 *         mRecyclerView.setLayoutManager(linearLayoutManager);
 *
 *         // 适配器
 *         mBaseAdapter = new BaseRecyclerViewAdapter<MacLogs>(mMacListLogs, R.layout.item, this) {
 *             @Override
 *             protected void onBindViewHolder(MyViewHolder holder, MacLogs model, int position) {
 *                 holder.text(R.id.safety, model.getSafety());
 *                 holder.text(R.id.name, model.getName());
 *                 holder.image(R.id.icon, getIcon(model.getIcon()));
 *             }
 *         };
 *         mRecyclerView.setAdapter(mBaseAdapter);
 */

/**
 * @param <T>
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder>
        implements ListAdapter {

    //<editor-fold desc="BaseRecyclerViewAdapter">
    private final int mLayoutId; // 布局文件
    private final List<T> mList; // 数据
    private int mLastPosition = -1;
    private boolean mOpenAnimationEnable = true;
    private AdapterView.OnItemClickListener mListener; //监听事件

    /**
     * 构建方法
     *
     * @param layoutId
     */
    public BaseRecyclerViewAdapter(int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList<>();
        this.mLayoutId = layoutId;

    }

    public BaseRecyclerViewAdapter(Collection<T> collection, @LayoutRes int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList<>(collection);
        this.mLayoutId = layoutId;
    }

    public BaseRecyclerViewAdapter(Collection<T> collection, @LayoutRes int layoutId, AdapterView
            .OnItemClickListener listener) {
        setHasStableIds(false);
        setOnItemClickListener(listener);
        this.mList = new ArrayList<>(collection);
        this.mLayoutId = layoutId;
    }

    //</editor-fold>

    private void addAnimate(MyViewHolder holder, int postion) {
        if (mOpenAnimationEnable && mLastPosition < postion) {
            holder.itemView.setAlpha(0);
            holder.itemView.animate().alpha(1).start();
            mLastPosition = postion;
        }
    }

    //<editor-fold desc="RecyclerAdapter">
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mLayoutId, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        onBindViewHolder(holder, position < mList.size() ? mList.get(position) : null, position);
    }

    protected abstract void onBindViewHolder(MyViewHolder holder, T model, int position);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        addAnimate(holder, holder.getLayoutPosition());
    }

    public void setOpenAnimationEnable(boolean enable) {
        this.mOpenAnimationEnable = enable;
    }

    //</editor-fold>

    //<editor-fold desc="API">
    public BaseRecyclerViewAdapter<T> setOnItemClickListener(AdapterView.OnItemClickListener
                                                                     listener) {
        mListener = listener;
        return this;
    }

    public BaseRecyclerViewAdapter<T> refresh(Collection<T> collection) {
        mList.clear();
        mList.addAll(collection);
        notifyDataSetChanged();
        notifyListDataSetChanged();
        mLastPosition = -1;
        return this;
    }

    public BaseRecyclerViewAdapter<T> loadMore(Collection<T> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
        notifyListDataSetChanged();
        return this;
    }
    //</editor-fold>


    //<editor-fold desc="ListAdapter">
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

//    public boolean hasStableIds() {
//        return false;
//    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyListDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView != null) {
            holder = (MyViewHolder) convertView.getTag();
        } else {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.itemView;
            convertView.setTag(holder);
        }
        onBindViewHolder(holder, position);
        addAnimate(holder, position);
        return convertView;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    //</editor-fold>

}
