package com.iptv.newiptvmobile.ui.tool.point;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.newiptvmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView text_point;

    SharedPreferences db;
    SharedPreferences.Editor db_edit;

    String point;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PointMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointMainFragment newInstance(String param1, String param2) {
        PointMainFragment fragment = new PointMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = getContext().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        point = db.getString("point", "");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_point_main, container, false);
        text_point = view.findViewById(R.id.text_point);

        text_point.setText(" " + point + getString(R.string.txt_point));
        // Inflate the layout for this fragment
        return view;
    }
}