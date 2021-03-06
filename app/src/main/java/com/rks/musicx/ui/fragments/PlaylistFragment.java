package com.rks.musicx.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.Config;
import com.rks.musicx.R;
import com.rks.musicx.base.BaseRecyclerViewAdapter;
import com.rks.musicx.data.loaders.PlaylistLoader;
import com.rks.musicx.data.model.Playlist;
import com.rks.musicx.data.model.Song;
import com.rks.musicx.misc.utils.CustomLayoutManager;
import com.rks.musicx.misc.utils.DividerItemDecoration;
import com.rks.musicx.misc.utils.Extras;
import com.rks.musicx.misc.utils.Helper;
import com.rks.musicx.ui.activities.MainActivity;
import com.rks.musicx.ui.adapters.SongListAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import static com.rks.musicx.misc.utils.Constants.PARAM_PLAYLIST_ID;
import static com.rks.musicx.misc.utils.Constants.PARAM_PLAYLIST_NAME;


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

public class PlaylistFragment extends Fragment {

    private FastScrollRecyclerView rv;
    private Playlist mPlaylist;
    private SongListAdapter playlistViewAdapter;
    private int trackloader = -1;
    private Helper helper;

    private LoaderManager.LoaderCallbacks<List<Song>> playlistLoader = new LoaderCallbacks<List<Song>>() {
        @Override
        public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
            PlaylistLoader playlistLoader = new PlaylistLoader(getContext(), mPlaylist.getId());
            if (id == trackloader) {
                return playlistLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<List<Song>> loader, List<Song> data) {
            if (data == null) {
                return;
            }
            playlistViewAdapter.addDataList(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Song>> loader) {
            loader.reset();
            playlistViewAdapter.notifyDataSetChanged();
        }
    };

    private BaseRecyclerViewAdapter.OnItemClickListener mOnclick = (position, view) -> {
        switch (view.getId()) {
            case R.id.item_view:
                ((MainActivity) getActivity()).onSongSelected(playlistViewAdapter.getSnapshot(), position);
                break;
            case R.id.menu_button:
                helper.showMenu(true, trackloader, playlistLoader, PlaylistFragment.this, ((MainActivity) getActivity()), position, view, getContext(), playlistViewAdapter);
                break;
        }
    };

    public static PlaylistFragment newInstance(Playlist playlist) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_PLAYLIST_ID, playlist.getId());
        args.putString(PARAM_PLAYLIST_NAME, playlist.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        setHasOptionsMenu(true);
        if (args != null) {
            long id = args.getLong(PARAM_PLAYLIST_ID);
            String name = args.getString(PARAM_PLAYLIST_NAME);
            mPlaylist = new Playlist(id, name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        rv = (FastScrollRecyclerView) rootView.findViewById(R.id.playlistrv);
        CustomLayoutManager customLayoutManager = new CustomLayoutManager(getActivity());
        customLayoutManager.setSmoothScrollbarEnabled(true);
        rv.setLayoutManager(customLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), 75, false));
        playlistViewAdapter = new SongListAdapter(getContext());
        playlistViewAdapter.setLayoutId(R.layout.song_list);
        playlistViewAdapter.setOnItemClickListener(mOnclick);
        rv.setAdapter(playlistViewAdapter);
        rv.hasFixedSize();
        String ateKey = Helper.getATEKey(getContext());
        int colorAccent = Config.accentColor(getContext(), ateKey);
        rv.setPopupBgColor(colorAccent);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        helper = new Helper(getContext());
        load();
        return rootView;
    }

    /*
    loader
     */
    private void load() {
        getLoaderManager().initLoader(trackloader, null, playlistLoader);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        Extras.getInstance().getThemevalue(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(trackloader, null, playlistLoader);
    }

}
