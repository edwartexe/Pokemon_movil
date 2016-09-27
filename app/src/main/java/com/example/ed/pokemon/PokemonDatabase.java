package com.example.ed.pokemon;

/**
 * Created by ED on 20/9/2016.
 */
import com.raizlabs.android.dbflow.annotation.Database;
@Database(name= PokemonDatabase.NAME, version = PokemonDatabase.VERSION)
public class PokemonDatabase {
    public static final String NAME="PokemonDatabase";
    public static final int VERSION=3;
}
