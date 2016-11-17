package com.example.ed.pokemon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>, OnMapReadyCallback {

    private static String TAG = "JSONmain";
    private Button bb1;
    private Marker player;
    private List<Marker> salvajes;
    private List<Marker> tiendas;

    private ProgressDialog pDialog;
    private List<Coordenadas> coordList;
    private List<Poke> pokeList;

    private int ncrdL = 0;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest aLocationRequest;
    private LocationSettingsRequest nLocationSettingsRequest;
    private LocationListener locationListener;

    private static final int REQUEST_CHECK_SETTINGS = 123;

    public static final long UPDATE_INTERNAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERNAL_IN_MILLISECONDS =
            UPDATE_INTERNAL_IN_MILLISECONDS / 2;

    private GoogleMap googleMap;
    private static double initLat;
    private static double initLong;
    private final int MY_PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationListener = this;
        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

        List<Entrenador> trainerList = new Select().from(Entrenador.class).queryList();
        bb1= (Button) findViewById(R.id.button4);
        bb1.setText("Equipo de "+trainerList.get(0).Nombre);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        aLocationRequest = new LocationRequest();
        aLocationRequest.setInterval(UPDATE_INTERNAL_IN_MILLISECONDS);
        aLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERNAL_IN_MILLISECONDS);
        aLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        salvajes = new ArrayList<Marker>();
        tiendas=new ArrayList<Marker>();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    public  void onClickTeam(View view){
        Intent j = new Intent(MainActivity.this,Team.class);
        startActivity(j);
    }
    public void onClickMochila(View view) {
        Intent j = new Intent(MainActivity.this,Mochila.class);
        startActivity(j);
    }

    /*
    public void onClickObtener(View view) {
        Log.d(TAG,"en OBTENER");
        if (VerificarRed() == true) {
            //if(spriteList.isEmpty()){Log.d(TAG,"LA LISTA DE SPRITES ESTA VACIA");}

            new getSpriteURList().execute();
            while(pokeList.size()<10){
                //Log.d(TAG,"waiting for stuff")
            }
            pokeList = new Select().from(Poke.class).queryList();
            if(!(pokeList.isEmpty())){
                Log.d(TAG,"PASO EL IF");
                for(int i=0;i<pokeList.size();i++){
                    Log.d(TAG,"Accediendo pokeList: "+i);
                    new loadSpritesIntoList().execute(String.valueOf(pokeList.get(i)));
                }
            }else{
                Log.d(TAG,"SHT HAPPENED en el onclick obtener, la lista esta vacia");
            }

        }

    }*/
    /*
    public void onClickLoc(View view) {
        Log.d(TAG,"en Loc");
        if (VerificarRed() == true) {
            new getData().execute();
        }

    }
    */

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();}
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "????", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);

            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            Log.d(TAG,"consegui locaciones");
            initLat=lastLocation.getLatitude();
            initLong=lastLocation.getLongitude();
            if (VerificarRed() == true) {
                new getData().execute();
                new getPokestops().execute();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {Log.d(TAG, "onConnectionSuspended");}

    @Override
    public void onMapReady(GoogleMap ggMap) {
        this.googleMap = ggMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d("DEBUG MARKER","__succesfull click");
                if (marker.getTitle().equals("Pokemon salvaje"))
                {
                    //Toast.makeText(MainActivity.this, "Pokedex number: "+marker.getSnippet(), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MainActivity.this,Battle.class);
                    i.putExtra("valor1",marker.getSnippet());
                    startActivity(i);
                    return true;
                }else{
                    if(marker.getTitle().equals("Pokestop")){
                        //Toast.makeText(MainActivity.this, ""+marker.getSnippet(), Toast.LENGTH_LONG).show();
                        if( Integer.parseInt(marker.getSnippet()) %2==0){
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("PokeStop #"+marker.getSnippet())
                                    .setMessage("¿Quieres usar esta poke parada?")
                                    .setIcon(R.drawable.icon_potion)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            List<Entrenador> trainer = new Select().from(Entrenador.class).queryList();
                                            SQLite.update(Entrenador.class).set(Entrenador_Table.potion.eq(trainer.get(0).potion+3)).where(Entrenador_Table.id.is(trainer.get(0).id)).async().execute();
                                            Toast.makeText(MainActivity.this, "conseguiste pociones", Toast.LENGTH_SHORT).show();
                                            marker.remove();
                                        }}).setNegativeButton(android.R.string.no, null).show();
                        }else{
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("PokeStop #"+marker.getSnippet())
                                    .setMessage("¿Quieres usar esta poke parada?")
                                    .setIcon(R.drawable.icon_pokeball)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            List<Entrenador> trainer = new Select().from(Entrenador.class).queryList();
                                            SQLite.update(Entrenador.class).set(Entrenador_Table.pokeball.eq(trainer.get(0).pokeball+3)).where(Entrenador_Table.id.is(trainer.get(0).id)).async().execute();
                                            Toast.makeText(MainActivity.this, "conseguiste pokebolas", Toast.LENGTH_SHORT).show();
                                            marker.remove();
                                        }}).setNegativeButton(android.R.string.no, null).show();
                        }
                        return true;
                    }else{
                        return false;
                    }
                }


            }
        });
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d("DEBUG LocButton","__succesfull click");
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, aLocationRequest
                            , (com.google.android.gms.location.LocationListener) locationListener);
                }
                return false;
            }

        });



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onConnectionFailed" + connectionResult.toString());
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        Log.d(TAG, "onResult" + status.getStatusCode());
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "????", Toast.LENGTH_LONG).show();
                        return;
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, aLocationRequest, this);
                }

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //Toast.makeText(this,"LocationSettingsStatusCodes.RESOLUTION_REQUIRED",Toast.LENGTH_LONG).show();
                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Toast.makeText(this, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (mGoogleApiClient.isConnected()) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "????", Toast.LENGTH_LONG).show();
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, aLocationRequest, this);
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "disabled??", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "MainActivity onRequestPermissionsResult requestCode" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                //If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Perfecto", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "???", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        //List<Marker> markers = new ArrayList<Marker>();
        if (googleMap != null) {
            //googleMap.
            if(player != null){
                //player.remove();
                player.remove();
                player=null;
            }
            if(player ==null){
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                player = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("Player")
                                .snippet("Aqui estoy")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.sprite_1))
                        //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                );
            }

            /*
            if(!salvajes.isEmpty()){
                for(int i=0;i<3;i++){
                    salvajes.get(i).remove();
                }
                salvajes.clear();
            }
            if(salvajes.isEmpty()){
                for(int i=0;i<3;i++){
                    Double ltd,longtd;
                    ltd=   location.getLatitude()+ (Math.random()*((0.02)))-0.01;
                    longtd=location.getLongitude()+ (Math.random()*((0.02)))-0.01;
                    Marker encuentro=googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ltd,longtd))
                                    .title("Pokemon salvaje")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sprite_2))
                            //.icon(BitmapDescriptorFactory.fromBitmap(spriteList.get(i))));
                    );
                    salvajes.add(encuentro);
                }
            }*/


        }

    }


    private class getData extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String response=GetDatos();
            coordList = new ArrayList<Coordenadas>();
            if(response!=null){
                try {
                    JSONArray jArray = new JSONArray(response);
                    Log.d(TAG,"response: "+jArray.length());
                    ncrdL=jArray.length();
                    for(int i=0;i<jArray.length();i++){
                        JSONObject c=jArray.getJSONObject(i);
                        coordList.add(new Coordenadas(c.getString("lt"),c.getString("lng")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG,"response is null");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing())
                pDialog.dismiss();


            if (googleMap != null) {


                if(!salvajes.isEmpty()){
                    for(int i=0;i<salvajes.size();i++){
                        salvajes.get(i).remove();
                    }
                    salvajes.clear();

                }
                pokeList = new Select().from(Poke.class).queryList();
                int rnd_poke;
                if(salvajes.isEmpty()){
                    for(int i=0;i<ncrdL;i++){
                        Double ltd,longtd;
                        //ltd=Double.parseDouble(coordList.get(i).getLatitude());
                        //longtd=Double.parseDouble(coordList.get(i).getLongitude());
                        ltd=   initLat+ (Math.random()*((0.02)))-0.01;
                        longtd=initLong+ (Math.random()*((0.02)))-0.01;
                        rnd_poke= (int) (Math.random()*pokeList.size());

                        try{
                            Marker encuentro=googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ltd,longtd))
                                    .title("Pokemon salvaje")
                                    .snippet(""+pokeList.get(rnd_poke).id)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sprite_2))
                            );
                            new loadImageFromNetwork(encuentro).execute(pokeList.get(rnd_poke).ImgFront);
                            salvajes.add(encuentro);
                            //Toast.makeText(MainActivity.this,"created "+encuentro.getTitle()+" "+encuentro.getSnippet(),Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Log.d("Sht Happened",e.getLocalizedMessage());
                            Toast.makeText(MainActivity.this,"DAMN SON "+i,Toast.LENGTH_SHORT).show();
                        }

                    }
                }


            }
        }

    }

    private class getPokestops extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog=new ProgressDialog(MainActivity.this);
            //pDialog.setMessage("Please Wait there..");
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String response=GetDatos();
            coordList = new ArrayList<Coordenadas>();
            if(response!=null){
                try {
                    JSONArray jArray = new JSONArray(response);
                    Log.d(TAG,"response: "+jArray.length());
                    //ncrdL=jArray.length();
                    for(int i=0;i<jArray.length();i++){
                        JSONObject c=jArray.getJSONObject(i);
                        coordList.add(new Coordenadas(c.getString("lt"),c.getString("lng")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG,"response is null");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //if(pDialog.isShowing())
              //  pDialog.dismiss();


            if (googleMap != null) {
                if(!tiendas.isEmpty()){
                    for(int i=0;i<tiendas.size();i++){
                        tiendas.get(i).remove();
                    }
                    tiendas.clear();

                }
                //pokeList = new Select().from(Poke.class).queryList();
                if(tiendas.isEmpty()){
                    for(int i=0;i<5;i++){
                        Double ltd,longtd;
                        //ltd=Double.parseDouble(coordList.get(i).getLatitude());
                        //longtd=Double.parseDouble(coordList.get(i).getLongitude());
                        ltd=   initLat+ (Math.random()*((0.02)))-0.01;
                        longtd=initLong+ (Math.random()*((0.02)))-0.01;

                        try{
                            Marker edificio=googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ltd,longtd))
                                    .title("Pokestop")
                                    .snippet(""+i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sprite_3))
                            );
                            //new loadImageFromNetwork(edificio).execute(pokeList.get(i).ImgFront);
                            tiendas.add(edificio);
                            //Toast.makeText(MainActivity.this,"created "+encuentro.getTitle()+" "+encuentro.getSnippet(),Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Log.d("Sht Happened",e.getLocalizedMessage());
                            Toast.makeText(MainActivity.this,"DAMN SON "+i,Toast.LENGTH_SHORT).show();
                        }

                    }
                }


            }
        }

    }

    protected static String GetDatos(){
        Log.d(TAG,"get");
        String response=null;

        Log.d("ONMAPREADY","LAT="+initLat+"LONG="+initLong);
        try {
            URL utl=null;
            utl=new URL("http://190.144.171.172/function.php?lat="+initLat+"&lng="+initLong);
            URLConnection uc=utl.openConnection();
            BufferedReader in=new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while((inputLine=in.readLine())!=null){
                //Log.d(TAG,inputLine);
                response=inputLine;
            }
            in.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private class loadImageFromNetwork extends AsyncTask<String,Void,Bitmap>{
        public Marker mk;
        public loadImageFromNetwork(Marker marker)
        {this.mk=marker;}
        @Override
        protected Bitmap doInBackground(String... urls){
            Bitmap bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(urls[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
                //Log.d(TAG,e.getLocalizedMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            Log.d(TAG,"fuckmylife, im in loadImageFromNetwork");
            mk.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));

        }
    }


    public boolean VerificarRed(){
        ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected()){
            Toast.makeText(this,"Network is Available",Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(this,"Network is Unavailable",Toast.LENGTH_LONG).show();
            return false;
        }
    }

}