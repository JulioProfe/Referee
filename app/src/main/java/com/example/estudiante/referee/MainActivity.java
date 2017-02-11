package com.example.estudiante.referee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private Button start;
    private Button stop;
    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Comunicacion.getInstance().addObserver(this);

       start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        texto = (TextView) findViewById(R.id.texto);

        texto.setText("Posiciones\n");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunicacion.getInstance().enviar("start", Comunicacion.miIp, Comunicacion.puerto);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunicacion.getInstance().enviar("stop", Comunicacion.miIp, Comunicacion.puerto);
            }
        });
    }

    protected void onResume(){
        super.onResume();

            Comunicacion.getInstance().enviar("start", Comunicacion.miIp, Comunicacion.puerto);

    }

    protected void onPause(){
        super.onPause();
        Comunicacion.getInstance().enviar("stop", Comunicacion.miIp, Comunicacion.puerto);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof String){
            String mensaje = (String) o;
            updateInterface(mensaje);
        }
    }

    public void updateInterface(final  String cambio){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView llego = (TextView)findViewById(R.id.texto);
                llego.setText(llego.getText() + cambio + "\n");
            }
        });
    }
}

