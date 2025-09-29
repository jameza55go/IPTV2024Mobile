package com.iptv.newiptvmobile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvListCategory;
import com.iptv.newiptvmobile.model.SubData;

import java.util.List;


public class CategoryDialog extends Dialog{

    CategoryActivity activity;
    RecyclerView rv_list_category;
    RvListCategoryAdapter adapter;
    OnClickRvListCategory listener;

    List<SubData> list;

    public CategoryDialog(CategoryActivity activity,List<SubData> list,OnClickRvListCategory listener) {
        super(activity,android.R.style.Theme_Translucent_NoTitleBar);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.list = list;
        this.listener = listener;
    }

    public CategoryDialog(CategoryActivity activity, int themeResId, List<SubData> list, OnClickRvListCategory listener) {
        super(activity, themeResId);
        this.activity = activity;
        this.listener = listener;
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.category_dialog);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        rv_list_category = findViewById(R.id.rv_list_category);

        rv_list_category.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        adapter = new RvListCategoryAdapter(activity, list
                , (title,id, subId) -> {
            listener.onClickRvListCategory(title,id,subId);
            dismiss();
        });
        rv_list_category.setAdapter(adapter);


    }

}