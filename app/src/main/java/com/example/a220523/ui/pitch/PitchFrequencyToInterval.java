package com.example.a220523.ui.pitch;

public class PitchFrequencyToInterval {
    float[][] FreqList;
    float[] FreqListNoSharp;     // No sharp, only one dimension.
    String[] IntervalList;
    String result;
    int octave;
    float searchIpt;
    boolean status;
    int FreqRowLength;
    int FreqcolumnLength;

    PitchFrequencyToInterval(){
        FreqList = new float[][] {
                {32.7032f, 34.6478f, 36.7081f, 38.8909f, 41.2034f, 43.6535f, 46.2493f, 48.9994f, 51.9130f, 55.0000f, 58.2705f, 61.7354f},
                {65.4064f, 69.2957f, 73.4162f, 77.7817f, 82.4069f, 87.3071f, 92.4986f, 97.9989f, 103.8262f, 110.0000f, 116.5409f, 123.4708f},
                {130.8128f, 138.5913f, 146.8324f, 155.5635f, 164.8138f, 174.6141f, 184.9972f, 195.9977f, 207.6523f, 220.0000f, 233.0819f, 246.9417f},
                {261.6256f, 277.1826f, 293.6648f, 311.1270f, 329.6276f, 349.2282f, 369.9944f, 391.9954f, 415.3047f, 440.0000f, 466.1638f, 493.8833f},
                {523.5211f, 554.3653f, 587.3295f, 622.2540f, 659.2551f, 698.4565f, 739.9888f, 783.9909f, 830.6094f, 880.0000f, 932.3275f, 987.7666f},
                {1046.5020f, 1108.7310f, 1174.6590f, 1244.5080f, 1318.5100f, 1396.9130f, 1479.9780f, 1567.9820f, 1661.2190f, 1760.0000f, 1864.6550f, 1975.5330f},
                {2093.0050f, 2217.4610f, 2347.3180f, 2489.0160f, 2637.0200f, 2793.8260f, 2959.9550f, 3135.9630f, 3322.4380f, 3520.0000f, 3729.3100f, 3951.0660f},
                {4186.0090f, 4434.9220f, 4698.6360f, 4978.0320f, 5274.0410f, 5587.6520f, 5919.9110f, 6271.9270f, 6644.8750f, 7040.0000f, 7458.6200f, 7902.1330f}
        };

        FreqListNoSharp = new float[]{      // No sharp, only one dimension.
                32.7032f, 36.7081f, 41.2034f, 43.6535f, 48.9994f, 55.0000f, 61.7354f,
                65.4064f, 73.4162f, 82.4069f, 87.3071f, 97.9989f, 110.0000f, 123.4708f,
                130.8128f, 146.8324f, 164.8138f, 174.6141f, 195.9977f, 220.0000f, 246.9417f,
                261.6256f, 293.6648f, 329.6276f, 349.2282f, 391.9954f, 440.0000f, 493.8833f,
                523.5211f, 587.3295f, 659.2551f, 698.4565f, 783.9909f, 880.0000f, 987.7666f,
                1046.5020f, 1174.6590f, 1318.5100f, 1396.9130f, 1567.9820f, 1760.0000f, 1975.5330f,
                2093.0050f, 2347.3180f, 2637.0200f, 2793.8260f, 3135.9630f, 3520.0000f, 3951.0660f,
                4186.0090f, 4698.6360f, 5274.0410f, 5587.6520f, 6271.9270f, 7040.0000f, 7902.1330f
        };

        IntervalList = new String[]{
                "도", "도#", "레", "레#", "미", "파", "파#", "솔", "솔#", "라", "라#", "시"
        };

        FreqRowLength = FreqList.length;
        FreqcolumnLength = FreqList[0].length;
    }

    public String FI(float ipt){        // Frequency by Interval
        result = null;
        octave = 0;
        searchIpt = ipt;
        status = false;

        for (int i = 0; i < FreqRowLength; i++) {
            if(status){ break; }
            for (int j = 0; j < FreqcolumnLength; j++) {
                if(searchIpt < FreqList[i][j]){
                    if((j == 0) && (i != 0)){
                        j = FreqcolumnLength - 1;
                        i--;
                    }
                    result = IntervalList[j];
                    octave = i+1;

                    status = true;
                    break;
                }
            }
        }
        result = octave + "옥타브 " + result;
        return result;
    }

    public float IF(int oc, String ipt){        // Frequency by Interval
        float result = 0;
        int intervalLength = IntervalList.length;

        for(int i = 0; i < intervalLength; i++){
            if(IntervalList[i].equals(ipt)){
                result = FreqList[oc][i];
            }
        }
        return result;
    }

    public boolean isPossibleGetTagIdx(float itv){
        boolean status = false;
        if(itv >= 77.7817f && itv <= 174.6141f){
            status = true;
        }

        return status;
    }

    public int getTagIdxByInterval(float itv){      // input itv(interval) must be 77.7817 <= itv >= 174.6141
        final float targetItv = itv;
        final int tagMinIntervalIdx = 10;
        final int tagMaxIntervalIdx = 19;
        int result = 0;

        if(targetItv < 77.7817f || targetItv > 174.6141f)       // error catch
            return -1;

        for(int i = tagMaxIntervalIdx; i <= tagMaxIntervalIdx + 1; i++){
            if(targetItv < FreqListNoSharp[i]){
                result = i - tagMinIntervalIdx - 1;
                break;
            }
        }
        return result;
    }

}
