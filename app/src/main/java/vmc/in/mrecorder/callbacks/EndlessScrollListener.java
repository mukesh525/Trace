package vmc.in.mrecorder.callbacks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


/**
 * Created by mukesh on 10/8/15.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private static int firstVisibleInListview;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        visibleItemCount = layoutManager.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
        if (dy > 0) {
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                Log.i("RecyclerView scrolled: ", "scroll down!");
                Log.v("LAST", "Last Item Wow !");
                onLoadMore();

            }
        } else {
             onLoadUp();
            Log.i("RecyclerView scrolled: ", "scroll up!");
        }


        super.onScrolled(recyclerView, dx, dy);
    }

    public abstract void onLoadMore();
    public abstract void onLoadUp();


}