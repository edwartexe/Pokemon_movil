package com.example.ed.pokemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ShowTeamPoke extends AppCompatActivity {
    private ImageView iv;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6;
    private long eq_id, pk_id;

    private List<Equipo> teamList;
    private List<Poke> pokeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team_poke);

        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

        iv= (ImageView) findViewById(R.id.imageView6);
        tv1= (TextView) findViewById(R.id.textView10);
        tv2= (TextView) findViewById(R.id.textView11);
        tv3= (TextView) findViewById(R.id.textView12);
        tv4= (TextView) findViewById(R.id.textView14);
        tv5= (TextView) findViewById(R.id.textView13);
        tv6= (TextView) findViewById(R.id.textView15);

        Intent i = getIntent();
        eq_id= i.getLongExtra("eqid",0);
        pk_id= i.getLongExtra("pkid",0);
        teamList = new Select().from(Equipo.class).where(Equipo_Table.Equipo_id.eq(eq_id)).queryList();
        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(pk_id)).queryList();

        new loadSpriteFromNetwork(iv).execute(pokeList.get(0).ImgFront);
        tv1.setText("Especie: "+pokeList.get(0).Name+" #atrapado:"+teamList.get(0).Equipo_id);
        tv2.setText("HP: "+teamList.get(0).Current_HP+" / "+teamList.get(0).HP +" (MAX: "+pokeList.get(0).HP_max+")");
        tv3.setText("Ataque: "+teamList.get(0).ATK +" (MAX: "+pokeList.get(0).ATK_max+")");
        tv4.setText("Defensa: "+teamList.get(0).DEF +" (MAX: "+pokeList.get(0).DEF_max+")");
        tv5.setText("Velocidad: "+teamList.get(0).SPEED);
        tv6.setText("¿Pokemon Principal?: "+teamList.get(0).Lead);

    }

    private class loadSpriteFromNetwork extends AsyncTask<String,Void,Bitmap> {
        public ImageView iv;
        public loadSpriteFromNetwork(ImageView imgView)
        {this.iv=imgView;}
        @Override
        protected Bitmap doInBackground(String... urls){
            Bitmap bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(urls[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            iv.setImageBitmap(bitmap);
        }
    }
}


