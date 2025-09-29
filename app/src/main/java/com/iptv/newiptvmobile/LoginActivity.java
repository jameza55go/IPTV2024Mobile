package com.iptv.newiptvmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.AlertDialogMain;
import com.iptv.newiptvmobile.adapter.CustomArrayAdapter;
import com.iptv.newiptvmobile.ui.login.DemoFragment;
import com.iptv.newiptvmobile.ui.login.LoginAndRegisterFragment;
import com.iptv.newiptvmobile.ui.login.LoginFragment;
import com.iptv.newiptvmobile.ui.login.MoreActivity;
import com.iptv.newiptvmobile.ui.login.MorePageListenerToActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MorePageListenerToActivity {

    LinearLayout bt_demo, bt_more;
    TextView text_demo, text_more;
    private Spinner spinner;
    List<String> paths = new ArrayList<>();

    Context context;
    private DatabaseReference myRef;
    String appNewVersion = "1.0";
    String appUrlUpdate = "";
    String appVersion = "1.0";
    private ProgressDialog progressDialog;
    boolean allow = false;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    int count_login = 0;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_login);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        editor = getSharedPreferences("iptv", MODE_PRIVATE).edit();
        db.getString("lang", Locale.getDefault().getLanguage());
        progressDialog = new ProgressDialog(this);
        context = LoginActivity.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allow = this.getPackageManager().canRequestPackageInstalls();
        } else {
            try {
                allow = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (!allow) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES));
                                } else {
                                    startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                                }
                            }
                            FirebaseGet();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
        bt_demo = findViewById(R.id.demo);
        bt_more = findViewById(R.id.more);
        text_demo = findViewById(R.id.text_demo);
        text_more = findViewById(R.id.text_more);
        spinner = (Spinner) findViewById(R.id.spinner);
        logo = findViewById(R.id.imageView9);

        paths.add("TH");
        paths.add("EN");
        int positionLang;
        if (db.getString("lang", Locale.getDefault().getLanguage()).equals("th")) {
            positionLang = 0;
        } else {
            positionLang = 1;
        }

        CustomArrayAdapter adapter = new CustomArrayAdapter(LoginActivity.this,
                R.layout.item_spiner, paths);
        adapter.setDropDownViewResource(R.layout.item_spiner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setSelection(positionLang, false);
        spinner.setOnItemSelectedListener(this);

        loadFragment(new LoginAndRegisterFragment(), "login");


        bt_demo.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DemoActivity.class);
            startActivity(intent);
        });

        bt_demo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                text_demo.setTextColor(getResources().getColor(R.color.black));
            } else {
                text_demo.setTextColor(getResources().getColor(R.color.white));
            }
        });

        bt_more.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
            startActivityForResult(intent, 2);
        });

        bt_more.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                text_more.setTextColor(getResources().getColor(R.color.black));
            } else {
                text_more.setTextColor(getResources().getColor(R.color.white));
            }
        });


    }

    public void FirebaseGet() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map map = (Map) snapshot.getValue();
                editor.putString("apiurl", map.get("apiurl").toString());
                editor.putString("gameurl", map.get("gameurl").toString());
                editor.putString("wss", map.get("wss").toString());
                editor.putString("lineid", map.get("lineid").toString());
                editor.putString("linename", map.get("linename").toString());
                editor.putString("brandlogo", map.get("brandlogo").toString());
                editor.apply();
                appNewVersion = map.get("appversion").toString();
                appUrlUpdate = map.get("appurl").toString();
                appVersion = BuildConfig.VERSION_NAME;
                Picasso.get().load(map.get("brandlogo").toString()).into(logo);
                if (!appVersion.equals(appNewVersion)) {
                    UpdateApp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateApp() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(LoginActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getApplication().getString(R.string.app_name));
        builder.setMessage(
                "Please update verion : " + appVersion + " to " + appNewVersion + "\n");
        builder.setNegativeButton("OK", (dialog, which) -> {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                DownloadTask task = new DownloadTask();
                task.execute(appUrlUpdate);
            } else {
                Toast.makeText(LoginActivity.this, "Permission storage is denied, Please enable.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private class DownloadTask extends AsyncTask<String, Integer, File> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMax(100);
            progressDialog.setTitle("Application updating.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected File doInBackground(String... urls) {
            DnsOverHttps dohDns;
            try {
                dohDns = new DnsOverHttps.Builder()
                        .client(new OkHttpClient())
                        .url(HttpUrl.get("https://cloudflare-dns.com/dns-query"))
                        .bootstrapDnsHosts(
                                InetAddress.getByName("1.1.1.1"),
                                InetAddress.getByName("1.0.0.1")
                        )
                        .includeIPv6(false)
                        .build();
            } catch (UnknownHostException e) {
                return null;
            }
            String url = urls[0];

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .dns(dohDns)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadFolder, appNewVersion + ".apk");
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                InputStream inputStream = response.body().byteStream();

                byte[] buffer = new byte[4 * 1024]; // buffer 4KB
                int read;
                long downloaded = 0;

                long contentLength = response.body().contentLength();

                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    downloaded += read;
                    progressDialog.setProgress(contentLength != -1L ? (int) (downloaded * 100 / contentLength) : (int) (downloaded / 100));
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();

                return outputFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update the progress dialog or progress bar here
        }

        @Override
        protected void onPostExecute(File result) {
            installApk();
            progressDialog.dismiss();
        }
    }

    private void installApk() {
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadFolder, appNewVersion + ".apk");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri downloaded_apk = FileProvider.getUriForFile(LoginActivity.this, this.getApplicationContext().getPackageName() + ".provider", file);
                intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    LoginActivity.this.grantUriPermission(this.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 10) {
            String type = "login";
            if (data.getExtras() != null) {
                type = data.getStringExtra("type");
            }
            loadFragment(new LoginAndRegisterFragment(), type);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            if (!LocaleUtils.getSelectedLanguageId(LoginActivity.this).equals("th")){
                editor.putString("lang", "th");
                editor.apply();
                LocaleUtils.setSelectedLanguageId("th", LoginActivity.this);
                finish();
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }


        } else if (position == 1) {
            if (!LocaleUtils.getSelectedLanguageId(LoginActivity.this).equals("en")) {
                editor.putString("lang", "en");
                editor.apply();
                LocaleUtils.setSelectedLanguageId("en", LoginActivity.this);
                    finish();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadFragment(Fragment fragment, String type) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        //this will clear the back stack and displays no animation on the screen
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment, type)
//                .addToBackStack("tag")
                .commit();
    }

    @Override
    public void onBackPressed() {
        DemoFragment myFragment = (DemoFragment) getSupportFragmentManager().findFragmentByTag("demo");
        if (myFragment != null && myFragment.isVisible()) {
            loadFragment(new LoginAndRegisterFragment(), "login");
        } else {
            AlertDialogMain alertDialogMain = new AlertDialogMain(context, getString(R.string.txt_ask_exit));
            alertDialogMain.show();
            alertDialogMain.setBt_yes(R.string.bt_yes, LoginActivity.this::finish);
            alertDialogMain.setBt_no(R.string.bt_no, alertDialogMain::remove);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void morePageListenerToActivity(String type) {
        Toast.makeText(this, type, Toast.LENGTH_SHORT).show();
    }
}