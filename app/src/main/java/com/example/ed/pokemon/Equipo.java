package com.example.ed.pokemon;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ED on 20/9/2016.
 */
@Table(database = PokemonDatabase.class)
public class Equipo extends BaseModel {
    @PrimaryKey//(autoincrement = true)
    long Equipo_id;

    @Column
    long Pokedex_id;

    @Column
    public int HP;

    @Column
    public int Current_HP;

    @Column
    public int ATK;

    @Column
    public int DEF;

    @Column
    public int SPEED;

    @Column
    public boolean Lead;

    public Equipo(long id, long pokedex, int hp, int ataque, int defensa, int speed,boolean leader){
        this.Equipo_id = id;
        this.Pokedex_id = pokedex;
        this.HP = hp;
        this.Current_HP=hp;
        this.ATK= ataque;
        this.DEF= defensa;
        this.SPEED = speed;
        this.Lead=leader;
    }

    public Equipo() {

    }

    @Override
    public String toString() {
        return "Equipo{" +
                "Equipo_id=" + Equipo_id +
                ", Pokedex_id=" + Pokedex_id +
                ", HP=" + HP +
                ", Current HP"+Current_HP+
                ", ATK=" + ATK +
                ", DEF=" + DEF +
                ", SPEED=" + SPEED +
                ", LEAD="+Lead+
                '}';
    }
}
