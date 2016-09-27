package com.example.ed.pokemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class Battle extends AppCompatActivity {
    private List<Poke> pokeList;
    private List<Equipo> teamList;
    private List<Entrenador> trainer;
    private TextView tv1,tv2;

    ImageView bg,pk1,pk2;
    long my_id,wild_id;
    int  wild_hp,wild_c_hp, wild_atk,wild_def,wild_speed;
    int my_hp, my_c_hp,my_atk,my_def,my_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

        bg= (ImageView) findViewById(R.id.imageView);
        pk1= (ImageView) findViewById(R.id.imageView2);
        pk2= (ImageView) findViewById(R.id.imageView3);

        tv1= (TextView) findViewById(R.id.textView);
        tv2= (TextView) findViewById(R.id.textView2);

        //pk1.setImageResource(R.drawable.myimage);
        //pk2.setImageResource(R.drawable.myimage);
       Intent i = getIntent();
        long temp_id= Long.parseLong(i.getStringExtra("valor1"));

        teamList = new Select().from(Equipo.class).where(Equipo_Table.Lead.eq(true)).queryList();
        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(teamList.get(0).Pokedex_id)).queryList();
        my_hp= teamList.get(0).HP;
        my_c_hp=teamList.get(0).Current_HP;
        my_atk= teamList.get(0).ATK;
        my_def= teamList.get(0).DEF;
        my_speed= teamList.get(0).SPEED;
        new loadSpriteFromNetwork(pk1).execute(pokeList.get(0).ImgBack);

        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(temp_id)).queryList();
        wild_id=pokeList.get(0).id;
        wild_hp= (int) ((Math.random()*((pokeList.get(0).HP_max/2-1)))+pokeList.get(0).HP_max/2);
        wild_c_hp=wild_hp;
        wild_atk= (int) ((Math.random()*((pokeList.get(0).ATK_max/2-1)))+pokeList.get(0).ATK_max/2);
        wild_def= (int) ((Math.random()*((pokeList.get(0).DEF_max/2-1)))+pokeList.get(0).DEF_max/2);
        wild_speed= (int) ((Math.random()*((10-1)))+1);
        new loadSpriteFromNetwork(pk2).execute(pokeList.get(0).ImgFront);
        //Toast.makeText(this,"pokemon enemigo, id= "+pokeList.get(0).id+" and its atk is:"+pokeList.get(0).ATK_max,Toast.LENGTH_LONG).show();

        tv1.setText("Enemy Hp: "+wild_c_hp);
        tv2.setText("My Hp: "+my_c_hp);
    }

    public void onClickAtacar(View view) {
        capturado();
    }
    public void onClickItem(View view) {
    }
    public void onClickHuir(View view) {
    }
    public void onClickCambiar(View view) {
    }

    public void capturado(){
        trainer = new Select().from(Entrenador.class).queryList();
        teamList = new Select().from(Equipo.class).queryList();

        int pokeball_amount=trainer.get(0).pokeball-1;
        Equipo atrapado = new Equipo(teamList.size(), wild_id, wild_hp, wild_atk, wild_def, wild_speed, false);
        atrapado.save();
        Toast.makeText(this,"Lo Atrapaste!!",Toast.LENGTH_SHORT).show();
        /*try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        finish();
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
