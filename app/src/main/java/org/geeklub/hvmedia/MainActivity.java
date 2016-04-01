package org.geeklub.hvmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by HelloVass on 16/4/1.
 */
public class MainActivity extends AppCompatActivity {

  private View mVideoDemoButton;

  private View mAudioDemoButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    mVideoDemoButton = findViewById(R.id.btn_video_demo);
    mVideoDemoButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, VideoActivity.class));
      }
    });

    mAudioDemoButton = findViewById(R.id.btn_audio_demo);
    mAudioDemoButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, AudioActivity.class));
      }
    });
  }
}
