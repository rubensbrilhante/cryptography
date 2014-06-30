package com.teste.brilhante.teste;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


public class Main extends Activity implements View.OnClickListener {

    public static final String PREFERENCE = "preference";
    public static final String UTF_8 = "UTF-8";
    public static final String KEY = "key";
    ViewSwitcher switcher;
    Button btnEncript;
    Button btnDencript;
    TextView decriptedTxt;
    EditText txtToEncript;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setActions();
    }

    private void setActions() {
        btnEncript.setOnClickListener(this);
        btnDencript.setOnClickListener(this);
    }

    private void findViews() {
        switcher = (ViewSwitcher) findViewById(R.id.switcher);
        btnDencript = (Button) findViewById(R.id.btn_decript);
        btnEncript = (Button) findViewById(R.id.btn_encript);
        decriptedTxt = (TextView) findViewById(R.id.txt_decript);
        txtToEncript = (EditText) findViewById(R.id.edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_decript:
                clickDecript();
                break;
            case R.id.btn_encript:
                Editable txtToEncriptText = txtToEncript.getText();
                if (txtToEncriptText.length() > 0) {
                    clickEncript(txtToEncriptText.toString());
                } else {
                    txtToEncript.setError("Nada para encriptar");
                }
                break;
        }
    }

    private void clickDecript() {
        if (!isBack) {
            String algorithm = getAlgorithm();
            SharedPreferences preferences = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
            String encripted = preferences.getString(algorithm, "");
            Log.d(AES.class.getName(), "recovString:" + encripted);
            try {
                decriptedTxt.setText(new String(doDecript(encripted), UTF_8));
                btnDencript.setText(R.string.voltar);
                isBack = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            switcher.setDisplayedChild(0);
        }
    }

    private byte[] doDecript(String encripted) throws Exception {
        byte[] decode = Base64.decode(encripted, Base64.NO_PADDING);
        Log.d(AES.class.getName(), "bytes Base64 Decoded:" + decode);
        Log.d(AES.class.getName(), "bytes string Decoded:" + encripted.getBytes(UTF_8));
        return AES.decrypt(decode, getKey());
    }

    private byte[] getKey() throws UnsupportedEncodingException {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        String string = preferences.getString(KEY, "");
        return Base64.decode(string, Base64.NO_PADDING);
    }

    private void clickEncript(String nonCriptedTxt) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        if (!preferences.contains(KEY)) {
            try {
                byte[] data = AES.makeKey();
                String key = Base64.encodeToString(data, Base64.NO_PADDING);
                Log.d(AES.class.getName(), "saved:" + key);
                preferences.edit().putString(KEY, key).commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }
        String algorithm = getAlgorithm();
        try {
            byte[] data = doEncript(nonCriptedTxt);
            Log.d(AES.class.getName(), "bytesEncoded:" + data);
            String encripted = Base64.encodeToString(data, Base64.NO_PADDING);
            Log.d(AES.class.getName(), "savedString:" + encripted);
            preferences.edit().putString(algorithm, encripted).commit();
            switcher.setDisplayedChild(1);
            isBack = false;
            btnDencript.setText(R.string.decriptografar);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private byte[] doEncript(String nonCriptedTxt) throws Exception {
        return AES.encrypt(nonCriptedTxt.getBytes(UTF_8), getKey());
    }

    private String getAlgorithm() {
        return "AES";
    }
}
