package tm.mr.tmtv;

import android.app.Application;

import com.firebase.client.Firebase;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
