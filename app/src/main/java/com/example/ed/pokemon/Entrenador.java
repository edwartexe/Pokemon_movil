package com.example.ed.pokemon;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ED on 20/9/2016.
 */

@Table(database = PokemonDatabase.class)
public class Entrenador extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public int pokeball;

    @Column
    public int potion;

    @Column
    public String Nombre;

    @Column
    public int capturados;


    public Entrenador(long id, int pokeball, int potion, String nombre, int capturados) {
        this.id = id;
        this.pokeball = pokeball;
        this.potion = potion;
        Nombre = nombre;
        this.capturados = capturados;
    }

    public Entrenador() {

    }

    @Override
    public String toString() {
        return "Entrenador{" +
                "id=" + id +
                ", pokeball=" + pokeball +
                ", potion=" + potion +
                ", Nombre='" + Nombre + '\'' +
                ", capturados=" + capturados +
                '}';
    }
}
