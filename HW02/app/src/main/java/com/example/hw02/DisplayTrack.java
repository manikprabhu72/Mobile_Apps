package com.example.hw02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DisplayTrack extends AppCompatActivity {
    TextView tv_title,tv_genre,tv_artist,tv_album,tv_track_price,tv_album_price;
    ImageView iv_image;
    Button bt_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_track);
        setTitle("iTunes Music Search");
        tv_title = findViewById(R.id.tv_title);
        tv_genre = findViewById(R.id.tv_genre);
        tv_artist = findViewById(R.id.tv_artist);
        tv_album = findViewById(R.id.tv_album);
        tv_track_price = findViewById(R.id.tv_track_price);
        tv_album_price = findViewById(R.id.tv_album_price);
        iv_image = findViewById(R.id.iv_image);
        bt_finish = findViewById(R.id.bt_finish);
        if(getIntent() !=null && getIntent().getExtras() != null){
            Track track = (Track)getIntent().getExtras().getSerializable(MainActivity.TRACK_KEY);
            tv_title.setText("Track: "+track.title);
            tv_genre.setText("Genre: "+track.genre);
            tv_artist.setText("Artist: "+track.artist);
            tv_album.setText("Album: "+track.album);
            tv_album_price.setText("Album Price: "+track.albumPrice);
            tv_track_price.setText("Track Price: "+track.trackPrice);
            if(track.imageURL != null && !track.imageURL.isEmpty()){
                Picasso.get().load(track.imageURL).into(iv_image);
            } else{
                Toast.makeText(DisplayTrack.this,"No Image URL Found!!!", Toast.LENGTH_SHORT).show();
                iv_image.setVisibility(ImageView.INVISIBLE);
            }
        }

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
