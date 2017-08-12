package ed.iii.tw.mystopwatch;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private boolean isRunnin;
    private Button left, right;
    private long i, lapCount;
    private Timer timer;
    private ClockTask clockTask;
    private UIHandler handler;
    private TextView clock_hr, clock_min, clock_sec, clock_ms;
    private ListView list;
    private SimpleAdapter adapter;
    private String[] from = {"ed"};
    private int[] to = {R.id.lap_content};
    private List<Map<String, String>> data;
    private int hr, min, sec, ms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        clock_hr = (TextView) findViewById(R.id.clock_hr);
        clock_min = (TextView) findViewById(R.id.clock_min);
        clock_sec = (TextView) findViewById(R.id.clock_sec);
        clock_ms = (TextView) findViewById(R.id.clock_ms);
        timer = new Timer();
        handler = new UIHandler();

        list = (ListView) findViewById(R.id.list);
        initList();
    }

    private void initList() {
        data = new LinkedList<>();
        adapter =
                new SimpleAdapter(
                        this, data , R.layout.layout_lap, from, to);
        list.setAdapter(adapter);
    }

    // Reset/Lap
    public void doLeft(View view){
        if (isRunnin){
            doLap();
        }else{
            doReset();
        }
    }
    // Start/Stop
    public void doRight(View view){
        isRunnin = !isRunnin;
        if (isRunnin){
            right.setText("Stop");
            left.setText("Lap");
            doStart();
        }else{
            right.setText("Start");
            left.setText("Reset");
            doStop();
        }
    }

    private void doStart(){
        if (clockTask == null){
            clockTask = new ClockTask();
            timer.schedule(clockTask, 10, 10);
        }
    }
    private void doStop(){
        if (clockTask != null){
            clockTask.cancel();
            clockTask = null;
        }

    }
    private void doLap(){
        Map<String, String> row = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append((hr<10?"0"+hr:hr)+":"+(min<10?"0"+min:min)+":"+(sec<10?"0"+sec:sec)+"."+(ms<10?"0"+ms:ms));
        row.put(from[0], ++lapCount + ". " + sb);
        data.add(0, row);
        adapter.notifyDataSetChanged();
    }
    private void doReset(){
        i = lapCount = 0;
        data.clear();
        adapter.notifyDataSetChanged();
        clock_hr.setText("00");
        clock_min.setText("00");
        clock_sec.setText("00");
        clock_ms.setText("00");
    }

    @Override
    public void finish() {
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.finish();
    }

    private class ClockTask extends TimerTask{
        @Override
        public void run() {
            i++;
            Message mesg = new Message();
            Bundle data = new Bundle();
            data.putLong("i", i);
            mesg.setData(data);
            handler.sendMessage(mesg);
        }
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long i = msg.getData().getLong("i");
            hr = (int)((i / (100 * 60 * 60))%24);
            min = (int)((i / (100 * 60 ))%60);
            sec = (int)((i / 100)%60);
            ms = (int)(i % 100);
            clock_hr.setText(hr < 10 ? "0" + hr : "" + hr);
            clock_min.setText(min < 10 ? "0" + min : "" + min);
            clock_sec.setText(sec < 10 ? "0" + sec : "" + sec);
            clock_ms.setText(ms < 10 ? "0" + ms : "" + ms);
        }
    }
}
