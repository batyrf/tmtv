package tm.mr.tmtv;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;

/**
 * Created by viridis on 30.03.2018.
 */

public class Player extends SimpleExoPlayer {

    Context context;
    DataSource.Factory factory;

    Uri uri;

    public Player(final Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl) {
        this(renderersFactory, trackSelector, loadControl);
        this.context = context;
        factory = new DefaultDataSourceFactory(context, "tmtv");
        addListener(new DefaultEventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (error.getCause() instanceof HttpDataSource.HttpDataSourceException) {
                    ((listener) context).noConnectionCallback();
                }
            }
        });
    }

    protected Player(RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl) {
        super(renderersFactory, trackSelector, loadControl);
    }

    static Player INSTANCE;

    public static Player getInstance(Context context) {
        if (INSTANCE == null) {
            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl();
            INSTANCE = new Player(context, renderersFactory, trackSelector, loadControl);
        }
        return INSTANCE;
    }

    public void play(Uri uri) {
        this.uri = uri;
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(factory).createMediaSource(uri);//, mainHandler, null);
        prepare(hlsMediaSource);
        setPlayWhenReady(true);
    }

    public Uri getUri() {
        return uri;
    }

    interface listener {
        void noConnectionCallback();
    }

}