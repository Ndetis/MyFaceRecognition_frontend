package com.example.myfacerecognition;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.print.PrintHelper;

import com.example.myfacerecognition.Utils.DataHelper;
import com.example.myfacerecognition.Utils.DataUserHelper;
import com.example.myfacerecognition.Utils.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogFragment.OnInputListener {

    private static final String TAG = "MainActivity";
    public String mInput;
    public TextView code_ue, filiere, intitulé, semestre, grade, annee;
    public EditText editText, editText2, editText3, editText4;
    DrawerLayout drawerLayout;
    String[] st;
    String str;
    TableLayout stk;
    FloatingActionButton fab_actualize, fab;
    Bitmap bitmap;
    ScrollView llPdf;
    private DataHelper db;
    private DataUserHelper dbs;
    private ProgressDialog pd;

    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: got the input: " + input);
        mInput = input;
        st = mInput.split(";");

        db.addNewline(st[0].toUpperCase(Locale.ROOT), st[1].toUpperCase(Locale.ROOT), st[2].toUpperCase(Locale.ROOT), st[3].toUpperCase(Locale.ROOT), st[4].toUpperCase(Locale.ROOT), st[5].toUpperCase(Locale.ROOT));
        setInputToTextView();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*------------Barre de titre,Drawer menu,menu,etc-----------------*/
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        /*-------------Définir les différents éléments des fichiers xml--------*/

        code_ue = findViewById(R.id.code_ue_m);
        filiere = findViewById(R.id.filiere_m);
        intitulé = findViewById(R.id.intitulé_m);
        semestre = findViewById(R.id.semestre_m);
        grade = findViewById(R.id.grade_m);
        annee = findViewById(R.id.annee_m);
        stk = (TableLayout) findViewById(R.id.Presence_Table);
        fab_actualize = findViewById(R.id.fab_actualize);
        pd = new ProgressDialog(MainActivity.this);
        fab = findViewById(R.id.fab);
        llPdf = findViewById(R.id.page);

        /*--------------Definir les differentes actions à accomplir suite à un clic-----------*/

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        //faire basculer le menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        db = new DataHelper(this);
        dbs = new DataUserHelper(this);
        setInputToTextView();
        fab_actualize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stk.getChildCount() != 0) {
                    stk.removeViews(0, Math.max(0, stk.getChildCount()));
                    dbs.ondelete();
                    getUsers();
                } else {
                    dbs.ondelete();
                    getUsers();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'aperçu d'impression
                PrintHelper printHelper = new PrintHelper(MainActivity.this);
                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("Liste_de_presence_MYCampusFace", convertViewToBitmap(llPdf));
            }
        });
    }

    /*----------------Differentes fonctions utilisées------------*/

    public boolean getUsers() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.200.104:500/api/users_recognize").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String st = response.body().string();
                String file = st.replace('[', ' ');
                String obj = file.replace(']', ' ');
                String sp = obj.replace('"', ' ');
                String[] str = sp.split(",");
                Log.e("data:", String.valueOf(st.length()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (st.length() != 3) {
                            for (int i = 0; i < str.length; i++) {
                                String[] tab = str[i].split("/");
                                dbs.addNewline(tab[0].toUpperCase(Locale.ROOT).trim(), tab[1].toUpperCase(Locale.ROOT), tab[2].toUpperCase(Locale.ROOT), tab[3].toUpperCase(Locale.ROOT), tab[4].toUpperCase(Locale.ROOT), tab[5].toUpperCase(Locale.ROOT), tab[6].toUpperCase(Locale.ROOT), tab[7].toUpperCase(Locale.ROOT), tab[8].toUpperCase(Locale.ROOT), tab[9].toUpperCase(Locale.ROOT).trim());
                            }
                            init();
                        } else {
                            Toast.makeText(MainActivity.this, "Table of recognized persons is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return true;
    }

    public void init() {
        int i = 0;
        String fil = filiere.getText().toString();
        String[] f = fil.split(":");
        String niv = grade.getText().toString();
        String[] n = niv.split(":");
        String date = LocalDateTime.now().toString();
        String[] d = date.split("T");

        String selection = "niveau=? AND annee=?";
        String[] selectionArgs = {n[1], d[0]};


        /*----------------------Titre du tableau--------------------*/
        TableLayout stk = (TableLayout) findViewById(R.id.Presence_Table);
        stk.setDividerPadding(2);
        TableRow tbrow0 = new TableRow(this);
        //tbrow0.setBackgroundColor(Color.parseColor("#8692f7"));
        tbrow0.setMinimumHeight(50);
        tbrow0.setMinimumWidth(100);
        tbrow0.setBackgroundResource(R.drawable.border2);

        tbrow0.setPadding(5, 5, 5, 5);
        /*---------------------------------------------*/
        TextView tv0 = new TextView(this);
        tv0.setText(" N° ");
        tv0.setTextSize(15);
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        /*---------------------------------------------*/
        TextView tv1 = new TextView(this);
        tv1.setText(" Noms et Prenoms ");
        tv1.setTextSize(15);
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);
        /*---------------------------------------------*/
        TextView tv2 = new TextView(this);
        tv2.setText(" Matricules ");
        tv2.setTextSize(15);
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);
        /*---------------------------------------------*/
        TextView tv3 = new TextView(this);
        tv3.setText(" Signature ");
        tv3.setTextSize(15);
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        /*----------Auto Mapping content-----------------------------*/

        SQLiteDatabase base = dbs.getReadableDatabase();
        Cursor cursor = base.query("student", new String[]{DataUserHelper.ID, DataUserHelper.NOM, DataUserHelper.PRENOM, DataUserHelper.MATRICULE, DataUserHelper.FILIERE, DataUserHelper.NIVEAU, DataUserHelper.TRANSACTION, DataUserHelper.TRANCHE, DataUserHelper.SOMME, DataUserHelper.ANNEE}, selection, selectionArgs, null, null, null);
        //Cursor cursor = base.rawQuery("SELECT * FROM student", null);

        if (cursor.moveToFirst()) {
            do {
                String st = cursor.getString(0) + "->" + cursor.getString(1) + "->" + cursor.getString(2) + "->" + cursor.getString(3) + "->"
                        + cursor.getString(4) + "->" + cursor.getString(5) + "->" + cursor.getString(6) + "->" + cursor.getString(7) + "->" + cursor.getString(8) + "->" + cursor.getString(9) + ".";
                String[] tab = st.split("->");

                Log.e("day:", tab[4] + "-" + f[1]);

                if (Objects.deepEquals(f[1].trim(), tab[4].trim())) {
                    TableRow tbrow = new TableRow(this);
                    tbrow.setPadding(5, 5, 5, 5);
                    if (i % 2 == 0) {
                        tbrow.setBackgroundResource(R.drawable.border);
                    } else {
                        tbrow.setBackgroundResource(R.drawable.border3);
                    }

                    /*--------------------------------------*/

                    TextView t1v = new TextView(this);
                    t1v.setText(String.valueOf(i + 1));
                    t1v.setTextColor(Color.BLACK);
                    t1v.setGravity(Gravity.CENTER);
                    t1v.setMaxWidth(80);
                    tbrow.addView(t1v);
                    /*-------------------------------------*/
                    TextView t2v = new TextView(this);
                    t2v.setText(tab[1] + " " + tab[2]);
                    t2v.setTextColor(Color.BLACK);
                    t2v.setGravity(Gravity.CENTER);
                    t2v.setMaxWidth(230);
                    tbrow.addView(t2v);
                    /*-------------------------------------*/
                    TextView t3v = new TextView(this);
                    t3v.setText(tab[3]);
                    t3v.setTextColor(Color.BLACK);
                    t3v.setGravity(Gravity.CENTER);
                    t3v.setMaxWidth(80);
                    tbrow.addView(t3v);
                    /*-------------------------------------*/
                    TextView t4v = new TextView(this);
                    t4v.setText("");
                    t4v.setTextColor(Color.BLACK);
                    t4v.setGravity(Gravity.CENTER);
                    tbrow.addView(t4v);
                    stk.addView(tbrow);

                    i++;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        base.close();
    }

    private void setInputToTextView() {
        SQLiteDatabase base = db.getReadableDatabase();
        Cursor cursor = base.query("header", new String[]{DataHelper.CODE_UE, DataHelper.FILIERE, DataHelper.INTITULE, DataHelper.SEMESTRE, DataHelper.GRADE, DataHelper.ANNEE},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String st = cursor.getString(0) + "->" + cursor.getString(1) + "->" + cursor.getString(2) + "->" + cursor.getString(3) + "->"
                        + cursor.getString(4) + "->" + cursor.getString(5) + "->" + ".";
                String[] tab = st.split("->");

                code_ue.setText("Code UE:" + tab[0]);
                filiere.setText("Filière:" + tab[1]);
                intitulé.setText("Intitulé:" + tab[2]);
                semestre.setText("Semestre:" + tab[3]);
                annee.setText("Annee:" + tab[4]);
                grade.setText("Grade:" + tab[5]);

            } while (cursor.moveToNext());
        }
        base.close();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.theme) {
            //Toast.makeText(this, "Nothing ....", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.ChangeHearder) {
            DialogFragment dialog = new DialogFragment();
            dialog.show(getFragmentManager(), "MyCustomDialog");
        }

        if (id == R.id.update_person) {
            /**/
            LayoutInflater inflater = LayoutInflater.from(this);//getActivity(this).getLayoutInflater();
            final View view = inflater.inflate(R.layout.updatecustums, null);

            editText = view.findViewById(R.id.editText);
            editText2 = view.findViewById(R.id.editText2);
            editText3 = view.findViewById(R.id.editText3);
            editText4 = view.findViewById(R.id.editText4);

            /*recuperation de l'id de l'étudiant dont les informations doivent etre mis à jour*/

            SQLiteDatabase base = dbs.getReadableDatabase();
            Cursor cursor = base.query("student", new String[]{DataUserHelper.ID}, DataUserHelper.MATRICULE = editText.getText().toString().toUpperCase(Locale.ROOT), null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    str = cursor.getString(0);
                    Log.e("data", str);
                } while (cursor.moveToNext());
            }
            base.close();

            /**/
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Update...");
            dialog.setMessage("Enter data");
            dialog.setView(view);
            dialog.setPositiveButton("OK", (dialogInterface, i) -> {
                String editTextInput = str + "," + editText.getText().toString().toUpperCase(Locale.ROOT).trim() + "," + editText2.getText().toString().toUpperCase(Locale.ROOT).trim() + "," + editText3.getText().toString().toUpperCase(Locale.ROOT).trim() + "," + editText4.getText().toString().trim();
                Log.e("onclick", "editext value is: " + editTextInput);
                _uploadForm(editTextInput);
            });
            dialog.setNegativeButton("Cancel", null);
            dialog.create();
            dialog.show();
        }

        if (id == R.id.Contact) {
            Toast.makeText(this, "see all contact of enterprise", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void _uploadForm(String text) {
        String dumyText = text;
        RequestBody formbody
                = new FormBody.Builder()
                .add("sample", dumyText)
                .build();

        pd.setTitle("Please Wait...");
        pd.setMessage("Preparing to upload...");
        pd.show();

        postRequest_form(" http://192.168.200.104:500/update", formbody);
    }

    void postRequest_form(String postUrl, RequestBody formbody) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(formbody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.hide();
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String st = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (st.equals("User informations update successfully")) {
                            pd.hide();
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "data updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });
    }

    public void OpenUserInfoView() {
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    public void OpenUserFaceRecoView() {
        startActivity(new Intent(getApplicationContext(), Face_Reco_Activity.class));
    }

    private void shareTextOnly(String titlee) {
        String sharebody = titlee;
        Intent intentt = new Intent(Intent.ACTION_SEND);
        intentt.setType("text/plain");
        intentt.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intentt.putExtra(Intent.EXTRA_TEXT, sharebody);
        startActivity(Intent.createChooser(intentt, "Share Via"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                //startActivity(new Intent(this,MainActivity.class));
                //replaceFragment(new HomeFragment());
                break;

            case R.id.sample:
                OpenUserInfoView();
                break;

            case R.id.scanface:
                OpenUserFaceRecoView();
                break;

            case R.id.nav_share:
                shareTextOnly("My Campus Face App");
                break;

            case R.id.nav_about:
                //replaceFragment(new AboutFragment());
                /**/
                LayoutInflater inflater = LayoutInflater.from(this);//getActivity(this).getLayoutInflater();
                final View view = inflater.inflate(R.layout.aboutus, null);
                /**/
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setView(view);
                dialog.create();
                dialog.show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private Bitmap convertViewToBitmap(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}