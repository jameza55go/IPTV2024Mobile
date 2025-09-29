package com.iptv.newiptvmobile.ui.tool.setting;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.CustomArraySettingMainMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class LivePlayerFragment extends Fragment {

    SharedPreferences db;
    SharedPreferences.Editor editor;
    String player;
    String currentPlayer;
    CustomArraySettingMainMenuAdapter adapter_row1;
    Spinner spinner_row_1;
    LinearLayout bt_confirm_change_setting_menu_main;
    TextView confirmtext;
    List<String> paths_row1 = new ArrayList<>();
    int FPositionRow1 = 0;
    String selectRow1;

    public LivePlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_player, container, false);
        db = requireContext().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        editor = requireContext().getSharedPreferences("iptv", MODE_PRIVATE).edit();
        player = db.getString("LivePlayer", "AndroidPlayer");
        spinner_row_1 = view.findViewById(R.id.spinner_row_1);
        bt_confirm_change_setting_menu_main = view.findViewById(R.id.bt_confirm_change_setting_menu_main);
        confirmtext = view.findViewById(R.id.confirmtext);

        paths_row1.add("Android Player");
        paths_row1.add("FW Player");

        if (player.equals("AndroidPlayer")){
            FPositionRow1 = 0;
        }else{
            FPositionRow1 = 1;
        }

        adapter_row1 = new CustomArraySettingMainMenuAdapter(requireContext(),
                R.layout.item_spiner, paths_row1);
        adapter_row1.setDropDownViewResource(R.layout.item_spiner_dropdown);
        spinner_row_1.setAdapter(adapter_row1);
        spinner_row_1.setSelection(FPositionRow1);
        spinner_row_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter_row1.setCurrentIndex(position);
                adapter_row1.notifyDataSetChanged();
                if (position == 0){
                    currentPlayer = "AndroidPlayer";
                }else{
                    currentPlayer = "FWPlayer";
                }

                Log.d("onItemSelected1", "onItemSelected: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_confirm_change_setting_menu_main.setOnClickListener(v -> {
            editor.putString("LivePlayer",currentPlayer);
            editor.apply();
            Toast.makeText(getActivity(),"Update Complete.",Toast.LENGTH_SHORT).show();
        });
        bt_confirm_change_setting_menu_main.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    confirmtext.setTextColor(getResources().getColor(R.color.black));
                }else{
                    confirmtext.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        return view;
    }


}