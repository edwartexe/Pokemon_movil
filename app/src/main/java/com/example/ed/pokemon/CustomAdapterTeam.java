package com.example.ed.pokemon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by ED on 25/9/2016.
 */
public class CustomAdapterTeam extends BaseAdapter {

    private List<Equipo> e;
    private Context context;
    private TextView tv, tv2,tv3;
    private ImageView iv;


    public  CustomAdapterTeam(Context context, List<Equipo> inData) {
        super();
        this.context=context;
        this.e=inData;
    }

    @Override
    public int getCount() {
        return e.size();
    }

    @Override
    public Object getItem(int position) {
        return e.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int positon, View view, ViewGroup viewGroup) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_row_team,null);
        }

        iv=(ImageView) view.findViewById(R.id.imageView5);
        tv=(TextView) view.findViewById(R.id.textView7);
        tv2=(TextView) view.findViewById(R.id.textView8);
        tv3=(TextView) view.findViewById(R.id.textView9);


        List <Poke> pokeList = new Select().from(Poke.class).where(Poke_Table.id.eq(e.get(positon).Pokedex_id)).queryList();
        if(iv!=null){
            new loadSpriteFromNetwork(iv).execute(pokeList.get(0).ImgFront);
        }
        if(tv!=null){
            tv.setText(""+pokeList.get(0).Name);
        }
        if(tv2!=null){
            tv2.setText(e.get(positon).Current_HP+"/"+e.get(positon).HP);
        }
        if(tv3!=null){
            if(e.get(positon).Lead){
                tv3.setText("LIDER");
            }else{
                tv3.setText("standby");
            }

        }

        if(e.get(positon).Lead){
            view.setBackgroundColor(0xFFFF4444);
        }

        return view;
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
