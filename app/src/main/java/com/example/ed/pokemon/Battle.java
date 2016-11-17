package com.example.ed.pokemon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Battle extends AppCompatActivity {
    private List<Poke> pokeList;
    private List<Equipo> teamList;
    private List<Entrenador> trainer;

    private ListView listv;
    private ArrayList<String> myList;
    private ArrayAdapter<String> myarrayAdapter;

    private TextView tv1,tv2;

    private ProgressDialog pDialog;

    ImageView bg,pk1,pk2;
    long my_id,wild_id;
    int my_select;
    int  wild_hp,wild_c_hp, wild_atk,wild_def,wild_speed;
    int my_hp, my_c_hp,my_atk,my_def,my_speed;
    String my_type, wild_type, my_str, wild_str, my_weak,wild_weak, my_name,wild_name;

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

        listv= (ListView) findViewById(R.id.listView2);
        myList=new ArrayList<String>();
        myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList);
        listv.setAdapter(myarrayAdapter);
        //listv.setTextFilterEnabled(true);


        //pk1.setImageResource(R.drawable.myimage);
        //pk2.setImageResource(R.drawable.myimage);
       Intent i = getIntent();
        long temp_id= Long.parseLong(i.getStringExtra("valor1"));

        loadMyPoke();

        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(temp_id)).queryList();
        wild_name=pokeList.get(0).Name;
        wild_id=pokeList.get(0).id;
        wild_hp= (int) ((Math.random()*((pokeList.get(0).HP_max/2-1)))+pokeList.get(0).HP_max/2);
        wild_c_hp=wild_hp;
        wild_atk= (int) ((Math.random()*((pokeList.get(0).ATK_max/2-1)))+pokeList.get(0).ATK_max/2);
        wild_def= (int) ((Math.random()*((pokeList.get(0).DEF_max/2-1)))+pokeList.get(0).DEF_max/2);
        wild_type=pokeList.get(0).Type;
        wild_speed= (int) ((Math.random()*((10-1)))+1);
        wild_str=pokeList.get(0).Fortaleza;
        wild_weak=pokeList.get(0).Debilidad;
        new loadSpriteFromNetwork(pk2).execute(pokeList.get(0).ImgFront);
        myList.add("Un "+wild_name+" salvaje aparecio");myarrayAdapter.notifyDataSetChanged();
        //Toast.makeText(this,"pokemon enemigo, id= "+pokeList.get(0).id+" and its atk is:"+pokeList.get(0).ATK_max,Toast.LENGTH_LONG).show();

        tv1.setText("Enemy Hp: "+wild_c_hp+" /"+wild_hp);
        tv2.setText("My Hp: "+my_c_hp+" /"+my_hp);
    }

    public void loadMyPoke(){
        teamList = new Select().from(Equipo.class).where(Equipo_Table.Lead.eq(true)).queryList();
        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(teamList.get(0).Pokedex_id)).queryList();
        my_name=pokeList.get(0).Name;
        my_id=teamList.get(0).Equipo_id;
        my_hp= teamList.get(0).HP;
        my_c_hp=teamList.get(0).Current_HP;
        my_atk= teamList.get(0).ATK;
        my_def= teamList.get(0).DEF;
        my_speed= teamList.get(0).SPEED;
        my_type=pokeList.get(0).Type;
        my_str=pokeList.get(0).Fortaleza;
        my_weak=pokeList.get(0).Debilidad;
        new loadSpriteFromNetwork(pk1).execute(pokeList.get(0).ImgBack);
        myList.add("Vamos, "+my_name+"!!");myarrayAdapter.notifyDataSetChanged();
        update_battle();
    }

    public void onClickAtacar(View view) {

        if(check_fainted_pokemon(my_c_hp,wild_c_hp)==0 || check_fainted_pokemon(my_c_hp,wild_c_hp)==2){
            if(my_speed >= wild_speed){
                myList.add("Tu "+my_name+" atacó primero");myarrayAdapter.notifyDataSetChanged();
                wild_c_hp=calcularDaño(wild_c_hp,my_atk,wild_def, wild_weak, wild_str, my_type);
                if(wild_c_hp>0){
                    myList.add(wild_name+" ataca de regreso");myarrayAdapter.notifyDataSetChanged();
                    my_c_hp=calcularDaño(my_c_hp,wild_atk,my_def, my_weak, my_str, wild_type);
                }
            }else{
                myList.add(wild_name+" atacó primero");myarrayAdapter.notifyDataSetChanged();
                my_c_hp=calcularDaño(my_c_hp,wild_atk,my_def, my_weak, my_str, wild_type);
                if(my_c_hp>0){
                    myList.add("Tu "+my_name+" contraataca");myarrayAdapter.notifyDataSetChanged();
                    wild_c_hp=calcularDaño(wild_c_hp,my_atk,wild_def, wild_weak, wild_str, my_type);
                }
            }
        }
        switch(check_fainted_pokemon(my_c_hp,wild_c_hp)){
            case 0://update battle
                update_battle();
                break;
            case 1: //change pokemon & update
                update_battle();
                if(isAnyoneOkay()){
                    myList.add(my_name+" fue vencido, usa otro pokemon");myarrayAdapter.notifyDataSetChanged();
                    theAmazingSwitch();
                }
                else{
                    Toast.makeText(this,"te derrotaron",Toast.LENGTH_SHORT).show();
                    finish();}
                break;
            case 2://battle ends
                myList.add("Wild pokemon has fainted");myarrayAdapter.notifyDataSetChanged();
                //Toast.makeText(this,"Wild pokemon has fainted",Toast.LENGTH_SHORT).show();
                update_battle();
                levelUP();
                break;
            default:
                Toast.makeText(this,"Unexpected Truce Happened",Toast.LENGTH_SHORT).show();
                update_battle();
        }
    }



    public void onClickItem(View view) {
        trainer = new Select().from(Entrenador.class).queryList();

        CharSequence allitems[]=new CharSequence[2];
            allitems[0]="Pokeball x"+trainer.get(0).pokeball;
            allitems[1]="Potions x"+trainer.get(0).potion;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Que objeto usaras?");
        builder.setItems(allitems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0: //usar pokebola
                        if(trainer.get(0).pokeball>0){
                            int pokeball_amount=trainer.get(0).pokeball-1;
                            SQLite.update(Entrenador.class).set(Entrenador_Table.pokeball.eq(pokeball_amount)).where(Entrenador_Table.id.is(trainer.get(0).id)).async().execute();
                            double rnd_mod=0+(Math.random()*1);
                            if(rnd_mod > wild_c_hp/wild_hp){
                                capturado();}
                            else{
                                myList.add("La pokebola fallo en atraparlo");myarrayAdapter.notifyDataSetChanged();
                                //Toast.makeText(Battle.this,"La pokebola fallo en atraparlo",Toast.LENGTH_SHORT).show();
                                }
                        }
                        else{
                            Toast.makeText(Battle.this,"NO TIENES POKEBOLAS!!",Toast.LENGTH_SHORT).show();}
                        break;
                    case 1://usar pocion
                        if(trainer.get(0).potion>0){
                            my_c_hp=my_c_hp+(my_hp/2);
                            myList.add("Te recuperaste con una pocion");myarrayAdapter.notifyDataSetChanged();
                            int potion_amount=trainer.get(0).potion-1;
                            SQLite.update(Entrenador.class).set(Entrenador_Table.potion.eq(potion_amount)).where(Entrenador_Table.id.is(trainer.get(0).id)).async().execute();
                            update_battle();
                        }
                        else{
                            Toast.makeText(Battle.this,"NO TIENES POCIONES!!",Toast.LENGTH_SHORT).show();}
                        break;
                    default:
                        Toast.makeText(Battle.this,"no escogiste un item",Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        builder.show();




    }

    public void onClickHuir(View view) {
        Toast.makeText(this,"Huiste de la pelea",Toast.LENGTH_SHORT).show();
        update_battle();
        finish();
    }
    public void onClickCambiar(View view) {
        theAmazingSwitch();
    }

    public void theAmazingSwitch(){
        pokeList.clear();
        //teamList = new Select().from(Equipo.class).where(Equipo_Table.Equipo_id.isNot(my_id)).queryList();
        teamList = new Select().from(Equipo.class).queryList();
        CharSequence allies[]=new CharSequence[teamList.size()];
        for(int i=0;i<teamList.size();i++){
            pokeList.add(new Select().from(Poke.class).where(Poke_Table.id.eq(teamList.get(i).Pokedex_id)).querySingle());
            allies[i]="#"+i+": "+pokeList.get(i).Name;
            //teamList.get(i).Equipo_id
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Choose a new pokemon");
        builder.setItems(allies, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pokeList.get(which);
                if(teamList.get(which).Current_HP<=0){
                    Toast.makeText(Battle.this,"Este pokemon esta muy debil",Toast.LENGTH_SHORT).show();
                }else{
                    if(teamList.get(which).Equipo_id==my_id){
                        Toast.makeText(Battle.this,"Este pokemon ya esta peleando",Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(Battle.this,"You picked "+pokeList.get(my_select).Name,Toast.LENGTH_SHORT).show();
                        my_select=which;
                        new theGreatSwitch().execute();

                    /*UPDATE Equipo_Table SET lead= CASE WHEN equipo_id=which THEN true ELSE false END*/

                    }
                }
            }
        });
        builder.show();
    }

    public void levelUP(){
        teamList = new Select().from(Equipo.class).where(Equipo_Table.Equipo_id.is(my_id)).queryList();
        pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(teamList.get(0).Pokedex_id)).queryList();
        CharSequence options[]=new CharSequence[3];
            options[0]="HP";
            options[1]="ATK";
            options[2]="DEF";


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Subiste de nivel, escoge que mejorar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result;
                switch(which){
                    case 0:
                        if(teamList.get(0).HP<pokeList.get(0).HP_max){
                            result="subio tu resistencia";
                            SQLite.update(Equipo.class).set(Equipo_Table.HP.eq(teamList.get(0).HP+1)).where(Equipo_Table.Equipo_id.is(my_id)).execute();
                        }else{result="tu resistencia ya esta al maximo";}
                        break;
                    case 1:
                        if(teamList.get(0).ATK<pokeList.get(0).ATK_max){
                            result="subio tu ataque";
                            SQLite.update(Equipo.class).set(Equipo_Table.ATK.eq(teamList.get(0).ATK+1)).where(Equipo_Table.Equipo_id.is(my_id)).execute();
                        }else{result="tu ataque ya esta al maximo";}
                        break;
                    case 2:

                        if(teamList.get(0).DEF<pokeList.get(0).DEF_max){
                            result="subio tu defensa";
                            SQLite.update(Equipo.class).set(Equipo_Table.DEF.eq(teamList.get(0).DEF+1)).where(Equipo_Table.Equipo_id.is(my_id)).execute();
                        }else {result="tu defensa ya esta al maximo";}
                        break;
                    default:
                        result="no subio nada";
                        break;
                }
                Toast.makeText(Battle.this,""+result,Toast.LENGTH_SHORT).show();
                teamList = new Select().from(Equipo.class).where(Equipo_Table.Equipo_id.is(my_id)).queryList();
                if(teamList.get(0).HP>=pokeList.get(0).HP_max && teamList.get(0).ATK>=pokeList.get(0).ATK_max && teamList.get(0).DEF>=pokeList.get(0).DEF_max && pokeList.get(0).Evolution>0){
                    SQLite.update(Equipo.class).set(Equipo_Table.Pokedex_id.eq(pokeList.get(0).Evolution)).where(Equipo_Table.Equipo_id.is(my_id)).execute();
                    new AlertDialog.Builder(Battle.this)
                            .setTitle("¡¡¡OH!!!")
                            .setMessage("TU POKEMON ESTA EVOLUCIONANDO")
                            .setPositiveButton("YES!YES!YES!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                 }
                            })
                            //.setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();

                }else{finish();}

            }
        });
        builder.show();
    }

    public void capturado(){
        trainer = new Select().from(Entrenador.class).queryList();
        teamList = new Select().from(Equipo.class).queryList();

        Equipo atrapado = new Equipo(teamList.size(), wild_id, wild_hp, wild_atk, wild_def, wild_speed, false);
        atrapado.save();
        Toast.makeText(this,"Lo Atrapaste!!",Toast.LENGTH_SHORT).show();
        update_battle();
        finish();
    }
    public int calcularDaño(int prey_c_hp,int predator_atk,int prey_def, String prey_weak, String prey_str, String predator_type){
        double dmg_cal, dmg_mod, rnd_mod;
        rnd_mod=0+(Math.random() * 5);
        if(predator_type.equals(prey_weak)) {
            myList.add("¡¡¡Es muy eficaz!!!");myarrayAdapter.notifyDataSetChanged();
            dmg_mod=1.3;
        }else{
            if(predator_type.equals(prey_str)) {
                myList.add("Pero no es muy eficaz...");myarrayAdapter.notifyDataSetChanged();
                dmg_mod=0.7;
            }else{
                dmg_mod=1;
            }
        }
        dmg_cal=rnd_mod+dmg_mod*(predator_atk-prey_def);
        if(dmg_cal<1){dmg_cal=1;}
        int waitforit= (int) dmg_cal;
        myList.add(waitforit+" de daño");myarrayAdapter.notifyDataSetChanged();
        waitforit=prey_c_hp - waitforit;
        return waitforit;
    }

    public int check_fainted_pokemon(int A_c_hp,int B_c_hp){
        int temp=0;
        if(A_c_hp>=1 && B_c_hp>=1){temp=0;}
        if(A_c_hp>=1 && B_c_hp<1) {temp=2;}
        if(A_c_hp<1 && B_c_hp>=1) {temp=1;}
        if(A_c_hp<1 && B_c_hp<1)  {temp=3;}
        return temp;
    }

    public boolean isAnyoneOkay(){
        boolean imokay=false;
        teamList = new Select().from(Equipo.class).queryList();
        for(int i=0;i<teamList.size();i++){
            if(teamList.get(i).Current_HP>0){imokay=true;}
        }
        return imokay;
    }

    public void update_battle(){
        if(my_c_hp>my_hp){my_c_hp=my_hp;}
        if(wild_c_hp>wild_hp){wild_c_hp=wild_hp;}
        if(my_c_hp<0){my_c_hp=0;}
        if(wild_c_hp<0){wild_c_hp=0;}
        SQLite.update(Equipo.class).set(Equipo_Table.Current_HP.eq(my_c_hp)).where(Equipo_Table.Equipo_id.is(my_id)).async().execute();
        tv1.setText("Enemy Hp: "+wild_c_hp+" /"+wild_hp);
        tv2.setText("My Hp: "+my_c_hp+" /"+my_hp);
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



    private class theGreatSwitch extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(Battle.this);
            pDialog.setMessage("You picked "+pokeList.get(my_select).Name);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SQLite.update(Equipo.class)
                    .set(Equipo_Table.Lead.eq(true))
                    .where(Equipo_Table.Equipo_id.is(my_select))
                    .async()
                    .execute();

            SQLite.update(Equipo.class)
                    .set(Equipo_Table.Current_HP.eq(my_c_hp))
                    .where(Equipo_Table.Equipo_id.is(my_id))
                    .async()
                    .execute();

            SQLite.update(Equipo.class)
                    .set(Equipo_Table.Lead.eq(false))
                    .where(Equipo_Table.Equipo_id.is(my_id))
                    .async()
                    .execute();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing())
                pDialog.dismiss();
            loadMyPoke();
        }

    }


}
