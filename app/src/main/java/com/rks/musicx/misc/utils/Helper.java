package com.rks.musicx.misc.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.Config;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rks.musicx.R;
import com.rks.musicx.data.model.Album;
import com.rks.musicx.data.model.Artist;
import com.rks.musicx.data.model.FolderModel;
import com.rks.musicx.data.model.Playlist;
import com.rks.musicx.data.model.Song;
import com.rks.musicx.data.network.LyricsData;
import com.rks.musicx.database.FavHelper;
import com.rks.musicx.interfaces.playlistPicked;
import com.rks.musicx.ui.activities.MainActivity;
import com.rks.musicx.ui.adapters.SongListAdapter;
import com.rks.musicx.ui.fragments.AlbumFragment;
import com.rks.musicx.ui.fragments.ArtistFragment;
import com.rks.musicx.ui.fragments.FavFragment;
import com.rks.musicx.ui.fragments.PlayListPicker;
import com.rks.musicx.ui.fragments.TagEditorFragment;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static com.rks.musicx.R.string.file_size;
import static com.rks.musicx.misc.utils.Constants.BlackTheme;
import static com.rks.musicx.misc.utils.Constants.DarkTheme;
import static com.rks.musicx.misc.utils.Constants.LightTheme;
import static com.rks.musicx.misc.utils.Constants.SONG_ALBUM;
import static com.rks.musicx.misc.utils.Constants.SONG_ARTIST;
import static com.rks.musicx.misc.utils.Constants.SONG_TITLE;
import static com.rks.musicx.misc.utils.Constants.SONG_TRACK_NUMBER;
import static com.rks.musicx.misc.utils.Constants.SONG_YEAR;

/*
 * Created by Coolalien on 24/03/2017.
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

public class Helper {

    private Context context;
    private ValueAnimator colorAnimation;

    public Helper(Context context) {
        this.context = context;
    }

    public static void setRingTone(Context context, String path) {
        if (!permissionManager.isWriteSettingsGranted(context)) {
            setRingtone(context, path);
        } else {
            Log.d("Helper", "Write Permission Not Granted on mashmallow+");
            setRingtone(context, path);
        }
    }

    /**
     * Set Ringtone
     *
     * @param context
     * @param path
     */
    private static void setRingtone(Context context, String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaColumns.DATA, file.getAbsolutePath());
        String filterName = path.substring(path.lastIndexOf("/") + 1);
        contentValues.put(MediaColumns.TITLE, filterName);
        contentValues.put(MediaColumns.MIME_TYPE, "audio/mp3");
        contentValues.put(MediaColumns.SIZE, file.length());
        contentValues.put(Media.IS_RINGTONE, true);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            String id = cursor.getString(0);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            context.getContentResolver().update(uri, contentValues, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
            Uri newuri = ContentUris.withAppendedId(uri, Long.valueOf(id));
            try {
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newuri);
                Toast.makeText(context, "Ringtone set", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            cursor.close();
        }
    }

    /**
     * Share Music
     *
     * @param path
     * @param context
     */
    public static void shareMusic(String path, Context context) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
            } else {
                Log.d("Helper", "path not found");
            }
        } else {
            Log.d("Helper", "path not found");
        }
    }

    /**
     * Song Details
     *
     * @param context
     * @param title
     * @param album
     * @param artist
     * @param trackno
     * @param data
     */
    public static void detailMusic(Context context, String title, String album, String artist,
                                   int trackno, String data) {
        if (data != null) {
            File file = new File(data);
            if (file.exists()) {
                float cal = (file.length() / 1024);
                String content = context.getText(R.string.song_Name) +
                        title +
                        "\n\n" +
                        context.getText(R.string.album_name) +
                        album +
                        "\n\n" +
                        context.getString(R.string.artist_name) +
                        artist +
                        "\n\n" +
                        context.getText(R.string.trackno) +
                        trackno +
                        "\n\n" +
                        context.getText(R.string.file_path) +
                        data +
                        "\n\n" +
                        context.getText(file_size) +
                        String.valueOf(String.format("%.2f", cal / 1024)) +
                        " MB";
                new MaterialDialog.Builder(context)
                        .title(R.string.action_details)
                        .content(content)
                        .positiveText(R.string.okay)
                        .onPositive((materialDialog, dialogAction) -> materialDialog.dismiss())
                        .show();
            } else {
                Toast.makeText(context, "File path not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("Helper", "path not found");
        }

    }


    /**
     * Return alpha color
     *
     * @param color
     * @param ratio
     * @return
     */
    public static int getColorWithAplha(int color, float ratio) {
        int transColor;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        transColor = Color.argb(alpha, r, g, b);
        return transColor;
    }

    /**
     * Edit Song Tags
     *
     * @param context
     * @param song
     * @return
     */
    public static boolean editSongTags(Context context, Song song) {
        File f = new File(song.getmSongPath());
        if (f.exists()) {
            AudioFile audioFile = null;
            try {
                audioFile = AudioFileIO.read(f);
            } catch (CannotReadException | InvalidAudioFrameException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            }
            TagOptionSingleton.getInstance().setAndroid(true);
            Tag tag = null;
            if (audioFile != null) {
                tag = audioFile.getTag();
            }
            if (tag == null) {
                tag = new ID3v24Tag();
            }
            try {
                String year = song.getYear() != null ? song.getYear() : SONG_YEAR;
                String title = song.getTitle() != null ? song.getTitle() : SONG_TITLE;
                String album = song.getAlbum() != null ? song.getArtist() : SONG_ALBUM;
                String artist = song.getArtist() != null ? song.getArtist() : SONG_ARTIST;
                String track = song.getTrackNumber() != 0 ? String.valueOf(song.getTrackNumber()) : SONG_TRACK_NUMBER;
                String lyrics = song.getLyrics() != null ? song.getLyrics() : "No Lyrics";
                tag.deleteField(FieldKey.LYRICS);
                tag.setField(FieldKey.TITLE, title);
                tag.setField(FieldKey.YEAR,  year);
                tag.setField(FieldKey.ARTIST, artist);
                tag.setField(FieldKey.ALBUM, album);
                tag.setField(FieldKey.TRACK, track);
                tag.setField(FieldKey.LYRICS, lyrics);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.TITLE, title);
                values.put(MediaStore.Audio.Media.YEAR, year);
                values.put(MediaStore.Audio.Media.ARTIST, artist);
                values.put(MediaStore.Audio.Media.TRACK, track);
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{BaseColumns._ID,
                                MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.ALBUM_KEY,
                                MediaStore.Audio.AlbumColumns.ARTIST}, MediaStore.Audio.AlbumColumns.ALBUM + " = ?",
                        new String[]{album}, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    values.put(MediaStore.Audio.Media.ALBUM_ID, id);
                } else {
                    values.put(MediaStore.Audio.Media.ALBUM, album);
                }
                if (cursor != null) {
                    cursor.close();
                }
                if (values.size() > 0) {
                    context.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media._ID + "=" + song.getId(), null);
                }else {
                    return false;
                }
            } catch (FieldDataInvalidException e) {
                e.printStackTrace();

            }
            try {
                if (audioFile != null) {
                    audioFile.commit();
                    audioFile.setTag(tag);
                }
                return true;
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Get Inbuilt Lyrics
     *
     * @param path
     * @return
     */
    public static String getInbuiltLyrics(String path) {
        File file = new File(path);
        if (file.exists()) {
            AudioFile audioFile = null;
            try {
                audioFile = AudioFileIO.read(file);
                TagOptionSingleton.getInstance().setAndroid(true);
            } catch (CannotReadException | ReadOnlyFileException | InvalidAudioFrameException | TagException | IOException e) {
                e.printStackTrace();
            }
            Tag tag = null;
            if (audioFile != null) {
                tag = audioFile.getTag();
            }
            if (tag == null) {
                tag = new ID3v24Tag();
            }
            try {
                return tag.getFirst(FieldKey.LYRICS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "No Lyrics found";
        } else {
            Log.d("Helper", "File not found");
            return null;
        }
    }

    /**
     * Animate View
     *
     * @param view
     * @return
     */
    public static Animator[] getAnimator(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
        };
    }

    /**
     * Fragment Transition
     *
     * @param activity
     * @param firstFragment
     * @param secondFragment
     * @param transitionViews
     */
    @SafeVarargs
    @SuppressLint("NewApi")
    public static void setFragmentTransition(FragmentActivity activity, Fragment firstFragment,
                                             Fragment secondFragment, @Nullable Pair<View, String>... transitionViews) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Inflate transitions to apply
            Transition changeTransform = TransitionInflater.from(activity)
                    .inflateTransition(R.transition.change_image_transform);
            Transition explodeTransform = TransitionInflater.from(activity)
                    .inflateTransition(R.transition.change_image_transform);
            // Setup exit transition on first fragment
            firstFragment.setSharedElementReturnTransition(changeTransform);
            firstFragment.setExitTransition(explodeTransform);
            // Setup enter transition on second fragment
            secondFragment.setSharedElementEnterTransition(changeTransform);
            secondFragment.setEnterTransition(explodeTransform);
        }
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, secondFragment)
                .addToBackStack("transaction");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && transitionViews != null) {

            for (Pair<View, String> tr : transitionViews) {
                ft.addSharedElement(tr.first, tr.second);
            }
        }
        ft.commit();
    }

    /**
     * Filter String
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        Pattern lineMatcher = Pattern.compile("\\n[\\\\/:*?\\\"<>|]((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)");
        Matcher m = lineMatcher.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * Save Lyrics
     *
     * @param path
     * @param content
     */
    public static void saveLyrics(String path, String content) {
        try {
            if (!content.isEmpty() && !path.isEmpty() && content.length() > 0 && path.length() > 0) {
                FileWriter writer = new FileWriter(path);
                writer.flush();
                writer.write(stringFilter(content));
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create App directory
     *
     * @param direName
     * @return
     */
    public static String createAppDir(String direName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "MusicX", direName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return null;
    }

    /**
     * Rotate ImageView
     *
     * @param imageView
     */
    public static void rotationAnim(ImageView imageView) {
        RotateAnimation rotateAnimation1 = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation1.setInterpolator(new LinearInterpolator());
        rotateAnimation1.setDuration(300);
        rotateAnimation1.setRepeatCount(0);
        imageView.startAnimation(rotateAnimation1);
    }

    /**
     * Return Array with palette color
     *
     * @param context
     * @param palette
     * @return
     */
    public static int[] getAvailableColor(Context context, Palette palette) {
        int[] temp = new int[3]; //array with size 3
        if (palette.getDarkVibrantSwatch() != null) {
            temp[0] = palette.getDarkVibrantSwatch().getRgb();
            temp[1] = palette.getDarkVibrantSwatch().getTitleTextColor();
            temp[2] = palette.getDarkVibrantSwatch().getBodyTextColor();
        } else if (palette.getDarkMutedSwatch() != null) {
            temp[0] = palette.getDarkMutedSwatch().getRgb();
            temp[1] = palette.getDarkMutedSwatch().getTitleTextColor();
            temp[2] = palette.getDarkMutedSwatch().getBodyTextColor();
        } else if (palette.getVibrantSwatch() != null) {
            temp[0] = palette.getVibrantSwatch().getRgb();
            temp[1] = palette.getVibrantSwatch().getTitleTextColor();
            temp[2] = palette.getVibrantSwatch().getBodyTextColor();
        } else if (palette.getDominantSwatch() != null) {
            temp[0] = palette.getDominantSwatch().getRgb();
            temp[1] = palette.getDominantSwatch().getTitleTextColor();
            temp[2] = palette.getDominantSwatch().getBodyTextColor();
        } else if (palette.getMutedSwatch() != null) {
            temp[0] = palette.getMutedSwatch().getRgb();
            temp[1] = palette.getMutedSwatch().getTitleTextColor();
            temp[2] = palette.getMutedSwatch().getBodyTextColor();
        } else {
            String atkey = Helper.getATEKey(context);
            int accent = Config.accentColor(context, atkey);
            temp[0] = accent;
            temp[1] = 0xffe5e5e5;
            temp[2] = accent;
        }
        return temp;
    }

    public static String getATEKey(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean theme = sharedPreferences.getBoolean(DarkTheme, false);
        Boolean blacktheme = sharedPreferences.getBoolean(BlackTheme, false);
        if (theme) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BlackTheme, false);
            editor.apply();
            return DarkTheme;
        } else if (blacktheme) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DarkTheme, false);
            editor.apply();
            return BlackTheme;
        } else {
            return LightTheme;
        }
    }

    /**
     * Filter Artist ArrayList
     *
     * @param artistlist
     * @param query
     * @return
     */
    public static List<Artist> filterArtist(List<Artist> artistlist, String query) {
        query = query.toLowerCase();
        final List<Artist> filterartistlist = new ArrayList<>();
        for (Artist artist : artistlist) {
            final String text = artist.getName().toLowerCase();
            if (text.contains(query)) {
                filterartistlist.add(artist);
            }
        }
        return filterartistlist;
    }

    /**
     * Filter Album
     *
     * @param albumList
     * @param query
     * @return
     */
    public static List<Album> filterAlbum(List<Album> albumList, String query) {
        query = query.toLowerCase();
        final List<Album> filteralbumlist = new ArrayList<>();
        for (Album album : albumList) {
            final String text = album.getAlbumName().toLowerCase();
            if (text.contains(query)) {
                filteralbumlist.add(album);
            }
        }
        return filteralbumlist;
    }

    /**
     * GuideLines Dialog
     *
     * @param context
     */
    public static void GuidLines(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("GuideLines");
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/Guidlines.html");
        builder.negativeText(android.R.string.cancel);
        builder.positiveText(android.R.string.ok);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.autoDismiss(true);
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.cancelable(true);
            }
        });
        builder.customView(webView, false);
        builder.build();
        builder.show();
    }

    /**
     * ChangeLogs Dialog
     *
     * @param context
     */
    public static void Changelogs(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("Changelogs");
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/app_changelogs.html");
        builder.negativeText(android.R.string.cancel);
        builder.positiveText(android.R.string.ok);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.autoDismiss(true);
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.cancelable(true);
            }
        });
        builder.customView(webView, false);
        builder.build();
        builder.show();
    }

    /**
     * Licenses Dialog
     *
     * @param context
     */
    public static void Licenses(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("Licenses");
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/licenses.html");
        builder.negativeText(android.R.string.cancel);
        builder.positiveText(android.R.string.ok);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.autoDismiss(true);
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.cancelable(true);
            }
        });
        builder.customView(webView, false);
        builder.build();
        builder.show();
    }

    public static String shortTime(Context context, long secs) {
        long hours, mins;
        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;
        final String durationFormat = context
                .getString(hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    /**
     * Show Rate Dailog
     *
     * @param mContext
     */
    public static void showRateDialog(final Context mContext) {
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        String appName = mContext.getString(R.string.app_name);
        builder.title("Rate " + appName);
        builder.content("If you enjoy using " + appName
                + ", please take a moment to rate it. Thanks for your support!");
        builder.negativeText(mContext.getString(android.R.string.cancel));
        builder.positiveText(mContext.getString(android.R.string.ok));
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.rks.musicx")));
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                builder.autoDismiss(true);
            }
        });
        builder.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Return Storage Path
     *
     * @return
     */
    public static String getStoragePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String musicfolderpath = Environment.getExternalStorageDirectory().getPath();
            Log.d("Helper", musicfolderpath);
            return musicfolderpath;
        } else {
            return null;
        }
    }

    /**
     * Return text As bitmap
     *
     * @param text
     * @param textSize
     * @param textColor
     * @return
     */
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize); //text size
        paint.setColor(textColor); //text color
        paint.setTextAlign(Paint.Align.LEFT); //align center
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint); //draw text
        return image;
    }

    public static String durationCalculator(long id) {
        String finalTimerString = "";
        String secondsString = "";
        String mp3Minutes = "";
        // Convert total duration into time

        int minutes = (int) (id % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((id % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            mp3Minutes = "0" + minutes;
        } else {
            mp3Minutes = "" + minutes;
        }
        finalTimerString = finalTimerString + mp3Minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

    /**
     * Delete Playlist Track
     *
     * @param context
     * @param playlistId
     * @param audioId
     */
    public static void deletePlaylistTrack(Context context, long playlistId, long audioId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String filter = MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + audioId;
        resolver.delete(uri, filter, null);
    }

    /**
     * Delete playlist
     *
     * @param context
     * @param selectedplaylist
     */
    public static void deletePlaylist(Context context, String selectedplaylist) {
        String playlistid = getPlayListId(context, selectedplaylist);
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistid};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
    }

    /**
     * Return Playlist Id
     *
     * @param context
     * @param playlist
     * @return
     */
    private static String getPlayListId(Context context, String playlist) {
        int recordcount;
        Uri newuri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        final String playlistid = MediaStore.Audio.Playlists._ID;
        final String playlistname = MediaStore.Audio.Playlists.NAME;
        String where = MediaStore.Audio.Playlists.NAME + "=?";
        String[] whereVal = {playlist};
        String[] projection = {playlistid, playlistname};
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(newuri, projection, where, whereVal, null);
        if (cursor != null) {
            recordcount = cursor.getCount();
            String foundplaylistid = "";
            if (recordcount > 0) {
                cursor.moveToFirst();
                int idColumn = cursor.getColumnIndex(playlistid);
                foundplaylistid = cursor.getString(idColumn);
                cursor.close();
            }
            cursor.close();
            return foundplaylistid;
        } else {
            return "";
        }

    }

    /**
     * Delete Track
     *
     * @param id
     * @param songLoaders
     * @param fragment
     * @param name
     * @param path
     * @param context
     */
    @SuppressLint("StringFormatInvalid")
    private void DeleteTrack(int id, LoaderManager.LoaderCallbacks<List<Song>> songLoaders,
                             Fragment fragment, String name, String path, Context context) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context);
        dialog.title(name);
        dialog.content(context.getString(R.string.delete_music, name));
        dialog.positiveText(android.R.string.ok);
        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        if (file.delete()) {
                            Log.e("-->", "file Deleted :" + path);
                            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"audio/*"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                                @Override
                                public void onMediaScannerConnected() {

                                }

                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    fragment.getLoaderManager().restartLoader(id, null, songLoaders);
                                }
                            });
                            fragment.getLoaderManager().restartLoader(id, null, songLoaders);
                            Toast.makeText(context, "Track deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("-->", "file not Deleted :" + name);
                            fragment.getLoaderManager().restartLoader(id, null, songLoaders);
                            Toast.makeText(context, "Failed to delete song", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        fragment.getLoaderManager().restartLoader(id, null, songLoaders);
                    }
                } else {
                    Log.d("Helper", "Path not found");
                }

            }
        });
        dialog.negativeText(R.string.cancel);
        dialog.show();
    }

    /**
     * Song Menu Options
     *
     * @param torf
     * @param id
     * @param songLoaders
     * @param fragment
     * @param activity
     * @param position
     * @param v
     * @param context
     * @param songListAdapter
     */
    public void showMenu(boolean torf, int id, LoaderManager.LoaderCallbacks<List<Song>> songLoaders, Fragment fragment, MainActivity activity, int position, View v, Context context, SongListAdapter songListAdapter) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.song_list_item, popup.getMenu());
        Song song = songListAdapter.getItem(position);
        popup.getMenu().findItem(R.id.action_remove_playlist).setVisible(torf);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove_playlist:
                        deletePlaylistTrack(context, Extras.getInstance().getPlaylistId(), song.getId());
                        Toast.makeText(context, "Removed from playlist", Toast.LENGTH_SHORT).show();
                        fragment.getLoaderManager().restartLoader(id, null, songLoaders);
                        break;
                    case R.id.action_add_to_queue:
                        activity.addToQueue(song);
                        Toast.makeText(context, "Added to queue", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_set_as_next_track:
                        activity.setAsNextTrack(song);
                        Toast.makeText(context, "Added to next", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add_to_playlist:
                        PlaylistChooser(fragment, context, song.getId());
                        break;
                    case R.id.action_addFav:
                        new FavHelper(context).addFavorite(song.getId());
                        Toast.makeText(context, "Added to fav", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_edit_tags:
                        Extras.getInstance().saveMetaData(song);
                        activity.setFragment(TagEditorFragment.getInstance());
                        break;
                    case R.id.action_set_ringtone:
                        setRingTone(context, song.getmSongPath());
                        Toast.makeText(context, "Ringtone set", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        DeleteTrack(id, songLoaders, fragment, song.getTitle(), song.getmSongPath(), context);
                        break;
                    case R.id.action_details:
                        detailMusic(context, song.getTitle(), song.getAlbum(), song.getArtist(),
                                song.getTrackNumber(), song.getmSongPath());
                        break;
                    case R.id.action_share:
                        Helper.shareMusic(song.getmSongPath(), context);
                        break;
                    case R.id.action_fav:
                        activity.setFragment(FavFragment.newFavoritesFragment());
                        break;
                    case R.id.action_removeFav:
                        if (new FavHelper(context).isFavorite(song.getId())) {
                            new FavHelper(context).removeFromFavorites(song.getId());
                            Toast.makeText(context, "Removed from fav", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "First add to fav", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.go_to_album:
                        Album album = new Album();
                        album.setAlbumName(song.getAlbum());
                        album.setArtistName(song.getArtist());
                        album.setYear(parseToInt(song.getYear(), 0));
                        album.setTrackCount(song.getTrackNumber());
                        album.setId(song.getAlbumId());
                        activity.setFragment(AlbumFragment.newInstance(album));
                        break;
                    case R.id.go_to_artist:
                        Artist artist = new Artist(song.getArtistId(), song.getArtist(), song.getTrackNumber(), song.getTrackNumber());
                        activity.setFragment(ArtistFragment.newInstance(artist));
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public static int parseToInt(String maybeInt, int defaultValue) {
        if (maybeInt == null) return defaultValue;
        maybeInt = maybeInt.trim();
        if (maybeInt.isEmpty()) return defaultValue;
        return Integer.parseInt(maybeInt);
    }

    /**
     * Playlist Chooser
     *
     * @param fragment
     * @param context
     * @param song
     */
    public void PlaylistChooser(Fragment fragment, Context context, long song) {
        PlayListPicker playListPicker = new PlayListPicker();
        playListPicker.setPicked(new playlistPicked() {
            @Override
            public void onPlaylistPicked(Playlist playlist) {
                addSongToPlaylist(context.getContentResolver(), playlist.getId(), song);
                Toast.makeText(context, "Song is added ", Toast.LENGTH_SHORT).show();
            }
        });
        playListPicker.show(fragment.getFragmentManager(), null);
    }

    /**
     * Add songs to playlist
     *
     * @param resolver
     * @param playlistId
     * @param songId
     */
    public void addSongToPlaylist(ContentResolver resolver, long playlistId, long songId) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        final int base = getSongCount(resolver, uri);
        insert(resolver, uri, songId, base + 1);
    }

    /**
     * Return song Count
     *
     * @param resolver
     * @param uri
     * @return
     */
    public int getSongCount(ContentResolver resolver, Uri uri) {
        String[] cols = new String[]{"count(*)"};
        Cursor cur = resolver.query(uri, cols, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
            final int count = cur.getInt(0);
            cur.close();
            return count;
        } else {
            return 0;
        }
    }

    /**
     * Create Playlist
     *
     * @param resolver
     * @param playlistName
     * @return
     */
    public Uri createPlaylist(ContentResolver resolver, String playlistName) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, playlistName);
        return resolver.insert(uri, values);
    }

    /**
     * Return Playlist
     *
     * @param resolver
     * @param uri
     * @param songId
     * @param index
     */
    private void insert(ContentResolver resolver, Uri uri, long songId, int index) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, index);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId);
        resolver.insert(uri, values);

    }

    /**
     * Search Lyrics
     *
     * @param context
     * @param title
     * @param artist
     * @param path
     * @param setlyrics
     */
    public void searchLyrics(Context context, String title, String artist, String path, TextView setlyrics) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_lyrics, null);
        MaterialDialog.Builder searchLyrics = new MaterialDialog.Builder(context);
        searchLyrics.title("Search Lyrics");
        searchLyrics.positiveText(android.R.string.ok);
        TextInputEditText songeditText = (TextInputEditText) v.findViewById(R.id.lyricssong_name);
        TextInputEditText artisteditText = (TextInputEditText) v.findViewById(R.id.lyricsartist_name);
        songeditText.setText(title);
        artisteditText.setText(artist);
        searchLyrics.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                LoadLyrics(songeditText.getText().toString(), artisteditText.getText().toString(), path, setlyrics);
            }
        });
        searchLyrics.negativeText(android.R.string.cancel);
        searchLyrics.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                searchLyrics.autoDismiss(true);
            }
        });
        searchLyrics.customView(v, false);
        searchLyrics.show();
    }

    /**
     * Display Lyrics
     *
     * @param title
     * @param artist
     * @param path
     * @param lrcView
     */
    public void LoadLyrics(String title, String artist, String path, TextView lrcView) {
        if (title != null && artist != null) {
            File file = new File(loadLyrics(title));
            if (file.exists()) {
                if (file.getName().equals(title)) {
                    readLyricsFromFile(file, lrcView);
                } else {
                    Log.d("Helper", "not same file ");
                }
            } else {
                try {
                    new LyricsData(context, title, artist, path, lrcView).execute("Executed");
                } finally {
                    lrcView.setText(getInbuiltLyrics(path));
                }
            }

        } else {
            Log.d("Helper", "Title, Artist is null");
        }
    }

    /**
     * Location Of Lyrics
     *
     * @param name
     * @return
     */
    public String loadLyrics(String name) {
        return getDirLocation() + setFileName(name);
    }

    /**
     * Read Lyrics from file
     *
     * @param file
     * @param textView
     */
    public void readLyricsFromFile(File file, TextView textView) {
        if (file != null) {
            FileInputStream iStr;
            try {
                iStr = new FileInputStream(file);
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(iStr));
                String TextLine = "";
                String TextBuffer = "";
                try {
                    while ((TextLine = fileReader.readLine()) != null) {
                        TextBuffer += TextLine + "\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textView.setText(TextBuffer);
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Helper", "error");
        }
    }

    /**
     * ArtistImage  load
     *
     * @param name
     * @return
     */
    public String loadArtistImage(String name) {
        return getArtistArtworkLocation() + setFileName(name) + ".jpeg";
    }

    /**
     * AlbumImage  load
     *
     * @param name
     * @return
     */
    public String loadAlbumImage(String name) {
        return getAlbumArtworkLocation() + setFileName(name) + ".jpeg";
    }

    /**
     * AlbumArtwork Location
     *
     * @return
     */
    public String getAlbumArtworkLocation() {
        return Environment.getExternalStorageDirectory() + "/MusicX/" + ".AlbumArtwork/";
    }

    /**
     * ArtistArtwork Location
     *
     * @return
     */
    public String getArtistArtworkLocation() {
        return Environment.getExternalStorageDirectory() + "/MusicX/" + ".ArtistArtwork/";
    }

    /**
     * Return Lyrics Directory
     *
     * @return
     */
    public String getDirLocation() {
        return Environment.getExternalStorageDirectory() + "/MusicX/" + "Lyrics/";
    }

    /**
     * Set fileName
     *
     * @param title
     * @return
     */
    public String setFileName(String title) {
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.unknown);
        }
        return title;
    }

    /**
     * Animate view's background color
     *
     * @param view
     * @param colorBg
     */
    public void animateViews(View view, int colorBg) {
        colorAnimation = setAnimator(0xffe5e5e5, colorBg);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
    }

    private ValueAnimator setAnimator(int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        long duration = 300;
        colorAnimation.setDuration(duration);
        return colorAnimation;
    }

    /**
     * Filter Song List
     *
     * @param songList
     * @param query
     * @return
     */
    public List<Song> filter(List<Song> songList, String query) {
        query = query.toLowerCase().trim();
        final List<Song> filtersonglist = new ArrayList<>();
        for (Song song : songList) {
            final String text = song.getTitle().toLowerCase().trim();
            if (text.contains(query)) {
                filtersonglist.add(song);
            }
        }
        return filtersonglist;
    }

    public List<FolderModel> filterFolder(List<FolderModel> modelList, String query){
        query = query.toLowerCase().trim();
        final List<FolderModel> folderModels = new ArrayList<>();
        for (FolderModel folder : folderModels){
            final String text = folder.getName().toLowerCase().trim();
            if (text.contains(query)){
                folderModels.add(folder);
            }
        }
        return folderModels;
    }

    /**
     * Font
     * @param context
     * @param path
     */
    public static void getCalligraphy (Context context, String path){
         CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(path)
                .addCustomStyle(AppCompatTextView.class, android.R.attr.textViewStyle)
                .addCustomStyle(TextView.class, android.R.attr.textViewStyle)
                .addCustomStyle(EditText.class, android.R.attr.editTextStyle)
                .setFontAttrId(R.attr.fontPath)
                .build());
        Extras.getInstance().saveTypeface(path);
    }

}

