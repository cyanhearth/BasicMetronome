package com.example.cyanhearth.basicmetronome;

import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
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

    // SoundPool to handle each sound and HashMap to contain references
    private SoundPool pool;
    private HashMap<String, Integer> soundPoolMap;

    // references to UI elements
    private EditText tempoSelection;
    private TextView notifyError;
    private Spinner soundsSpinner;
    private Spinner timeSigSpinner;
    private Button button;

    // used to find the sound in the hashmap
    private String sound_id;

    //used to set the appropriate time signature
    private String timeSig;

    // tempo in bpm
    private int tempo;

    // period in ms
    private int period;

    // handler to play the sound at appropriate intervals
    private Handler handler;
    private Runnable repeat;

    // to keep track of which beat is being played
    private int beat;

    // which beat should be emphasised
    private int emphasis;

    // modify period to fit the time signature
    private int modifier;

    // the volume at which the sound is to be played
    private float volume;

    //reference to the resources
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        handler = new Handler();

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
                // this matches the key in the hashmap
                sound_id = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // initialize and populate the time signature spinner
        timeSigSpinner = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> timeSigAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_sigs, android.R.layout.simple_spinner_item);

        timeSigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSigSpinner.setAdapter(timeSigAdapter);

        timeSigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeSig = (String) parent.getItemAtPosition(position);

                // set modifier and emphasis values to match the time signature
                switch (timeSig) {
                    case "2/4":
                        modifier = 1;
                        emphasis = 2;
                        break;
                    case "3/4":
                        modifier = 1;
                        emphasis = 3;
                        break;
                    case "4/4":
                        modifier = 1;
                        emphasis = 4;
                        break;
                    case "4/8":
                        modifier = 2;
                        emphasis = 4;
                        modifier = 2;
                        break;
                    default:
                        modifier = 1;
                        emphasis = 4;
                        break;
                }

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

        // add the sounds to the SoundPool
        soundPoolMap.put("Blip", pool.load(this, R.raw.blip, 1));
        soundPoolMap.put("Boop", pool.load(this, R.raw.boop, 1));

        // initialize the beat
        beat = 0;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // reset the error message
                notifyError.setText("");

                //Get the value the user entered for the tempo
                tempo = Integer.parseInt(tempoSelection.getText().toString());

                if (tempo < 40 || tempo > 200) {

                    // notify the user if an invalid tempo has been entered
                    notifyError.setText(res.getString(R.string.wrong_tempo));
                    return;

                }

                // make sure the tempo can't be altered once the metronome has been started
                tempoSelection.setFocusable(false);

                // button behaviour if the metronome is not running
                if (button.getText().toString().equals(res.getString(R.string.button_text_start))) {

                    repeat = new Runnable () {

                        @Override
                        public void run() {

                            if (beat >= emphasis) {
                                beat = 0;
                            }

                            if (beat == 0) {
                                //first beat should be emphasised
                                volume = 1.0f;
                            }
                            else {
                                volume = 0.3f;
                            }

                            // period in milliseconds
                            period = (60000 / tempo) / modifier;

                            // play the sound from the SoundPool
                            pool.play(soundPoolMap.get(sound_id), volume, volume, 1, 0, 1);
                            beat++;

                            handler.postDelayed(repeat, period);

                        }
                    };

                    // start the runnable
                    handler.postDelayed(repeat, period);

                    button.setText(res.getString(R.string.button_text_stop));

                }
                else {

                    // stop the runnable
                    handler.removeCallbacks(repeat);

                    //reset beat
                    beat = 0;

                    button.setText(res.getString(R.string.button_text_start));

                    // allow tempo to be changed again
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
