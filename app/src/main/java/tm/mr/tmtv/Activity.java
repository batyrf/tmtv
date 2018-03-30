package tm.mr.tmtv;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * Created by viridis on 29.03.2018.
 */

public class Activity extends AppCompatActivity implements PlayerControlView.VisibilityListener,
        RvAdapter.listenerUri, View.OnClickListener,
        OnSuccessListener<QuerySnapshot>, OnFailureListener,
        Player.listener {

    public static final String TAG = "ANDNERD";
    private static final String SAVE_URI = "save_uri";
    Dialog dialog;
    Map<String, Object> map;
    Player player;

    AppCompatImageButton btn;
    PlayerView playerView;

    String sUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        FirebaseFirestore.getInstance().collection("channels").get().addOnFailureListener(this).addOnSuccessListener(this);
        dialog = new Dialog();

        player = Player.getInstance(this);
        playerView = findViewById(R.id.playerView);
        playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(this);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        if (savedInstanceState != null)
            sUri = savedInstanceState.getString(SAVE_URI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sUri != null)
            player.play(Uri.parse(sUri));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_URI, player.getUri().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {
        btn.setVisibility(visibility == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onChannelChange(Uri uri) {
        player.play(uri);
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (map != null) {
            dialog.show(getSupportFragmentManager(), map, Activity.this);
            playerView.hideController();
        }
    }

    @Override
    public void onSuccess(QuerySnapshot documentSnapshots) {
        map = documentSnapshots.iterator().next().getData();
        Log.d(TAG, "onSuccess: " + map);
        if (sUri == null) {
            sUri = map.values().toArray()[0].toString();
            player.play(Uri.parse(sUri));
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure: " + e);
    }

    @Override
    public void noConnectionCallback() {
        Toast.makeText(this, getResources().getString(R.string.internet_connection_required), Toast.LENGTH_LONG).show();
    }
}
