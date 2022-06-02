package com.example.a220523.ui.chart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import com.example.a220523.databinding.ActivityMelonChartBinding;
import com.example.a220523.databinding.FragmentChartBinding;

public class ChartFragment extends Fragment {
    private ChartViewModel chartViewModel;
    private ActivityMelonChartBinding binding;
    private FragmentChartBinding binding_2;

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    String melon_chart_url = "https://www.melon.com/chart/index.htm";
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chartViewModel =
                new ViewModelProvider(this).get(ChartViewModel.class);

        binding = ActivityMelonChartBinding.inflate(inflater, container, false);
        binding_2 = FragmentChartBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        context = container.getContext();

        // final TextView textView = binding.textChart;
        chartViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        recyclerView = binding.recyclerViewChart;
                // findViewById(R.id.recyclerView_chart);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        getData();

        return root;
    }

    private void getData(){
        MelonJsoup jsoupAsyncTask = new MelonJsoup();
        jsoupAsyncTask.execute();
    }
    private class MelonJsoup extends AsyncTask<Void, Void, Void> {
        ArrayList<String> listTitle = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        ArrayList<String> listUrl = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(melon_chart_url).get();
                final Elements rank_list1 = doc.select("div.wrap_song_info div.ellipsis.rank01 span a");
                final Elements rank_list_name = doc.select("div.wrap_song_info div.ellipsis.rank02 span a");

                final Elements image_list1 = doc.select("tr#lst50.lst50 div.wrap a.image_typeAll img");
                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //순위정보
                        for(Element element: rank_list1) {
                            listTitle.add(element.text());
                        }
                        //가수정보
                        for (Element element : rank_list_name) {
                            listName.add(element.text());
                        }
                        // 이미지정보
                        for (Element element : image_list1){
                            listUrl.add(element.attr("src"));
                        }

                        for (int i = 0; i < 30 ; i++) {
                            ChartDTO data = new ChartDTO();
                            data.setTitle(listTitle.get(i));
                            data.setImageUrl(listUrl.get(i));
                            data.setRankNum(String.valueOf(i+1));
                            data.setName(listName.get(i));

                            adapter.addItem(data);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
