package id.agusibrahim.sinchcallsimple;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IncommingCallActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mCallingStatus;
    private TextView mCallingName;
    private LinearLayout mCallingNotify;
    private Button mCallingAnswer;
    private Button mCallingReject;
    private LinearLayout mCallingActionButton;
    private Call call;
    private Ringtone r;
    private boolean isIncomming;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.incommingcall_layout);
        initView();
        call = Apps.callClient.getCall(getIntent().getStringExtra("callid"));
        isIncomming=getIntent().getBooleanExtra("incomming", true);
        if(isIncomming) {
            setBlinking(mCallingNotify, true);
            mCallingStatus.setText("TELEPON MASUK");
            mCallingName.setText(call.getRemoteUserId()+"");
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            setFullscreen(true);
            r.play();
        }else{
            setFullscreen(false);
            mCallingStatus.setText("MEMANGGIL...");
            mCallingAnswer.setVisibility(View.GONE);
            mCallingName.setText(call.getRemoteUserId()+"");
            mCallingReject.setText("END");
        }
    }

    private void initView() {
        mCallingStatus = findViewById(R.id.calling_status);
        mCallingName = findViewById(R.id.calling_name);
        mCallingNotify = findViewById(R.id.calling_notify);
        mCallingAnswer = findViewById(R.id.calling_answer);
        mCallingAnswer.setOnClickListener(this);
        mCallingReject = findViewById(R.id.calling_reject);
        mCallingReject.setOnClickListener(this);
        mCallingActionButton = findViewById(R.id.calling_action_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.calling_answer:
                call.answer();
                mCallingAnswer.setVisibility(View.GONE);
                mCallingReject.setText("END");
                mCallingStatus.setText("ACTIVE CALL");
                setBlinking(mCallingNotify, false);
                if(r!=null)r.stop();
                setFullscreen(false);
                break;
            case R.id.calling_reject:
                call.hangup();
                if(r!=null)r.stop();
                finish();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchLogging(SinchStatus.SinchLogger sinchLogger){
        if(sinchLogger.message.contains("terminationCause=DENIED")&&!isIncomming){
            if(r!=null)r.stop();
            call.hangup();
            Toast.makeText(this, "USER REJECTED", Toast.LENGTH_SHORT).show();
            finish();
        }else if(sinchLogger.message.contains("onSessionEstablished")){
            mCallingStatus.setText("ACTIVE CALL");
        }else if(sinchLogger.message.contains("onSessionTerminated")){
            call.hangup();
            if(r!=null)r.stop();
            if(sinchLogger.message.contains("terminationCause=NO_ANSWER")){
                Toast.makeText(this, "NO ANSWER", Toast.LENGTH_SHORT).show();
            }else if(sinchLogger.message.contains("terminationCause=TIMEOUT")){
                Toast.makeText(this, "TIMEOUT", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void setFullscreen(boolean status){
        if(status){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    private void setBlinking(View object, boolean status) {
        if(!status){
            object.animate().cancel();
            return;
        }
        ObjectAnimator anim= ObjectAnimator.ofFloat(object, View.ALPHA, 0.1f,1.0f);
        anim.setDuration(1000);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }
}
