package com.example.a220523.ui.pitch;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.a220523.R;
import com.example.a220523.databinding.FragmentTagBinding;
import com.example.a220523.ui.tag.TagFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

import com.example.a220523.databinding.FragmentPitchBinding;

public class PitchFragment extends Fragment implements HighPitchInterface{
    PitchFrequencyToInterval FTI = new PitchFrequencyToInterval();
    Context context;
    DbOpenHelper mDbOpenHelper;

    AudioDispatcher dispatcher;
    TarsosDSPAudioFormat tarsosDSPAudioFormat;
    File file;

    TextView pitchTextView;
    Button recordButton;
    // Button playButton;
    CheckBox highPitchCheckbox;

    HighPitchDialogActivity customDialog;

    boolean isRecording = false;
    String filename = "recorded_sound.wav";

    private LineChart chart;

    static int time = 0;
    public void setTime(int v){
        time = v;
    }
    public int getTime(){
        return time;
    }
    final int timeLimit = 3;

    Timer timer = new Timer();
    static TimerTask task;
    int remainingTime = 0;
    float userHighPitchAvg = 0;     // 유저 최고음

    boolean neverForcedMoveToTag = false;


    private TimerTask mkTimerTask() {
        setTime(0);
        TimerTask tempTesk = new TimerTask() {
            @Override
            public void run() {
                remainingTime = timeLimit - getTime();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pitchTextView.setText(" 낼 수 있는 최고음을 3초간 유지하세요: " + remainingTime);
                    }
                });
                time++;
                if(time > timeLimit){
                    isRecording = false;
                    setTime(0);
                    userHighPitchAvg = calcHighPitchAvg(recordPitchList);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitchTextView.setText("당신의 최고음은: " + FTI.FI(userHighPitchAvg));

                            if(FTI.isPossibleGetTagIdx(userHighPitchAvg) && !neverForcedMoveToTag) {
                                customDialog.show();
                                customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT);
                            }
                        }
                    });

                    mDbOpenHelper.insertColumn("test", userHighPitchAvg+"");

                    recordButton.setText("측정 시작");
                    stopRecording();
                }
            }
        };
        return tempTesk;
    }

    public float highPitchAvg = 0;

    private PitchViewModel PitchViewModel;
    private FragmentPitchBinding binding;
    private FragmentTagBinding tagBinding;
    private View root;
    private View tagRoot;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PitchViewModel = new ViewModelProvider(this).get(PitchViewModel.class);

        binding = FragmentPitchBinding.inflate(inflater, container, false);
        tagBinding = FragmentTagBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        tagRoot = tagBinding.getRoot();

        context = container.getContext();
        mDbOpenHelper = new DbOpenHelper(context);

        final TextView textView = binding.pitchTextView;
        PitchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });

        // timer.purge();
        super.onCreate(savedInstanceState);
        customDialog = new HighPitchDialogActivity(this.context, this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);



        chart = (LineChart) binding.LineChart;

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.BLACK);
        chart.setGridBackgroundColor(Color.BLACK);

// description text
        chart.getDescription().setEnabled(true);
        Description des = chart.getDescription();
        des.setEnabled(true);
        /*des.setText("Real-Time DATA");
        des.setTextSize(15f);
        des.setTextColor(Color.WHITE);*/

// touch gestures (false-비활성화)
        chart.setTouchEnabled(false);

// scaling and dragging (false-비활성화)
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

//auto scale
        chart.setAutoScaleMinMaxEnabled(true);

// if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

//X축
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setDrawAxisLine(false);

        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setDrawGridLines(false);

        /*
        //Legend
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setTextSize(12f);
        l.setTextColor(Color.WHITE);
        */

        // disable Legend
        chart.getLegend().setEnabled(false);

        //Y축
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(getResources().getColor(R.color.GREEN));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.GREEN));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);



// don't forget to refresh the drawing
        chart.invalidate();

        File sdCard = Environment.getExternalStorageDirectory();
        file = new File(sdCard, filename);

    /*
    filePath = file.getAbsolutePath();
    Log.e("MainActivity", "저장 파일 경로 :" + filePath); // 저장 파일 경로 : /storage/emulated/0/recorded.mp4
    */

        tarsosDSPAudioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                22050,
                2 * 8,
                1,
                2 * 1,
                22050,
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));

        pitchTextView = binding.pitchTextView;
        recordButton = binding.recordButton;
        // playButton = binding.playButton;
        highPitchCheckbox = binding.highPitchCheckbox;

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    recordAudio();
                    isRecording = true;
                    recordButton.setText("측정 중단");
                } else {
                    stopRecording();
                    isRecording = false;
                    recordButton.setText("측정 시작");
                }
            }
        });

        highPitchCheckbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                recordButton.setText("측정 시작");
                isRecording = false;
                stopRecording();
            }
        });

        /* playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        }); */

        // SQLite DB
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        Cursor iCursor = mDbOpenHelper.selectColumns();
        while(iCursor.moveToNext()){
            @SuppressLint("Range")
            String tempHighPitch = iCursor.getString(iCursor.getColumnIndex("highpitch"));

            userHighPitchAvg = Float.parseFloat(tempHighPitch);
        }
        if (userHighPitchAvg != 0){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final String s = "나의 최고 음정: "+ FTI.FI(userHighPitchAvg);
                    pitchTextView.setText(s);
                }
            });
        }


        return root;
    }

    public void addEntry(float num) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        data.addEntry(new Entry((float)set.getEntryCount(), num), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(100);
        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
    }

    public LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Real-time Line Data");
        set.setLineWidth(1.5f);
        set.setDrawValues(false);
        set.setValueTextColor(getResources().getColor(R.color.white));
        set.setColor(getResources().getColor(R.color.white));
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));

        return set;
    }

    ArrayList<Float> recordPitchList = new ArrayList<Float>();
    public float calcHighPitchAvg(ArrayList<Float> arr){
        if (arr.isEmpty()) { return -1; }

        final int size = arr.size();
        float result = 0;

        for(int i = 0; i < size; i++){
            result += arr.get(i);
        }
        result /= size;
        return result;
    }

    public void recordAudio() {
        releaseDispatcher();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            AudioProcessor recordProcessor = new WriterProcessor(tarsosDSPAudioFormat, randomAccessFile);
            dispatcher.addAudioProcessor(recordProcessor);

            if (highPitchCheckbox.isChecked()){
                recordPitchList.clear();
                task = mkTimerTask();
                timer.scheduleAtFixedRate(task, 10, 1000);
            }
            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                    final float pitchInHz = res.getPitch();
                    final float datanum = pitchInHz;

                    if (highPitchCheckbox.isChecked()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addEntry(datanum);
                                if(datanum != -1 && datanum < 5000) { recordPitchList.add(datanum); }
                            }
                        });
                    }

                    else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String result = FTI.FI(pitchInHz) + ": " + pitchInHz;
                                pitchTextView.setText(result);
                                addEntry(datanum);
                            }
                        });
                    }
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

            /* if(highPitchCheckbox.isChecked() && (getTime() >= timeLimit)){
                highPitchAvg = calcHighPitchAvg(datanumList);
                pitchTextView.setText("최고음: " + highPitchAvg);
                stopRecording();
                // timer.interrupt();
            } */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* public void playAudio()
    {
        try{
            releaseDispatcher();

            FileInputStream fileInputStream = new FileInputStream(file);
            dispatcher = new AudioDispatcher(new UniversalAudioInputStream(fileInputStream, tarsosDSPAudioFormat), 1024, 0);

            AudioProcessor playerProcessor = new AndroidAudioPlayer(tarsosDSPAudioFormat, 2048, 0);
            dispatcher.addAudioProcessor(playerProcessor);

            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e){
                    final float pitchInHz = res.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitchTextView.setText(pitchInHz + "");
                        }
                    });
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }*/

    public void stopRecording() {
        if(task != null) {
            task.cancel();
        }
        releaseDispatcher();
    }

    public void releaseDispatcher() {
        if (dispatcher != null) {
            if (!dispatcher.isStopped()) dispatcher.stop();
            dispatcher = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onYesButtonClicked() {
        int r = FTI.getTagIdxByInterval(userHighPitchAvg);

        Bundle result = new Bundle();
        result.putString("bundleKey", Integer.toString(r));
        getParentFragmentManager().setFragmentResult("requestKey", result);

        FragmentTransaction frt = requireActivity().getSupportFragmentManager().beginTransaction();
        frt.replace(tagRoot.getId(), new TagFragment());        // error point
        frt.commitAllowingStateLoss();
    }

    @Override
    public void onNoButtonClicked() {

    }

    @Override
    public void onNevCheckBoxChanged(boolean isChecked){
        neverForcedMoveToTag = isChecked;
    }
}