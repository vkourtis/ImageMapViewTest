package com.example.vkourtis.imagemapviewtest;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.paths.CompositePathView;

import java.util.List;

/**
 * A custom view that enables the placement of a number of POIs on top of an image and allows
 * interaction with them.
 */
public class ImageMapView extends TileView {
    private static final String TAG = ImageMapView.class.getName();

    private Paint paint;
    private List<POI> poiList;
    private OnPoiSelectedListener onPoiSelectedListener;
    private OnSelectionCanceledListener onSelectionCanceledListener;
    private OnEmptyAreaTapListener onEmptyAreaTapListener;
    private TiledImage mImage;
    private CompositePathView.DrawablePath mSelectedPoiDrawblePath;

    private boolean poiTapConfirmed;        // flag to determine where a tap took place
    private boolean poiIsCurrentlySelected; // flag to determing if a POI is currently presented

    // A global info box to show a specific POI's info. Only one info box is visible at any time.
    // Using this global variable we are able to add it or remove it when needed.
    private ExhibitBrowserInfoBox mInfoBox;

    private final int COLOR_LINE = ContextCompat.getColor(getContext(), R.color.imageMapLine);
    private final int COLOR_FILL_SELECTED = ContextCompat.getColor(getContext(), R.color.imageMapFillSelected);
    private final int COLOR_FILL_VISITED = ContextCompat.getColor(getContext(), R.color.imageMapFillVisited);

    public ImageMapView(Context context) {
        super(context);
        setup();
    }

    public ImageMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public ImageMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        //Setup the paint
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));

        mSelectedPoiDrawblePath = new CompositePathView.DrawablePath();
        mSelectedPoiDrawblePath.paint = new Paint(paint);
        mSelectedPoiDrawblePath.paint.setColor(COLOR_FILL_SELECTED);
        mSelectedPoiDrawblePath.paint.setStyle(Paint.Style.FILL);

        // Initialize the global info box, used to present a POI's info
        mInfoBox = new ExhibitBrowserInfoBox(getContext());

        // Setup the tileview
        this.setSaveEnabled(true);
        this.setBitmapProvider(new BitmapProviderGlide());
        this.setScaleLimits(0, 2);

        //TODO: Need to change this to FIT, but we have to fix bug where two images are visible
        this.setMinimumScaleMode(MinimumScaleMode.FIT);
    }

    /**
     * Set the image of the ImageMapView from a file in the sdcard
     * @param imagePath the absolute path of the image
     */
    public void setImage(String imagePath) {
        mImage = new TiledImage(imagePath);

        // Set the size of the tileview
        this.setSize(mImage.getWidth(), mImage.getHeight());
        // and use a single tile
        this.addDetailLevel(1f, mImage, mImage.getTileWidth(), mImage.getTileHeight());

        // set mScale to 0, but keep scaleToFit true, so it'll be as small as possible but still match the container
        this.setScale(0);
        // let's use 0-1 positioning...
//        this.defineBounds(0, 0, 1, 1);
        // frame to center
        frameTo(0.5, 0.5);
        // render while panning
        this.setShouldRenderWhilePanning(true);
        // allow to scale back to 0 when double tap on full zoom
        this.setShouldLoopScale(true);
    }

    public void setPoiList(List<POI> poiList) {
        this.poiList = poiList;

        // Draw these POIs on the image
        drawPois();
    }

    public void addInfoBox(POI poi, String title, String text) {
        // Using the global info box to be able to remove it afterwards

        // set the title and text of the info box
        mInfoBox.setTitle(title);
        mInfoBox.setText(text);

        // retrieve the center of the POI that the info box refers to
        double[] position = getPoiCenter(poi);
        double positionX = position[0];
        double positionY = position[1];

        // the anchor of the infobox (the point of the infobox which will moved to the position)
        float anchorX, anchorY;

        // set the position of the infobox's nub and the respective anchor depending on the position
        if (positionX < 200) {
            mInfoBox.setNubPosition(ExhibitBrowserInfoBox.NUB_POSITION_LEFT);
            anchorX = 0f;
            anchorY = -0.5f;
        }
        else if (positionX > (mImage.getWidth()-200)) {
            mInfoBox.setNubPosition(ExhibitBrowserInfoBox.NUB_POSITION_RIGHT);
            anchorX = -1f;
            anchorY = -0.5f;
        }
        else if (positionY < 200) {
            mInfoBox.setNubPosition(ExhibitBrowserInfoBox.NUB_POSITION_TOP);
            anchorX = -0.5f;
            anchorY = 0f;
        }
        else {
            mInfoBox.setNubPosition(ExhibitBrowserInfoBox.NUB_POSITION_BOTTOM);
            anchorX = -0.5f;
            anchorY = -1f;
        }

        // center image to the info box's position
        slideToAndCenter(positionX, positionY);

        // add the info box as marker (stays put even when touching the tileview)
        addMarker(mInfoBox, positionX, positionY, anchorX, anchorY);

        // animate the infobox
        mInfoBox.transitionIn();
    }

    public void setOnPoiSelectedListener(OnPoiSelectedListener onPoiSelectedListener) {
        this.onPoiSelectedListener = onPoiSelectedListener;
    }

    public interface OnPoiSelectedListener {
        void onPoiSelected(POI poi);
    }

    public void setOnSelectionCanceled(OnSelectionCanceledListener onSelectionCanceledListener) {
        this.onSelectionCanceledListener = onSelectionCanceledListener;
    }

    public interface OnSelectionCanceledListener {
        void onSelectionCanceled();
    }

    public void setOnEmptyAreaTapListener(OnEmptyAreaTapListener onEmptyAreaTapListener) {
        this.onEmptyAreaTapListener = onEmptyAreaTapListener;
    }

    public interface OnEmptyAreaTapListener {
        void onEmptyAreaTapped();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        // Clear the POI tap confirmation flag to check if this was a tap on a POI
        poiTapConfirmed = false;

        // If there is a POI currently presented, clear this
        if (poiIsCurrentlySelected) {
            // Remove the "selected" region from the view on any tap
            removePath(mSelectedPoiDrawblePath);
            // Remove the currently show info box
            removeMarker(mInfoBox);

            poiIsCurrentlySelected = false;
            if (onSelectionCanceledListener != null) {
                onSelectionCanceledListener.onSelectionCanceled();
            }
        }
        // If no POI is selected, handle this as an "empty area" tap
        else {
            // Wait for some time to see if this was a tap on a POI
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // If it was actually an "empty area" tap, then use the listener if available
                    if (!poiTapConfirmed && onEmptyAreaTapListener != null) {
                        onEmptyAreaTapListener.onEmptyAreaTapped();
                    }
                }
            }, 50);
        }

        return super.onSingleTapConfirmed(event);
    }

    private void drawPois() {
        for(final POI poi : this.poiList) {
            // Draw the marker around the POI
            this.drawPath(getDrawAblePathFromPoi(poi, USE_MARKER));

            // If POI is visited before, mark is as visited
            if (poi.isVisited()) {
                this.drawPath(getDrawAblePathFromPoi(poi, USE_VISITED));
            }

            // Create a hotspot for each one of the POIs, so that we can handle taps on POIs
            HotSpot hotSpot = new HotSpot();
            //hotSpot.setTag(poi);
            hotSpot.setPath(getTransformedPathFromPoi(poi), new Region(0,0,mImage.getWidth(),mImage.getHeight()));
            // Set the listener for this hotspot
            hotSpot.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
                @Override
                public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                    // Set the flags
                    poiTapConfirmed = true;
                    poiIsCurrentlySelected = true;

                    // When one POI is selected, reset the "selected" region to show that on the view
                    removePath(mSelectedPoiDrawblePath);
                    mSelectedPoiDrawblePath.path = getTransformedPathFromPoi(poi);
                    drawPath(mSelectedPoiDrawblePath);

                    // Use the onPoiSelectedListener if set
                    if (onPoiSelectedListener != null) onPoiSelectedListener.onPoiSelected(poi);
                }
            });
            // And finally add the hotspot on the view (this is transparent to the user)
            this.addHotSpot(hotSpot);
        }
    }

    private Path getTransformedPathFromPoi(POI poi) {
        Matrix matrix = new Matrix();
        matrix.postScale(1f/mImage.getSampleSize(), 1f/mImage.getSampleSize());

        Path transformedPath = new Path();
        poi.getPath().transform(matrix, transformedPath);

        return transformedPath;
    }

    private static final int USE_MARKER = 0;
    private static final int USE_VISITED = 1;
    private CompositePathView.DrawablePath getDrawAblePathFromPoi(POI poi, int use) {
        Matrix matrix = new Matrix();
        matrix.postScale(1f/mImage.getSampleSize(), 1f/mImage.getSampleSize());

        Path transformedPath = new Path();
        poi.getPath().transform(matrix, transformedPath);

        final CompositePathView.DrawablePath drawablePath = new CompositePathView.DrawablePath();
        drawablePath.path = transformedPath;
        drawablePath.paint = new Paint(paint);

        switch (use) {
            case USE_MARKER:
                drawablePath.paint.setColor(COLOR_LINE);
                drawablePath.paint.setStyle(Paint.Style.STROKE);
                break;
            case USE_VISITED:
                drawablePath.paint.setColor(COLOR_FILL_VISITED);
                drawablePath.paint.setStyle(Paint.Style.FILL);
                break;
            default:
                break;
        }

        return drawablePath;
    }

    private double[] getPoiCenter(POI poi) {
        double[] center = new double[2];

        // Use transformed path to align with current image dimensions
        Path transformedPath = getTransformedPathFromPoi(poi);

        RectF mRectF = new RectF();
        transformedPath.computeBounds(mRectF, true);
        center[0] = mRectF.centerX();
        center[1] = mRectF.centerY();

        return center;

    }

    /**
     * This is a convenience method to scrollToAndCenter after layout
     * (which won't happen if called directly in onCreate)
     * see https://github.com/moagrius/TileView/wiki/FAQ
     */
    private void frameTo( final double x, final double y ) {
        this.post( new Runnable() {
            @Override
            public void run() {
                ImageMapView.this.scrollToAndCenter(x, y);
            }
        });
    }
}