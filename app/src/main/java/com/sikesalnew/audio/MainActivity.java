package com.sikesalnew.audio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sikesalnew.audio.Library.RecordDialog;

import java.util.UUID;



public class MainActivity extends AppCompatActivity implements Runnable   {
   RecordDialog recordDialog;
   TextView txt_file,txt_name,duration;
   Button btn_play;
   MediaPlayer mp;
    boolean wasPlaying = false;
    SeekBar seekBar;
    FloatingActionButton fab;
    String filenames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_file = findViewById(R.id.textView7);
        final TextView seekBarHint = findViewById(R.id.textView3);
        duration = findViewById(R.id.textView2);
        txt_name = findViewById(R.id.textView9);
        seekBar = findViewById(R.id.seekBar2);
        fab = findViewById(R.id.btn_play);
        mp = new MediaPlayer();

//        btn_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mp.start();
//            }
//        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { Toast.makeText(MainActivity.this, "play", Toast.LENGTH_SHORT).show();
             playSong();
            }
        });



        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog = RecordDialog.newInstance("Record Audio");
                recordDialog.setMessage("Tekan untuk merekam suara ");
                recordDialog.show(MainActivity.this.getFragmentManager(),"TAG");
                recordDialog.setPositiveButton("Simpan", new RecordDialog.ClickListener() {
                    @Override
                    public void OnClickListener(String path, String filename) {
                        txt_file.setText(path);
                        txt_name.setText(filename);
                        try{


                            filenames=filename;
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this,"Save audio: " + path, Toast.LENGTH_LONG).show();
                    }

                });
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    seekBarHint.setText("0:0" + x);
                else
                    seekBarHint.setText("0:" + x);

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));

                if (progress > 0 && mp != null && !mp.isPlaying()) {
                    clearMediaPlayer();
                    fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                    MainActivity.this.seekBar.setProgress(0);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                if (mp != null && mp.isPlaying()) {
                    mp.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    public void playSong() {

        try {


            if (mp != null && mp.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            }


            if (!wasPlaying) {

                if (mp == null) {
                    mp = new MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));

//                AssetFileDescriptor descriptor = getAssets().openFd("suits.mp3");
//                mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
//                descriptor.close();
                mp.setDataSource("/storage/emulated/0/eoffice/"+filenames);
                mp.prepare();

//                mp.prepare();
                mp.setVolume(0.5f, 0.5f);
                mp.setLooping(false);
                seekBar.setMax(mp.getDuration());

                mp.start();
                new Thread(this).start();

            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void run() {

        int currentPosition = mp.getCurrentPosition();
        int total = mp.getDuration();


        while (mp != null && mp.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mp.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    private void clearMediaPlayer() {
        mp.stop();
        mp.release();
        mp = null;
    }



}
