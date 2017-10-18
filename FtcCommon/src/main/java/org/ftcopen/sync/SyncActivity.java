package org.ftcopen.sync;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.qualcomm.ftccommon.R;

import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManager;

public class SyncActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DeviceNameManager.Callback {

  private GoogleApiClient googleApiClient;
  private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallbackImpl();
  private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallbackImpl();
  private String deviceName;
  private static final String SERVICE_ID = "org.ftcopen.sync";
  private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sync);
    DeviceNameManager.getInstance().registerCallback(this);
    googleApiClient = new GoogleApiClient.Builder(this)
        .addOnConnectionFailedListener(this)
        .addConnectionCallbacks(this)
        .addApi(Nearby.CONNECTIONS_API)
        .build();
  }


  @Override
  public void onStart() {
    super.onStart();
    googleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (googleApiClient != null && googleApiClient.isConnected()) {
      googleApiClient.disconnect();
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    startAdvertising();
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  private void startAdvertising() {
    Nearby.Connections.startAdvertising(
        googleApiClient,
        deviceName,
        SERVICE_ID,
        connectionLifecycleCallback,
        new AdvertisingOptions(STRATEGY))
      .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
        @Override
        public void onResult(@NonNull Connections.StartAdvertisingResult result) {
          if (result.getStatus().isSuccess()) {
            // We're advertising!
          } else {
            // We were unable to start advertising.
          }
        }
      });
  }

  private void startDiscovery() {
    Nearby.Connections.startDiscovery(
        googleApiClient,
        SERVICE_ID,
        endpointDiscoveryCallback,
        new DiscoveryOptions(STRATEGY))
      .setResultCallback( new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status status) {
          if (status.isSuccess()) {
            // We're discovering!
          } else {
            // We were unable to start discovering.
          }
        }
      });
  }

  private void requestConnection(String id) {
    Nearby.Connections.requestConnection(
        googleApiClient,
        deviceName,
        id,
        connectionLifecycleCallback)
      .setResultCallback(new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status status) {
          if(status.isSuccess()) {
            // We successfully requested a connection. Now both sides
            // must accept before the connection is established.
          } else {
            // Connection request failed
          }
        }
      });

  }

  @Override
  public void onDeviceNameChanged(String newDeviceName) {
    deviceName = newDeviceName;
  }


  private class ConnectionLifecycleCallbackImpl extends ConnectionLifecycleCallback {

    @Override
    public void onConnectionInitiated(String s, ConnectionInfo connectionInfo) {

    }

    @Override
    public void onConnectionResult(String s, ConnectionResolution connectionResolution) {

    }

    @Override
    public void onDisconnected(String s) {

    }
  }

  private class EndpointDiscoveryCallbackImpl extends EndpointDiscoveryCallback {

    @Override
    public void onEndpointFound(String s, DiscoveredEndpointInfo discoveredEndpointInfo) {

    }

    @Override
    public void onEndpointLost(String s) {

    }
  }
}
