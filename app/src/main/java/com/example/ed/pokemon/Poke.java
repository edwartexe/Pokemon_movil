package com.example.ed.pokemon;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ED on 12/9/2016.
 */

@Table(database = PokemonDatabase.class)
public class Poke extends BaseModel {
    //private String ImgFront;

    @PrimaryKey//(autoincrement = true)
    long id;

    @Column
    public String Name;

    @Column
    public String Type;

    @Column
    public String Fortaleza;

    @Column
    public String Debilidad;

    @Column
    public String ImgFront;

    @Column
    public String ImgBack;

    @Column
    public int HP_max;

    @Column
    public int ATK_max;

    @Column
    public int DEF_max;

    @Column
    public int Evolution;

    public Poke(long id, String name,String type, String strength, String weakness, int hp_max, int ataque_max, int defensa_max, String imgFront, String imgBack,int ev_id) {
        this.Evolution = ev_id;
        this.Type = type;
        this.Fortaleza = strength;
        this.Debilidad = weakness;
        ImgFront = imgFront;
        ImgBack = imgBack;
        this.id = id;
        this.HP_max = hp_max;
        this.ATK_max = ataque_max;
        this.DEF_max = defensa_max;
        this.Name = name;
    }

    public Poke(int id, String fs){
        this.id=id;
        this.ImgFront=fs;
    }

    public Poke() {

    }

    @Override
    public String toString(){
        return ImgFront;
    }
}
