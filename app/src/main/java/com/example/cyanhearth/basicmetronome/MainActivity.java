package com.example.cyanhearth.basicmetronome;

import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private SoundPool pool;
    private HashMap<String, Integer> soundPoolMap;
    private Button button;
    private String sound_id;
    private int tempo;
    private int period;

    private EditText tempoSelection;
    private TextView notifyError;

    private Spinner soundsSpinner;

    private Timer timer;
    private TimerTask repeat;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        timer = new Timer();

        button = (Button) findViewById(R.id.button);

        tempoSelection = (EditText) findViewById(R.id.editText);
        notifyError = (TextView) findViewById(R.id.textView2);

        soundPoolMap = new HashMap<> ();

        //initialize and populate the sounds spinner
        soundsSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sounds_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        soundsSpinner.setAdapter(adapter);

        soundsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sound_id = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // load SoundPool differently depending on Android version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            pool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .build();
        }
        else
        {
            pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        soundPoolMap.put("Blip", pool.load(this, R.raw.blip, 1));
        soundPoolMap.put("Boop", pool.load(this, R.raw.boop, 1));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyError.setText("");

                //Get the value the user entered for the tempo
                tempo = Integer.parseInt(tempoSelection.getText().toString());

                if (tempo < 40 || tempo > 200) {

                    notifyError.setText(res.getString(R.string.wrong_tempo));
                    return;

                }

                tempoSelection.setFocusable(false);

                // period in milliseconds
                period = 60000 / tempo;

                //id = soundPoolMap.get(1);

                if (button.getText().toString().equals(res.getString(R.string.button_text_start))) {

                    repeat = new TimerTask() {
                        @Override
                        public void run() {
                            pool.play(soundPoolMap.get(sound_id), 1, 1, 1, 0, 1);
                        }
                    };

                    timer.schedule(repeat, 0, period);

                    button.setText(res.getString(R.string.button_text_stop));

                }
                else {

                    repeat.cancel();

                    button.setText(res.getString(R.string.button_text_start));

                    tempoSelection.setFocusableInTouchMode(true);

                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
