package com.wyq.firehelper.article.base;

import android.view.View;

import com.wyq.firehelper.article.R;
import com.wyq.firehelper.article.R2;
import com.wyq.firehelper.article.adapter.TvRecyclerViewAdapter;
import com.wyq.firehelper.base.BaseFragment;
import com.wyq.firehelper.base.widget.recyclerview.itemtouchhelper.SimpleItemTouchHelperCallback;

import java.util.Arrays;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public abstract class BaseRecyclerViewFragment extends BaseFragment {

    public abstract String[] listItemsNames();

    public abstract TvRecyclerViewAdapter.OnItemClickListener onListItemClickListener();

    @BindView(R2.id.list_fragment_child_recycler_view)
    public RecyclerView baseRV;

    @Override
    public int attachLayoutRes() {
        return R.layout.base_list_fragment_child_layout;
    }

    @CallSuper
    @Override
    public void initView(View view) {
        TvRecyclerViewAdapter adapter = new TvRecyclerViewAdapter(Arrays.asList(listItemsNames()));
//        if (getItemDecoration() != null) {
//            baseRV.addItemDecoration(getItemDecoration());
//        }
        if (getLayoutManager() != null) {
            baseRV.setLayoutManager(getLayoutManager());
        }
        baseRV.setAdapter(adapter);
        adapter.setOnItemClickListener(onListItemClickListener());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(baseRV);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
    }

}
