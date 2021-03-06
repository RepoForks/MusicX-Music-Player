package com.rks.musicx.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rks.musicx.R;
import com.rks.musicx.base.BaseRecyclerViewAdapter;
import com.rks.musicx.data.model.Song;
import com.rks.musicx.interfaces.bitmap;
import com.rks.musicx.interfaces.palette;
import com.rks.musicx.misc.utils.ArtworkUtils;
import com.rks.musicx.misc.utils.Extras;
import com.rks.musicx.misc.utils.SimpleItemTouchHelperCallback;
import com.rks.musicx.misc.widgets.CircleImageView;

import java.util.Collections;

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

public class QueueAdapter extends BaseRecyclerViewAdapter<Song, QueueAdapter.QueueViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {


    private final SimpleItemTouchHelperCallback.OnStartDragListener mDragStartListener;
    private int mLayoutId = R.layout.queue_songlist;
    private int mSelectedItemPosition = -1;

    public QueueAdapter(@NonNull Context context, SimpleItemTouchHelperCallback.OnStartDragListener mDragStartListener) {
        super(context);
        this.mDragStartListener = mDragStartListener;
        notifyDataSetChanged();
    }

    public void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new QueueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QueueViewHolder holder, int position) {
        Song song = getItem(position);
        if (mLayoutId == R.layout.song_list) {
            if (position == mSelectedItemPosition) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);

            }
            holder.SongTitle.setText(song.getTitle());
            holder.SongArtist.setText(song.getArtist());
            ArtworkUtils.ArtworkLoader(getContext(), song.getAlbum(), null, song.getAlbumId(), new palette() {
                @Override
                public void palettework(Palette palette) {
                }
            }, new bitmap() {
                @Override
                public void bitmapwork(Bitmap bitmap) {
                    holder.queueArtwork.setImageBitmap(bitmap);
                }

                @Override
                public void bitmapfailed(Bitmap bitmap) {
                    holder.queueArtwork.setImageBitmap(bitmap);
                }
            });
            holder.menu.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_menu));
            holder.queueArtwork.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
            if (Extras.getInstance().getDarkTheme() || Extras.getInstance().getBlackTheme()) {
                holder.SongTitle.setTextColor(Color.WHITE);
                holder.SongArtist.setTextColor(ContextCompat.getColor(getContext(), R.color.darkthemeTextColor));
                holder.menu.getDrawable().setTint(Color.WHITE);
            } else {
                holder.menu.getDrawable().setTint(Color.WHITE);
                holder.SongTitle.setTextColor(Color.WHITE);
                holder.SongArtist.setTextColor(Color.WHITE);
                if (Extras.getInstance().getPlayingViewTrack()){
                    holder.menu.getDrawable().setTint(ContextCompat.getColor(getContext(), R.color.MaterialGrey));
                    holder.SongTitle.setTextColor(Color.BLACK);
                    holder.SongArtist.setTextColor(Color.DKGRAY);
                }
            }
        }
        if (mLayoutId == R.layout.gridqueue) {
            if (position == mSelectedItemPosition) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);

            }
            ArtworkUtils.ArtworkLoader(getContext(), song.getAlbum(), null, song.getAlbumId(), new palette() {
                @Override
                public void palettework(Palette palette) {
                }
            }, new bitmap() {
                @Override
                public void bitmapwork(Bitmap bitmap) {
                    holder.queueArtworkgrid.setImageBitmap(bitmap);
                }

                @Override
                public void bitmapfailed(Bitmap bitmap) {
                    holder.queueArtworkgrid.setImageBitmap(bitmap);
                }
            });
        }
    }

    public void setSelection(int position) {
        int oldSelection = mSelectedItemPosition;
        mSelectedItemPosition = position;

        if (oldSelection >= 0 && oldSelection < data.size()) {
            notifyItemChanged(oldSelection);
        }

        if (mSelectedItemPosition >= 0 && mSelectedItemPosition < data.size()) {
            notifyItemChanged(mSelectedItemPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= data.size() || toPosition < 0 || toPosition >= data.size()) {
            return false;
        }
        Collections.swap(data, fromPosition, toPosition);

        if (mSelectedItemPosition == fromPosition) {
            mSelectedItemPosition = toPosition;
        } else if (mSelectedItemPosition == toPosition) {
            mSelectedItemPosition = fromPosition;
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public Song getItem(int position) throws ArrayIndexOutOfBoundsException {
        return super.getItem(position);
    }

    public class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {

        View itemView;
        private TextView SongTitle, SongArtist;
        private ImageButton menu;
        private ImageView queueArtworkgrid;
        private CircleImageView queueArtwork;

        public QueueViewHolder(View itemView) {
            super(itemView);

            if (mLayoutId == R.layout.song_list) {
                SongTitle = (TextView) itemView.findViewById(R.id.title);
                SongArtist = (TextView) itemView.findViewById(R.id.artist);
                queueArtwork = (CircleImageView) itemView.findViewById(R.id.artwork);
                menu = (ImageButton) itemView.findViewById(R.id.menu_button);
                this.itemView = itemView;
                itemView.setOnClickListener(this);
                menu.setOnClickListener(this);
            }
            if (mLayoutId == R.layout.gridqueue) {
                queueArtworkgrid = (ImageView) itemView.findViewById(R.id.queue_artwork);
                itemView.findViewById(R.id.item_view).setOnClickListener(this);
                itemView.setOnClickListener(this);
                this.itemView = itemView;
            }
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            triggerOnItemClickListener(position, v);
        }

        @Override
        public void onItemSelected() {
            if (mLayoutId == R.layout.song_list) {
                itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgcolor));
            }
            if (mLayoutId == R.layout.queue_songlist) {
                itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgcolor));
            }

        }

        @Override
        public void onItemClear() {
            if (mLayoutId == R.layout.queue_songlist) {
                itemView.setBackgroundColor(0);
            }
            if (mLayoutId == R.layout.song_list) {
                itemView.setBackgroundColor(0);
            }
        }
    }
}
