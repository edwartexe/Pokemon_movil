package com.example.ed.pokemon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class StartScreen extends AppCompatActivity {
    private static String TAG = "StartupScreen";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

        if (VerificarRed()) {;
            new getSpriteURList().execute();
        }
    }


    public void newGame(View view) {

        Intent i = new Intent(this,CharacterSelect.class);
        startActivity(i);
    }

    public void oldGame(View view) {
        Intent j = new Intent(this,MainActivity.class);
        startActivity(j);
    }

    private class getSpriteURList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG,"getSpriteURL activate!!");
            pDialog=new ProgressDialog(StartScreen.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pokes=getPokemon();
            String crappyPrefix = "null";
            if(pokes.startsWith(crappyPrefix)){
                pokes = pokes.substring(crappyPrefix.length(), pokes.length());
            }
            Log.d(TAG,"POKEMON URL LIST-BRACE YOURSELVES:"+pokes);
            if(pokes!=null){
                try {
                    JSONArray jArray = new JSONArray(pokes);
                    Log.d(TAG,"POKEMON INCOMING"+jArray.getJSONObject(0));
                    Poke pokedex;
                    Log.d(TAG,"pokes: "+jArray.length());
                    for(int i=0;i<jArray.length();i++){
                        Log.d(TAG, "step0");
                        JSONObject p=jArray.getJSONObject(i);
                        Log.d(TAG, "step1");
                        //Poke pk = new Poke(p.getInt("id"),p.getString("ImgFront")); //pre DBFLOW
                        //pokeList.add(pk); //PRE DBFLOW
                        //pokedex = new Poke(p.getInt("id"),p.getString("ImgFront"));
                        pokedex = new Poke(p.getInt("id"), p.getString("name"),p.getString("type"), p.getString("strength"), p.getString("weakness"), p.getInt("hp_max"), p.getInt("ataque_max"), p.getInt("defensa_max"), p.getString("ImgFront"),p.getString("ImgBack"),p.getInt("ev_id"));
                        Log.d(TAG, "step2");
                        pokedex.save();

                        Log.d(TAG,"Se ha cargado el url: "+i);
                    }
                    Log.d(TAG, "step 3");
                } catch (Exception e) {
                    pDialog.dismiss();
                    Log.d("pokeList Item ", ""+e.getMessage()+"\n"+e.getStackTrace());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG,"post executing this shit");
            if(pDialog.isShowing())
                pDialog.dismiss();

        }

    }



    protected static String getPokemon(){
        Log.d(TAG,"getPokemon");
        String response=null;
        try {
            URL utl=null;
            utl=new URL("http://realizzazione-sito.tk/dev/service/pokemonGo.json");
            URLConnection uc=utl.openConnection();
            BufferedReader in=new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while((inputLine=in.readLine())!=null){
                //Log.d(TAG,inputLine);
                response+=inputLine;
            }
            in.close();
            Log.d(TAG,"getPokemon response: "+response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"getPokemon did not work... "+response);
            return null;
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
