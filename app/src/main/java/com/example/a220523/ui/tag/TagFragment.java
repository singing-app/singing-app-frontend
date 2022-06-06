package com.example.a220523.ui.tag;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a220523.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a220523.databinding.FragmentTagBinding;
import com.example.a220523.ui.tag.TagViewModel;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TagFragment extends Fragment {
    private TagViewModel tagViewModel;
    private FragmentTagBinding binding;
    Context context;

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mRecyclerAdapter;
    ArrayList<FriendItem> mfriendItems;
    public FriendItem tempFriendItem;
    public ChipGroup chipGroup;
    int cnt = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);

        binding = FragmentTagBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = container.getContext();

        tempFriendItem = new FriendItem();
        mfriendItems = new ArrayList<>();

        chipGroup = binding.chipgroup;

        final int chipGroupSize = chipGroup.getChildCount();

        mRecyclerView = binding.recyclerView;

        /* initiate adapter */
        mRecyclerAdapter = new MyRecyclerAdapter();

        /* initiate recyclerview */
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));



        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            private int targetOctave = -1;
            private String targetPitch = null;
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                ArrayList<String> tempArrList;
                tempFriendItem.init();
                mfriendItems.clear();
                mRecyclerAdapter.setFriendList(mfriendItems);

                cnt = 1;

                for(int i = 0; i < chipGroupSize; i++){
                    Chip chip = (Chip)chipGroup.getChildAt(i);

                    if(chip.isChecked()){
                        if(i >= 0 && i <= 3){
                            tempArrList = new ArrayList<>(Arrays.asList("파", "솔", "라", "시"));
                            targetOctave = 2;
                            targetPitch = tempArrList.get(i);
                        }
                        else if(i >= 4 && i <= 9){
                            int idx = i - 4;
                            tempArrList = new ArrayList<>(Arrays.asList("도", "레", "미", "파", "솔", "라"));
                            targetOctave = 3;
                            targetPitch = tempArrList.get(idx);
                        }

                        final String tOctave = targetOctave + "옥";
                        db.collection(tOctave).document(targetPitch).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> tempDBMap;

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    tempDBMap = document.getData();


                                    mfriendItems.clear();
                                    /* adapt data */
                                    for (Map.Entry<String, Object> elem : tempDBMap.entrySet()) {
                                        mfriendItems.add(new FriendItem(String.valueOf(cnt), R.drawable.icon_cd, elem.getKey(), String.valueOf(elem.getValue()), document.getId(), tOctave));
                                        cnt++;
                                        // Log.d(TAG, "Tag - octave text: " + document.getId());
                                    }
                                    mRecyclerAdapter.addFriendList(mfriendItems);


                                } else {
                                    Log.d(TAG, "Error getting documents", task.getException());
                                }
                            }
                        });
                    }
                    // else
                }
            }
        });
        // chipGroup.getChildAt(x).performClick();
        // Fragment called by PitchFragment. Max pitch based music recommendations.
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                int resultIdx = Integer.parseInt(bundle.getString("bundleKey"));
                if(resultIdx < 0 || resultIdx > 9) return;     // error catch

                chipGroup.getChildAt(resultIdx).performClick();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
