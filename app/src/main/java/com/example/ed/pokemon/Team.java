package com.example.ed.pokemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class Team extends AppCompatActivity {
    private ListView lv;
    private List<Equipo> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());
        lv = (ListView) findViewById(R.id.listView);



        datos = new Select().from(Equipo.class).queryList();
        //ArrayAdapter<Equipo> adapter = new ArrayAdapter<Equipo>(this, android.R.layout.simple_list_item_1, android.R.id.text1, datos);
        CustomAdapterTeam adapter = new CustomAdapterTeam(this, datos);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Equipo> datos = null;
                datos = new Select().from(Equipo.class).queryList();
                Equipo datoAPasar = datos.get(position);

                Intent j = new Intent(Team.this, com.example.ed.pokemon.ShowTeamPoke.class);
                j.putExtra("eqid",datoAPasar.Equipo_id);
                j.putExtra("pkid",datoAPasar.Pokedex_id);
                startActivityForResult(j,1);
                //finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == 1) {}
            // Make sure the request was successful
            //if (resultCode == RESULT_OK) {}
        datos = new Select().from(Equipo.class).queryList();
        CustomAdapterTeam adapter = new CustomAdapterTeam(this, datos);
        lv.setAdapter(adapter);
    }
}
