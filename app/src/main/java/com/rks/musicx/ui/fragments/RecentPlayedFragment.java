package com.rks.musicx.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.appthemeengine.Config;
import com.rks.musicx.R;
import com.rks.musicx.base.BaseLoaderFragment;
import com.rks.musicx.base.BaseRecyclerViewAdapter;
import com.rks.musicx.data.model.Song;
import com.rks.musicx.misc.utils.CustomLayoutManager;
import com.rks.musicx.misc.utils.DividerItemDecoration;
import com.rks.musicx.misc.utils.Extras;
import com.rks.musicx.misc.utils.Helper;
import com.rks.musicx.ui.activities.MainActivity;
import com.rks.musicx.ui.adapters.SongListAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

/*
 * Created by Coolalien on 6/28/2016.
 */

/*
 * ©2017 Rajneesh Singh
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RecentPlayedFragment extends BaseLoaderFragment {

    private FastScrollRecyclerView rv;
    private SongListAdapter songListAdapter;
    private Helper helper;
    private int limit;
    private boolean isgridView;
    private Toolbar toolbar;

    private BaseRecyclerViewAdapter.OnItemClickListener onClick = new BaseRecyclerViewAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(int position, View view) {
            switch (view.getId()) {
                case R.id.album_artwork:
                case R.id.item_view:
                    ((MainActivity) getActivity()).onSongSelected(songListAdapter.getSnapshot(), position);
                    break;
                case R.id.menu_button:
                    helper.showMenu(false, recentlyplayed, RecentPlayedFragment.this, RecentPlayedFragment.this, ((MainActivity) getActivity()), position, view, getContext(), songListAdapter);
                    break;
            }
        }
    };

    public RecentPlayedFragment newInstance(int limit, boolean isgridView) {
        Bundle bundle = new Bundle();
        bundle.putInt("Limit", limit);
        bundle.putBoolean("Isgridview", isgridView);
        RecentPlayedFragment recentPlayedFragment = new RecentPlayedFragment();
        recentPlayedFragment.setArguments(bundle);
        return recentPlayedFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int limit = bundle.getInt("Limit");
            boolean isgrid = bundle.getBoolean("Isgridview");
            setLimit(limit);
            setIsgridView(isgrid);
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_recentlyplayed;
    }

    @Override
    protected void ui(View view) {
        rv = (FastScrollRecyclerView) view.findViewById(R.id.recentplayedrv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void funtion() {
        helper = new Helper(getContext());
        String ateKey = Helper.getATEKey(getContext());
        int colorAccent = Config.accentColor(getContext(), ateKey);
        isgridView = true;
        rv.setPopupBgColor(colorAccent);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        background();
    }

    @Override
    protected String filter() {
        return null;
    }

    @Override
    protected String[] argument() {
        return new String[0];
    }

    @Override
    protected String sortOder() {
        return null;
    }

    @Override
    protected void background(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getActivity() != null){
                    songListAdapter = new SongListAdapter(getContext());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (getActivity() == null){
                    return;
                }
                CustomLayoutManager customLayoutManager = new CustomLayoutManager(getContext());
                customLayoutManager.setSmoothScrollbarEnabled(true);
                if (isgridView()) {
                    rv.setLayoutManager(customLayoutManager);
                    rv.addItemDecoration(new DividerItemDecoration(getContext(), 75, false));
                    songListAdapter.setLayoutId(R.layout.song_list);
                    toolbar.setVisibility(View.VISIBLE);
                    loadTracks();
                } else {
                    customLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rv.setLayoutManager(customLayoutManager);
                    rv.setNestedScrollingEnabled(false);
                    rv.setVerticalScrollBarEnabled(false);
                    rv.setHorizontalScrollBarEnabled(false);
                    songListAdapter.setLayoutId(R.layout.recent_list);
                    toolbar.setVisibility(View.GONE);
                    rv.setScrollBarSize(0);
                    loadTracks();
                }
                rv.setAdapter(songListAdapter);
                songListAdapter.setOnItemClickListener(onClick);
            }
        }.execute();
    }

    @Override
    protected boolean isTrack() {
        return false;
    }

    @Override
    protected boolean isRecentPlayed() {
        return true;
    }

    @Override
    protected boolean isFav() {
        return false;
    }

    @Override
    protected int getLimit() {
        return limit;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        Extras.getInstance().getThemevalue(getActivity());
    }

    /*
    Load Tracks
     */
    private void loadTracks() {
        getLoaderManager().initLoader(recentlyplayed, null, this);
    }

    /*
    reload track
    */
    @Override
    public void load() {
        getLoaderManager().restartLoader(recentlyplayed, null, this);
        if (getActivity() == null){
            return;
        }
    }



    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isgridView() {
        return isgridView;
    }

    public void setIsgridView(boolean isgridView) {
        this.isgridView = isgridView;
    }

    @Override
    public void setAdapater(List<Song> data) {
        songListAdapter.addDataList(data);
    }

    @Override
    public void notifyChanges() {
        songListAdapter.notifyDataSetChanged();
    }
}
