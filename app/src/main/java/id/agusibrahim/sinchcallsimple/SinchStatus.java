package id.agusibrahim.sinchcallsimple;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

public class SinchStatus {
    public static class SinchConnected{
        public SinchClient client;
        public CallClient callClient;
        public SinchConnected(SinchClient client, CallClient callClient){
            this.client=client;
            this.callClient=callClient;
        }
    }
    public static class SinchDisconnected{
        public SinchClient client;
        public SinchDisconnected(SinchClient client){
            this.client=client;
        }
    }
    public static class SinchFailed{
        public SinchClient client;
        public SinchError error;
        public SinchFailed(SinchClient client, SinchError error){
            this.client=client;
            this.error=error;
        }
    }
    public static class SinchLogger{
        public int level;
        public String area;
        public String message;
        public SinchLogger(String area, String msg, int level){
            this.level=level;
            this.area=area;
            this.message=msg;
        }
    }
    public static class SinchIncommingCall{
        public CallClient client;
        public Call call;
        public SinchIncommingCall(CallClient client, Call call){
            this.client=client;
            this.call=call;
        }
    }
}
