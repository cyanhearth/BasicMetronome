package com.example.cyanhearth.basicmetronome;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    SoundPool pool;
    Button button;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

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

        id = pool.load(this, R.raw.blip, 1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Get the value the user entered for the tempo
                TODO: filter for appropriate values
                */
                EditText tempoSelection = (EditText) findViewById(R.id.editText);
                int tempo = Integer.parseInt(tempoSelection.getText().toString());

                //Set to display tempo value for testing
                //TextView displayTempo = (TextView) findViewById(R.id.textView2);
                //displayTempo.setText(String.valueOf(tempo));
                pool.play(id, 1, 1, 1, -1, 1);
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
