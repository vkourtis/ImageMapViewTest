package com.example.vkourtis.imagemapviewtest;

import android.graphics.Path;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vassilis Kourtis on 11/01/2017.
 */

public class POI implements Parcelable {
    private static final String TAG = POI.class.getName();

    private int id;
    private String title;
    private String text;
    private String audio;   //path to audio file
    private List<PointF> perimeterPoints;
    private boolean visited;
    private Path path;

    public POI(int id) {
        this.id = id;
        this.perimeterPoints = new ArrayList<>();
        this.visited = false;
    }

    public void addAreaPoint(int x, int y) {
        perimeterPoints.add(new PointF(x,y));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAudio() {
        return audio;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Path getPath() {
        if (this.path == null) {
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);

            path.moveTo(perimeterPoints.get(0).x, perimeterPoints.get(0).y);

            for (int i = 1; i< perimeterPoints.size(); i++) {
                path.lineTo(perimeterPoints.get(i).x, perimeterPoints.get(i).y);
            }

            path.close();

            this.path = path;
        }

        return this.path;
    }

    // Parcelling part
    public POI(Parcel in){
        this.id = in.readInt();
        this.perimeterPoints = new ArrayList<>();
        in.readTypedList(this.perimeterPoints, PointF.CREATOR);
        this.visited = in.readByte() != 0;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeTypedList(perimeterPoints);
        dest.writeByte((byte) (this.visited ? 1 : 0));
    }

    public static final Creator CREATOR = new Creator() {
        public POI createFromParcel(Parcel in) {
            return new POI(in);
        }

        public POI[] newArray(int size) {
            return new POI[size];
        }
    };
}
