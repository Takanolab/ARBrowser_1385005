package com.paar.ch9;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jp.androidgroup.nyartoolkit.NyARToolkitAndroidActivity;
import jp.androidgroup.nyartoolkit.R;
import jp.androidgroup.nyartoolkit.SdLog;
import jp.androidgroup.nyartoolkit.utils.camera.CameraPreview;

import org.apache.lucene.analysis.PorterStemmer;
import org.takanolab.ar.db.MyDbAdapter;
import org.takanolab.ar.db.StopwordsHelper;

import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AugmentedActivity {
    private static final String TAG = "MainActivity";
    private static final String locale = "en";
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
	private static final Map<String,NetworkDataSource> sources = new ConcurrentHashMap<String,NetworkDataSource>(); 
    private static CameraSurface camera = null;
    CameraPreview _camera_preview;
	
	private MyDbAdapter mDbHelper;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		//sleep(4000);
        super.onCreate(savedInstanceState);
        
        LocalDataSource localData = new LocalDataSource(this.getResources());
        ARData.addMarkers(localData.getMarkers());
        
//        NetworkDataSource twitter = new TwitterDataSource(this.getResources());
//        sources.put("twitter",twitter);
//        NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
//        sources.put("wiki",wikipedia);
        
        // Show marker
        
	}

	@Override
    public void onStart() {
        super.onStart();
        
//        Location last = ARData.getCurrentLocation();
//        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_quest, menu);
        return true;
    }

    Intent intent = null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() item="+item);
        switch (item.getItemId()) {
            case R.id.showRadar:
                showRadar = !showRadar;
                item.setTitle(((showRadar)? "Hide" : "Show")+" Radar");
                return true;
                
            case R.id.showZoomBar:
                showZoomBar = !showZoomBar;
                item.setTitle(((showZoomBar)? "Hide" : "Show")+" Zoom Bar");
                zoomLayout.setVisibility((showZoomBar)?LinearLayout.VISIBLE:LinearLayout.GONE);
                return true;
                
            case R.id.quest_to_cg:
            	// change to NyARToolkit activity
                Intent cgintent = new Intent(com.paar.ch9.MainActivity.this, jp.androidgroup.nyartoolkit.NyARToolkitAndroidActivity.class);
                startActivity(cgintent);
                //カメラの停止
//				try {
//					camera.onAcStop();
//				} catch (Exception e) {
//					// TODO 自動生成された catch ブロック
//					e.printStackTrace();
//				}
//            	if( camera != null ){ 
//					camera.stopPreview();
//	                camera.setPreviewCallbackWithBuffer(null);
//	            	try {
//						camera.setPreviewDisplay(null);
//					} catch (IOException e) {
//						// TODO 自動生成された catch ブロック
//						e.printStackTrace();
//					}
//	            	camera.release();
//	                camera = null;
//            	}
//				finish();
                return true;
                
            case R.id.quest_to_search:
            	// change to search activity
                intent = new Intent(MainActivity.this, org.takanolab.ar.search.SearchActivity.class);
                startActivity(intent);
                SdLog.put("StartSearchMode");
                return true;
                
            case R.id.exit:
            	moveTaskToBack(true);
                break;
        }
        return false;
    }

	@Override
	public void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
		try {
			camera.onAcStop();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
//        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
//        Log.v(TAG, "Latitude="+location.getLatitude());
//        Log.v(TAG, "Longtitude="+location.getLongitude());
//        Log.v(TAG, "Altitude="+location.getAltitude());
    }

	@Override
	protected void markerTouched(Marker marker) {
//        Toast t = Toast.makeText(getApplicationContext(), marker.getName(), Toast.LENGTH_SHORT);
		Toast t = Toast.makeText(getApplicationContext(), marker.getDescription(), Toast.LENGTH_SHORT);
		
		SdLog.put("AnnotationTouch," + marker.name);
		
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        
        String description = marker.getDescription();
        PorterStemmer s = new PorterStemmer();
        StopwordsHelper stopwordsHelper = new StopwordsHelper();
        
        String[] splitedTerms = description.split(" ");
        for (String aTerm : splitedTerms) {

            // remove stop words
            if (stopwordsHelper.isStopword(aTerm.toLowerCase())) {
                    continue;
            }

            // stemming
            System.out.println(s.stem(aTerm.toLowerCase()));
            
            // store into database
        }

	}

    @Override
	protected void updateDataOnZoom() {
	    super.updateDataOnZoom();
	    
	    Log.v(TAG, "I'm at UpdateOnZoom");
//        Location last = ARData.getCurrentLocation();
//        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
	}
    
    //private void updateData(final double lat, final double lon, final double alt) {
    private void updateData(final double lat, final double lon, final double alt) {
        Log.i(TAG, "Latitude="+ lat);
        Log.i(TAG, "Longtitude="+lon);
        Log.i(TAG, "Altitude="+alt);
        
        final double lat2 = 35.48603;
        final double lon2 = 139.341198;
        final double alt2 = 0.0;
    	try {
            exeService.execute(
                new Runnable() {
                    
                    public void run() {
                        for (NetworkDataSource source : sources.values()) {
                            download(source, lat, lon, alt);
                        	//download(source, lat2, lon2, alt2);
                        }
                    }
                }
            );
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.",e);
        }
    }
    
    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
		if (source==null) return false;
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
			Log.v(TAG, "url="+url);
		} catch (NullPointerException e) {
			return false;
		}
    	
		List<Marker> markers = null;
		try {
			markers = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}

    	ARData.addMarkers(markers);
    	return true;
    }
    
    public synchronized void sleep(long msec)
    {	
    	try
    	{
    		wait(msec);
    	}catch(InterruptedException e){}
    }
    
}