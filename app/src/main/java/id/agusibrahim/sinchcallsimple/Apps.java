package id.agusibrahim.sinchcallsimple;

import android.app.Application;
import android.os.Build;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;

public class Apps extends Application {
    public static String USER_ID;
    public static SinchClient sinchClient;
    public static CallClient callClient;

    @Override
    public void onCreate() {
        super.onCreate();
        USER_ID=(""+(Build.FINGERPRINT+Build.MODEL).hashCode()).replace("-","");
        sinchClient = Sinch.getSinchClientBuilder().context(this)
                .applicationKey("xxxxx-4369-496c-a3a6-xxxxxx")
                .applicationSecret("EuJC9xmIWU+XJLCwxxxxxxxxx")
                .environmentHost("clientapi.sinch.com")
                .userId(USER_ID)
                .build();
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportCalling(true);
    }
}
