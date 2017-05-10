package com.example.vkourtis.imagemapviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flipagram.assetcopylib.AssetCopier;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String assetsDir = getExternalFilesDir(null).getPath() + "/assets";
        File destDir = new File(assetsDir);
        destDir.mkdirs();

        String assetName = "placeholder_large.jpg";

        try {
            new AssetCopier(this).withFileScanning().copy(assetName, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imagePath = assetsDir + "/" + assetName;

        List<POI> poiList = new ArrayList<>();

        POI poi1 = new POI(1);
        poi1.addAreaPoint(100,200);
        poi1.addAreaPoint(100,250);
        poi1.addAreaPoint(150,250);
        poi1.addAreaPoint(150,200);
        poi1.setTitle("Just a POI");
        poi1.setText("I am just a simple square");

        POI poi2 = new POI(2);
        poi2.addAreaPoint(260,360);
        poi2.addAreaPoint(270,380);
        poi2.addAreaPoint(270,400);
        poi2.addAreaPoint(350,400);
        poi2.setTitle("Fabulous POI");
        poi2.setText("I am a fabulous point of interest. Not just the next corner's square POI " +
                "that you usually meet. I am here to show you that you cannot expect all POIs to be " +
                "exactly the same. You may have long descriptions as well. You see, I cannot fit " +
                "my fabulousness in just one line!!");

        poiList.add(poi1);
        poiList.add(poi2);


        final ImageMapView imageMapView = (ImageMapView) findViewById(R.id.image_map_view);
        imageMapView.setImage(imagePath);
        imageMapView.setPoiList(poiList);
        imageMapView.setOnPoiSelectedListener(new ImageMapView.OnPoiSelectedListener() {
            @Override
            public void onPoiSelected(POI poi) {
                imageMapView.addInfoBox(poi, poi.getTitle(), poi.getText());
            }
        });
    }
}
