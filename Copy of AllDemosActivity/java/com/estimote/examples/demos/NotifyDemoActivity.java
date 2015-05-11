package com.estimote.examples.demos;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import static com.estimote.sdk.BeaconManager.MonitoringListener;

/**
 * Demo that shows how to use region monitoring. Two important steps are:
 * <ul>
 * <li>start monitoring region, in example in {@link #onResume()}</li>
 * <li>respond to monitoring changes by registering {@link MonitoringListener} in {@link BeaconManager}</li>
 * </ul>
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class NotifyDemoActivity extends Activity {

  private static final String TAG = NotifyDemoActivity.class.getSimpleName();
  private static final int NOTIFICATION_ID = 123;

  private BeaconManager beaconManager;
  private NotificationManager notificationManager;
  private Region region;
  private String myId;
  private String mac;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notify_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    Beacon beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    region = new Region("regionId", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    beaconManager = new BeaconManager(this);

    // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
    // In order for this demo to be more responsive and immediate we lower down those values.
    beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

    beaconManager.setMonitoringListener(new MonitoringListener() {
      @Override
      public void onEnteredRegion(Region region, List<Beacon> beacons) {
    	  
    	  Bundle userBundle = getIntent().getExtras();
		  myId = userBundle.getString("myId");
		  mac = userBundle.getString("mac");
    	  
    	    TextView statusTextView = (TextView) findViewById(R.id.status);
    	    
    	    statusTextView.setText("Your attendance has been recorded, " + myId + "!");
    	    new MyAsyncTask().execute(myId, mac);	
        onDestroy();
        //startOver();   
      }

      @Override
      public void onExitedRegion(Region region) {
 
      }
    });
    
    Button exitButton = (Button) findViewById(R.id.exitButton);
    
    exitButton.setOnClickListener(new View.OnClickListener() {
  		
  		@Override
  		public void onClick(View v) {
  			// TODO Auto-generated method stub
  			
  			startOver();
  			
  			
  			
  		}
  	});
  }
  
 

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();

    notificationManager.cancel(NOTIFICATION_ID);
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startMonitoring(region);
        } catch (RemoteException e) {
          Log.d(TAG, "Error while starting monitoring");
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    notificationManager.cancel(NOTIFICATION_ID);
    beaconManager.disconnect();
    super.onDestroy();
  }
/*
  private void postNotification(String msg) {
    Intent notifyIntent = new Intent(NotifyDemoActivity.this, NotifyDemoActivity.class);
    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivities(
        NotifyDemoActivity.this,
        0,
        new Intent[]{notifyIntent},
        PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notification = new Notification.Builder(NotifyDemoActivity.this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentTitle("Notify Demo")
        .setContentText(msg)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build();
    notification.defaults |= Notification.DEFAULT_SOUND;
    notification.defaults |= Notification.DEFAULT_LIGHTS;
    notificationManager.notify(NOTIFICATION_ID, notification);

    TextView statusTextView = (TextView) findViewById(R.id.status);
    statusTextView.setText(msg);
  }
  */
  private void startOver(){
	  Intent a = new Intent(this, Login.class);
	  startActivity(a);
	  android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(1);
  }
  
  /*
  private void dbConnect(){
	  
	  try {
	        DefaultHttpClient client = new DefaultHttpClient();  
	        String postURL = "http://192.168.1.115:8080/ClassPassServletProject/attend";
	        HttpPost post = new HttpPost(postURL); 
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("myId", myId));
	            params.add(new BasicNameValuePair("mac", mac));
	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params);
	            post.setEntity(ent);
	            HttpResponse responsePOST = client.execute(post);  
	            HttpEntity resEntity = responsePOST.getEntity();  
	            if (resEntity != null) {    
	                Log.i("RESPONSE",EntityUtils.toString(resEntity));
	            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	 

  }
  */
  
  private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
	  
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0], params[1]);
			return null;
		}

		protected void onPostExecute(Double result){
			
			Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
		}
		

		public void postData(String myId, String mac) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://52.1.225.213:8080/ClassPassServletProject/attend");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("myId", myId));
				nameValuePairs.add(new BasicNameValuePair("mac", mac));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
}
  
}


