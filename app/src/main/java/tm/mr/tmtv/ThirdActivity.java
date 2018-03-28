package tm.mr.tmtv;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ThirdActivity extends AppCompatActivity {

    static SimpleExoPlayer player;

    static Firebase mRef;
    static AppCompatDialogFragment dialogFragment;
    static SimpleExoPlayerView sepView;
    ImageButton btnList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialogFragment = new CustomDialog();
        btnList = (ImageButton) findViewById(R.id.btnList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment.show(getSupportFragmentManager(), "Dialog");
            }
        });

        sepView = (SimpleExoPlayerView) findViewById(R.id.sepv);

        sepView.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(final int visibility) {
                Log.d("ANDNERD", "onVisibilityChange: " + visibility);
                btnList.setVisibility(visibility == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });


        mRef = new Firebase("https://awesome0-86d59.firebaseio.com/list");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    str = str + snapshot.getKey() + "-" + snapshot.getValue() + ";";
                }
                str = str.substring(0, str.length() - 1);
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput("chnls", Context.MODE_PRIVATE);
                    outputStream.write(str.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        launchPlayer(this, getSharedPreferences("data", MODE_PRIVATE).getString("last", "http://217.174.225.146/legacyhls/ch001.m3u8"));
    }

    public static void launchPlayer(final Context context, String sUrl) {
        if (player != null) {
            player.release();
            player = null;
        }
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultTrackSelector(new AdaptiveVideoTrackSelection.Factory(bandwidthMeter)),
                new DefaultLoadControl());
        sepView.setPlayer(player);

        MediaSource mediaSource = new HlsMediaSource(
                Uri.parse(sUrl),
                new DefaultDataSourceFactory(context, Util.getUserAgent(context, "tvtm"), bandwidthMeter), null, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(context, R.string.internet_connection_required, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPositionDiscontinuity() {
            }
        });
        SharedPreferences.Editor edit = context.getSharedPreferences("data", MODE_PRIVATE).edit();
        edit.putString("last", sUrl);
        edit.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public static class CustomDialog extends AppCompatDialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_layout, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new MAdapter(getContext()));
            return view;
        }
    }

    public static class MAdapter extends RecyclerView.Adapter {
        Map<String, String> hm;
        Context context;

        public MAdapter(Context context) {
            this.context = context;
            hm = hm(context);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.two_line_list_item, parent, false);
            Holder dataObjectHolder = new Holder(view);
            return dataObjectHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final Holder myHolder = (Holder) holder;
            final String s = (String) hm.keySet().toArray()[position];
            myHolder.textView.setText(s.replace(",", "."));
            ((Holder) holder).textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchPlayer(context, hm.get(s));
                    dialogFragment.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return hm.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            private TextView textView;

            public Holder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text1);
            }
        }
    }

    public static Map<String, String> hm(Context context) {
        FileInputStream fis;
        StringBuilder sb = null;
        try {
            fis = context.openFileInput("chnls");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> hm = new HashMap<>();
        if (sb != null) {
            for (String s : sb.toString().split(";"))
                hm.put(s.split("-")[0], s.split("-")[1]);
        }
        hm = new TreeMap<>(hm);
        return hm;
    }

}