package com.example.ed.pokemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

public class CharacterSelect extends AppCompatActivity {
    EditText et;
    ImageButton ib1,ib2,ib3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);
        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

        et=(EditText)findViewById(R.id.editText);
        ib1= (ImageButton) findViewById(R.id.imageButton);
        ib2= (ImageButton) findViewById(R.id.imageButton2);
        ib3= (ImageButton) findViewById(R.id.imageButton3);
        //#385830

    }

    public void iChooseYou(View view) {
        String playername= String.valueOf(et.getText()).replace(" ","");
        Equipo atrapado;
        if(!playername.equals("")){
            Delete.table(Entrenador.class);
            Delete.table(Equipo.class);
            Entrenador mC = new Entrenador(0,10,10,playername,1);
            mC.save();
            if(view.equals(ib1)){
                Toast.makeText(this,"bulbasaur",Toast.LENGTH_SHORT).show();
                atrapado = new Equipo(0, 1, 50, 49, 60, 4,true);
                atrapado.save();
            }
            if(view.equals(ib2)){
                Toast.makeText(this,"charmander",Toast.LENGTH_SHORT).show();
                atrapado = new Equipo(0, 3, 40, 45, 40, 3,true);
                atrapado.save();
            }
            if(view.equals(ib3)){
                Toast.makeText(this,"squirtle",Toast.LENGTH_SHORT).show();
                atrapado = new Equipo(0, 5, 40, 40, 45, 5,true);
                atrapado.save();
            }
            Intent j = new Intent(this,MainActivity.class);
            startActivity(j);
            j.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            finish();
        }else{
            Toast.makeText(this,"Ingrese un nombre de jugador",Toast.LENGTH_LONG).show();
        }
    }

}
