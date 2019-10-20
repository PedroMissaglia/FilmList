package com.example.filmlist;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Films> filmList;
    private ListView filmListView;
    private FilmAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        filmListView = findViewById(R.id.ListView);
        filmList = new LinkedList<>();
        adapter = new FilmAdapter(this, filmList);
        filmListView.setAdapter(adapter);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            String end = getString(R.string.web_service_url, "13dbb657733404615251afec1f20cd00");
            new Thread(
                    () -> {
                        try {
                            URL url = new URL(end);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            InputStream is = conn.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader reader = new BufferedReader(isr);
                            String linha = null;
                            StringBuilder resultado = new StringBuilder("");
                            while((linha = reader.readLine()) != null){
                                resultado.append(linha);
                            }
                            reader.close();
                            conn.disconnect();
                            Serilialize(resultado.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            runOnUiThread(
                                    () -> {
                                        Toast.makeText(
                                                this,
                                                getString(R.string.invalid_url),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(
                                    ()-> {
                                        Toast.makeText(
                                                this,
                                                getString(R.string.connect_error),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                            );
                        }
                    }
            ).start();
        });
    }

    private void Serilialize(String jsonTextual){
        try {
            JSONObject json = new JSONObject(jsonTextual);
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++){
                JSONObject iesimo = results.getJSONObject(i);
//                JSONObject item = iesimo.getJSONObject("results");
                String filmName = iesimo.getString("title");
                String description = iesimo.getString("overview");
                String icon = iesimo.getString("poster_path");

                Films f = new Films(filmName, description, icon);
                filmList.add(f);
            }
            runOnUiThread(() ->{adapter.notifyDataSetChanged();});
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

}
