package com.bbxiaoqu.comm.tool;

/**
 * Created by dzy on 2016/6/27.
 */
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtil {

    public static void setListViewHeightBasedOnChildren(
            View contentlayout, ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) {
            return;
        }

        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        ViewGroup.LayoutParams contentlayout_params = contentlayout
                .getLayoutParams();
        contentlayout_params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 13;
        contentlayout.setLayoutParams(contentlayout_params);
    }

    public static void setListViewHeightBasedOnParent(
            View contentlayout, ExpandableListView listView) {
        int height = contentlayout.getLayoutParams().height;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height - 16;
        listView.setLayoutParams(params);

    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * listAdapter.getCount());
        listView.setLayoutParams(params);
        // listView.requestLayout();
    }
}
