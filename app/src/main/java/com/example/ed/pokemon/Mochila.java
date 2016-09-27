package com.example.ed.pokemon;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class Mochila extends AppCompatActivity {
    private TextView tv1,tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mochila);

        tv1=(TextView) findViewById(R.id.textView17);
        tv2=(TextView) findViewById(R.id.textView18);
        List<Entrenador> trainer = new Select().from(Entrenador.class).queryList();
        tv1.setText(""+trainer.get(0).pokeball);
        tv2.setText(""+trainer.get(0).potion);
    }
}
