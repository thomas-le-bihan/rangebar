package com.example.tlebihan.rangebar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.crittercism.app.Crittercism;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Crittercism.initialize(getApplicationContext(), "00f108f97d0543a79e7489da664358f400555300");

        final RangeBar rangeBar = (RangeBar) findViewById(R.id.rangebar);
        final EditText edtMin = (EditText) findViewById(R.id.edt_min);
        final EditText edtMax = (EditText) findViewById(R.id.edt_max);
        rangeBar.setOnValueChangedListener(new RangeBar.OnValueChangeListener() {
            @Override
            public void onValueChanged(RangeBar view, int min, int max) {
                System.out.println("[" + min + ", " + max + "]");
                edtMin.setText(min+"");
                edtMax.setText(max+"");
            }
        });
        edtMin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    rangeBar.setThumbMinByValue(Integer.valueOf(edtMin.getText().toString().trim()));
                    return true;
                }
                return false;
            }
        });
        edtMax.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    rangeBar.setThumbMaxByValue(Integer.valueOf(edtMax.getText().toString().trim()));
                    return true;
                }
                return false;
            }
        });
        rangeBar.setBounds(-50,50);
    }
}
