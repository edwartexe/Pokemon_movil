package com.example.ed.pokemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
    private List<Entrenador> trainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team_poke);

        //Material Design Widgets
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        trainer = new Select().from(Entrenador.class).queryList();

        new loadSpriteFromNetwork(iv).execute(pokeList.get(0).ImgFront);
        tv1.setText("Especie: "+pokeList.get(0).Name+"  #atrapado:"+teamList.get(0).Equipo_id);
        tv2.setText("HP: "+teamList.get(0).Current_HP+" / "+teamList.get(0).HP +" (MAX: "+pokeList.get(0).HP_max+")");
        tv3.setText("Ataque: "+teamList.get(0).ATK +" (MAX: "+pokeList.get(0).ATK_max+")");
        tv4.setText("Defensa: "+teamList.get(0).DEF +" (MAX: "+pokeList.get(0).DEF_max+")");
        tv5.setText("Velocidad: "+teamList.get(0).SPEED);
        tv6.setText("¿Pokemon Principal?: "+teamList.get(0).Lead);
    }

    public void updateStats(){
        teamList = new Select().from(Equipo.class).where(Equipo_Table.Equipo_id.eq(eq_id)).queryList();
        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(pk_id)).queryList();
        tv2.setText("HP: "+teamList.get(0).Current_HP+" / "+teamList.get(0).HP +" (MAX: "+pokeList.get(0).HP_max+")");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showcase_poke, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_potion:
                Toast.makeText(this,"POTION",Toast.LENGTH_LONG).show();
                int heal=teamList.get(0).Current_HP +(teamList.get(0).HP/2);
                if(heal>teamList.get(0).HP){heal=teamList.get(0).HP;}
                SQLite.update(Equipo.class)
                        .set(Equipo_Table.Current_HP.eq(heal))
                        .where(Equipo_Table.Equipo_id.is(eq_id))
                        .async()
                        .execute();
                int potion_amount=trainer.get(0).potion-1;
                SQLite.update(Entrenador.class)
                        .set(Entrenador_Table.potion.eq(potion_amount))
                        .where(Entrenador_Table.id.is(0))
                        .async()
                        .execute();
                tv2.setText("HP: "+heal+" / "+teamList.get(0).HP +" (MAX: "+pokeList.get(0).HP_max+")");
                //updateStats();
                return true;
            case R.id.nav_nleader:
                Toast.makeText(this,"LEADER",Toast.LENGTH_LONG).show();
                SQLite.update(Equipo.class)
                        .set(Equipo_Table.Lead.eq(false))
                        .where(Equipo_Table.Equipo_id.isNot(eq_id))
                        .async()
                        .execute();
                SQLite.update(Equipo.class)
                        .set(Equipo_Table.Lead.eq(true))
                        .where(Equipo_Table.Equipo_id.is(eq_id))
                        .async()
                        .execute();
                tv6.setText("¿Pokemon Principal?: "+"true");
                //updateStats();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
