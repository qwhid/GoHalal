package id.ac.unj.gohalal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.widget.TextView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.ac.unj.gohalal.Adapter.MenuAdapter;
import id.ac.unj.gohalal.Helper.JSONParser;


public class RestaurantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MenuAdapter adapter;
    JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;

    String MENU_URL= "http://gohalal.pe.hu/testv2/index.php/Restomenu";


    TextView restoName, restoAlamat, restoPhone, restoEmail;
    String TAG_RESTO = "restaurant";
    String TAG_MENU = "menu";
    String TAG_ID = "id";
    String TAG_IDRESTO = "idresto";
    String TAG_NAMA = "nama";
    String TAG_LOC = "langlat";
    String TAG_LAT = "latitude";
    String TAG_LONG = "longitude";
    String TAG_DESKRIPSI = "deskripsi";
    String TAG_ALAMAT = "alamat";
    String TAG_TELP = "telp";
    String TAG_EMAIL = "email";
    String TAG_IMAGE = "image";
    String TAG_RATE = "rate";
    String TAG_PRICE = "price";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restoName = (TextView)findViewById(R.id.restoName);
        restoAlamat = (TextView)findViewById(R.id.isiAlamat);
        restoEmail = (TextView)findViewById(R.id.isiEmail);
        restoPhone = (TextView)findViewById(R.id.isiTelp);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        viewInformation();

    }

    public void viewInformation(){
        Intent intent = getIntent();
        String id = intent.getExtras().getString(TAG_ID);
        String nama = intent.getExtras().getString(TAG_NAMA);
        String alamat = intent.getExtras().getString(TAG_ALAMAT);
        String deskripsi = intent.getExtras().getString(TAG_DESKRIPSI);
        String image = intent.getExtras().getString(TAG_IMAGE);
        String telp = intent.getExtras().getString(TAG_TELP);
        String email = intent.getExtras().getString(TAG_EMAIL);
        int rate = intent.getExtras().getInt(TAG_RATE);

        if(intent.getExtras() != null){
            restoName.setText(nama);
            restoEmail.setText(email);
            restoAlamat.setText(alamat);
            restoPhone.setText(telp);
        }

        LoadData loadData= new LoadData();
        loadData.execute(id);
    }

    class LoadData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RestaurantActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String idresto= args[0];

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idresto", idresto));

            JSONObject json = jParser.makeHttpRequest(MENU_URL, "POST", params);
            return json;
        }

        protected void onPostExecute(JSONObject result) {
            try{

                if(result != null){
                    JSONArray array = result.getJSONArray(TAG_MENU);

                    String [] nama = new String[array.length()];
                    String [] deskripsi = new String[array.length()];
                    int [] price = new int[array.length()];
                    int [] rate = new int[array.length()];
                    int [] idresto = new int[array.length()];

                    if(array != null || !array.equals("")){
                        for(int i = 0; i < array.length(); i++) {
                            nama[i] = array.getJSONObject(i).getString(TAG_NAMA);
                            deskripsi[i] = array.getJSONObject(i).getString(TAG_DESKRIPSI);
                            price[i] = array.getJSONObject(i).getInt(TAG_PRICE);
                            rate[i] = array.getJSONObject(i).getInt(TAG_RATE);
                            idresto[i]  = array.getJSONObject(i).getInt(TAG_IDRESTO);

                            adapter = new MenuAdapter(nama,deskripsi,idresto,rate,price);
                            pDialog.dismiss();
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Restaurant doesn't have menu yet...",Toast.LENGTH_LONG)
                            .show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }

    }

}