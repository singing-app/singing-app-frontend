package com.example.a220523.ui.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a220523.R;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private ArrayList<FriendItem> mFriendList = new ArrayList<FriendItem>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(mFriendList.get(position));
    }

    public void setFriendList(ArrayList<FriendItem> list){
        mFriendList.clear();

        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            FriendItem temp = list.get(i);
            if (Integer.valueOf(temp.getTxt_num()) < 10 ){
                temp.setTxt_num("0"+temp.getTxt_num());
            }
            this.mFriendList.add(new FriendItem(temp.getTxt_num(), temp.getResourceId(), temp.getText_title_insert(),
                    temp.getText_singer_insert(), temp.getText_pitch_insert(), temp.getOctave()));
        }
        notifyDataSetChanged();
    }

    public void addFriendList(ArrayList<FriendItem> list){
        if (mFriendList == null || mFriendList.isEmpty()){
            setFriendList(list);
        }
        else {
            mFriendList.addAll(list);
            notifyDataSetChanged();
        }


    }

    @Override
    public int getItemCount() {
        try{
            return mFriendList.size();
        } catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txt_num;
        TextView text_title_insert;
        TextView text_singer_insert;
        TextView text_pitch_insert;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            txt_num = (TextView) itemView.findViewById(R.id.txt_num);
            text_title_insert = (TextView) itemView.findViewById(R.id.text_title_insert);
            text_singer_insert = (TextView) itemView.findViewById(R.id.text_singer_insert);
            text_pitch_insert = (TextView) itemView.findViewById(R.id.text_pitch_insert);
        }

        void onBind(FriendItem item){
            String octaveAndPitch = item.getOctave() + "타브 " + item.getText_pitch_insert();

            imageView.setImageResource(item.getResourceId());
            txt_num.setText(item.getTxt_num());
            text_title_insert.setText(item.getText_title_insert());
            text_singer_insert.setText(item.getText_singer_insert());
            text_pitch_insert.setText(octaveAndPitch);
        }
    }
}