package com.iptv.newiptvmobile.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvDemoMenuAdapter;
import com.iptv.newiptvmobile.adapter.RvDemoSubAdapter;
import com.iptv.newiptvmobile.adapter.StartSnapHelper;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemoFragment extends Fragment {

    RecyclerView recyclerView_category_demo,recyclerView_demo_data;
    RvDemoMenuAdapter rvDemoMenuAdapter;
    RvDemoSubAdapter rvDemoSubAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String apiurl;
    SharedPreferences db;

    public DemoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemoFragment newInstance(String param1, String param2) {
        DemoFragment fragment = new DemoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = getActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        new LoadData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        recyclerView_category_demo = view.findViewById(R.id.recyclerView_category_demo);
        recyclerView_demo_data = view.findViewById(R.id.recyclerView_demo_data);
        // Inflate the layout for this fragment
        return view;
    }

    private class LoadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
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
                return "DnsInitError";
            }
            OkHttpClient okHttpClient2 = new OkHttpClient().newBuilder()
                    .dns(dohDns)
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "demo")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "ServerError";
                }
            } catch (IOException e) {
                return "InternetError";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.contains("InternetError")) {
                Toast.makeText(getActivity(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(getActivity(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();

//            Toast.makeText(getContext(),String.valueOf(jsonString), Toast.LENGTH_SHORT).show();
            recyclerView_category_demo.setLayoutManager(new StaggeredGridLayoutManager(10,StaggeredGridLayoutManager.VERTICAL));
            rvDemoMenuAdapter = new RvDemoMenuAdapter(data, getContext(), new RvDemoMenuListener() {
                @Override
                public void rvDemoMenuListener(int position, String title, String id) {

//                    recyclerView_demo_data.setLayoutManager(new StaggeredGridLayoutManager(8,StaggeredGridLayoutManager.HORIZONTAL));
                    recyclerView_demo_data.setLayoutManager(new GridLayoutManager(getContext(),8,GridLayoutManager.VERTICAL, false));
                    rvDemoSubAdapter = new RvDemoSubAdapter(data.get(position).getData(), getContext(), new RvDemoSubListener() {
                        @Override
                        public void rvDemoSubListener(int position, String title, String id) {
                            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                        }
                    });
                    recyclerView_demo_data.setAdapter(rvDemoSubAdapter);

                }
            });

            recyclerView_category_demo.setAdapter(rvDemoMenuAdapter);
            recyclerView_category_demo.requestFocus();

//            recyclerView_demo_data.setLayoutManager(new StaggeredGridLayoutManager(8,StaggeredGridLayoutManager.VERTICAL));
            recyclerView_demo_data.setLayoutManager(new GridLayoutManager(getContext(),8,GridLayoutManager.VERTICAL, false));
            rvDemoSubAdapter = new RvDemoSubAdapter(data.get(0).getData(), getContext(), new RvDemoSubListener() {
                @Override
                public void rvDemoSubListener(int position, String title, String id) {
                    Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                }
            });
            recyclerView_demo_data.setAdapter(rvDemoSubAdapter);
            LinearSnapHelper snapHelper = new StartSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView_demo_data);

        }
    }
}