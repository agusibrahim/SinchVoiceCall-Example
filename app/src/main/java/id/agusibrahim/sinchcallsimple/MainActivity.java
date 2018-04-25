package id.agusibrahim.sinchcallsimple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mMainMyid;
    private Button mMainCallbtn;
    private EditText mMainTargetid;
    private TextView mMainStatus;
    private CallClient callclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainMyid.setText(Apps.USER_ID);
        mMainCallbtn.setEnabled(Apps.sinchClient.isStarted());
        startService(new Intent(this, SinchService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMainMyid = findViewById(R.id.main_myid);
        mMainCallbtn = findViewById(R.id.main_callbtn);
        mMainCallbtn.setOnClickListener(this);
        mMainTargetid = findViewById(R.id.main_targetid);
        mMainStatus = findViewById(R.id.main_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.main_callbtn:
                if(mMainTargetid.getText().length()<8){
                    mMainTargetid.setError("Masukan ID yang benar");
                    break;
                }else mMainTargetid.setError(null);
                Call currentcall = callclient.callUser(mMainTargetid.getText().toString());
                Intent callscreen = new Intent(this, IncommingCallActivity.class);
                callscreen.putExtra("callid", currentcall.getCallId());
                callscreen.putExtra("incomming", false);
                callscreen.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(callscreen);
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchConnected(SinchStatus.SinchConnected sinchConnected){
        mMainStatus.append(String.format("* CONNECTED :)\n---------------------------\n"));
        mMainCallbtn.setEnabled(true);
        callclient=sinchConnected.callClient;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchDisconnected(SinchStatus.SinchDisconnected sinchDisconnected){
        mMainStatus.append(String.format("* DISCONNECTED\n---------------------------\n"));
        mMainCallbtn.setEnabled(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchFailed(SinchStatus.SinchFailed sinchFailed){
        mMainStatus.append(String.format("* CONNECTION FAILED: %s\n---------------------------\n", sinchFailed.error.getMessage()));
        mMainCallbtn.setEnabled(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchLogging(SinchStatus.SinchLogger sinchLogger){
        mMainStatus.append(String.format("* %s ** %s ** %s\n---------------------------\n", sinchLogger.message, sinchLogger.area, sinchLogger.level));
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
}
