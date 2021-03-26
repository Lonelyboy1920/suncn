package com.suncn.ihold_zxztc.view;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;

import org.jetbrains.annotations.NotNull;

public final class CustomLoadMoreView extends BaseLoadMoreView {

    @NotNull
    @Override
    public View getLoadComplete(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.layout.view_erv_nomore);
    }

    @NotNull
    @Override
    public View getLoadEndView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.layout.view_erv_nomore);
    }

    @NotNull
    @Override
    public View getLoadFailView(@NotNull BaseViewHolder baseViewHolder) {
        return  baseViewHolder.getView(R.layout.view_erv_error);
    }

    @NotNull
    @Override
    public View getLoadingView(@NotNull BaseViewHolder baseViewHolder) {
        return null;
    }

    @NotNull
    @Override
    public View getRootView(@NotNull ViewGroup viewGroup) {
        return null;
    }
}