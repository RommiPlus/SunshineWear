package com.example.android.sunshine.app.wear;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.Constants;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class WearWeatherSyncService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public WearWeatherSyncService() {
        super("WearWeatherSyncService");
    }

    private static final String TAG = WearWeatherSyncService.class.getSimpleName();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSyncToWear(Context context) {
        Intent intent = new Intent(context, WearWeatherSyncService.class);
        context.startService(intent);
    }

    private static final String[] WEAR_WEATHER_PROJECTION = new String[] {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Log.i(TAG, "onHandleIntent: blockingConnect");
        mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        Log.i(TAG, "mGoogleApiClient isConnected: " + mGoogleApiClient.isConnected());

        if (mGoogleApiClient.isConnected()) {
            // Last sync was more than 1 day ago, let's send a notification with the weather.
            String locationQuery = Utility.getPreferredLocation(this);

            Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());

            // we'll query our contentProvider, as always
            Cursor cursor = getContentResolver().query(weatherUri, WEAR_WEATHER_PROJECTION, null, null, null);

            if (cursor.moveToFirst()) {
                int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                double high = cursor.getDouble(INDEX_MAX_TEMP);
                double low = cursor.getDouble(INDEX_MIN_TEMP);

                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEATHER_PATH);
                putDataMapRequest.getDataMap().putInt(Constants.KEY_WEATHER_ID, weatherId);
                putDataMapRequest.getDataMap().putDouble(Constants.KEY_HIGH_TEMP, high);
                putDataMapRequest.getDataMap().putDouble(Constants.KEY_LOW_TEMP, low);
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                request.setUrgent();
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                        .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
//                                if (!dataItemResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "buildWatchOnlyNotification(): status: " +
                                            dataItemResult.getStatus() +
                                            "\nStatus code: " + dataItemResult.getStatus().getStatusCode());
//                                }
                            }
                        });
            }
            cursor.close();

            mGoogleApiClient.disconnect();
            Log.i(TAG, "mGoogleApiClient disconnect");
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
