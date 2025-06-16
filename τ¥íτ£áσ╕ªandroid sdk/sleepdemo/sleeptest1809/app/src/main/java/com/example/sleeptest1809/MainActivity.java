package com.example.sleeptest1809;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.bltech.mobile.utils.ST_sleep_info;
import com.jstyle.sleep.util.SleepDataListener;
import com.jstyle.sleeplibrary.ResloveUtil;
import com.jstyle.sleeplibrary.SleepDetail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.sleep_hourvalue)
    TextView sleep_hourvalue;
    @BindView(R.id.fenzhongvalue)
    TextView fenzhongvalue;
    @BindView(R.id.sleep_start_endtime)
    TextView sleep_start_endtime;
    @BindView(R.id.sleep_styletext)
    TextView sleep_styletext;
    @BindView(R.id.info)
    TextView info;

    @BindView(R.id.sleep_view)
    SleepView sleep_view;

    static {
        try {
            System.loadLibrary("sleep1657-lib");
        } catch (Throwable e) {
            Log.e("test", e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        float []dta=    new float[1440];
        float []dtab=    new float[1440];
        List<Integer>  aa=   readCSV(this);
        for (int i=0;i<1440;i++){
            dta[i]= aa.get(i).floatValue();
            dtab[i]= aa.get(i).floatValue();;
        }
        //传 0， null, new float[0],或者真数据
        ResloveUtil.getSleepDetail(dta,dtab, new SleepDataListener() {
            @Override
            public void getSleepDetail(SleepDetail sleepDetail) {
                if(0!=sleepDetail.getSleepSegment()){
                    Log.e("sleepDetail",sleepDetail.toString());
                    getSleepfordata(sleepDetail.getNewData(), sleepDetail.getSt_sleep_info() );

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Short.valueOf(sleepDetail.getSt_sleep_info() .getSt_sleep_evalution().getU16_total_sleep_segment()).intValue() != 0) {
                                sleep_hourvalue.setText(formatHour(sleepDetail.getRestTime()));
                                fenzhongvalue.setText(formatMinte(sleepDetail.getRestTime()));
                                info.setText("Off_Bed:"+sleepDetail.getOff_BedTime()
                                        +"DeepSleep:"+sleepDetail.getDeepTime()
                                        +" LightSleep:"+sleepDetail.getLightTime()
                                        +"Awake:"+sleepDetail.getAwakeTime()
                                        +"Rem:"+sleepDetail.getRemTime()  );
                                final int size = Short.valueOf(sleepDetail.getSt_sleep_info() .getSt_sleep_evalution().getU16_total_sleep_segment()).intValue();
                                sleep_start_endtime.setText("(" +getCountTimeer(sleepDetail.getSt_sleep_info() .getU16_start_time()[0]) + "-" + getCountTimeer(sleepDetail.getSt_sleep_info() .getU16_end_time()[size - 1]) + ")");
                                sleep_view.setDataSource(timeArray, new SleepView.Xylistener() {
                                    @Override
                                    public void ReadEcgData(Sleepfordata X) {
                                        if (null != X) {
                                            String sleepStatus = "";
                                            switch (X.getIndex()) {
                                                case 0:
                                                    sleepStatus = "Off_Bed";
                                                    break;
                                                case 1:
                                                    sleepStatus = "Deep_Sleep";
                                                    break;
                                                case 2:
                                                    sleepStatus = "Light_Sleep";
                                                    break;
                                                case 4:
                                                    sleepStatus = "Awake";
                                                    break;
                                                case 3:
                                                    sleepStatus = ("REM");
                                                    break;
                                            }
                                            sleep_styletext.setText(sleepStatus);
                                            sleep_hourvalue.setText(formatHour(X.getCount()));
                                            fenzhongvalue.setText(formatMinte(X.getCount()));
                                            sleep_start_endtime.setText("(" + X.getBegintime() + "-" + X.getEndtime() + ")");
                                        } else {
                                            sleep_hourvalue.setText(formatHour(sleepDetail.getRestTime()));
                                            fenzhongvalue.setText(formatMinte(sleepDetail.getRestTime()));
                                            sleep_start_endtime.setText("(" + getCountTimeer(sleepDetail.getSt_sleep_info() .getU16_start_time()[0]) + "-" +getCountTimeer(sleepDetail.getSt_sleep_info() .getU16_end_time()[size - 1]) + ")");
                                        }
                                    }
                                }, sleepDetail.getRestTime());

                            } else {
                                sleep_view.clearView();
                                sleep_styletext.setText("Sleep_Time");
                                sleep_hourvalue.setText("0");
                                fenzhongvalue.setText("0");
                                sleep_start_endtime.setText("(00:00-00:00)");
                            }

                        }
                    });
                }else{
                    info.setText("no sleep data" );
                }

            }

            @Override
            public void OnRrror(long l, String s) {

            }
        });


    }




    //读取CSV文件
    public static List<Integer> readCSV(Activity activity){
        List<Integer> list=new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.cc)));

        try {

            String line = null;
            //因为不知道有几行数据，所以先存入list集合中
            while((line = reader.readLine()) != null){
                if(!"".equals(line)){
                    list.add(Integer.valueOf(line));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(reader != null){ reader.close();
                    reader=null;}
            }catch(Exception e){ }
        }

        return list;
    }




    Sleepfordata AA = null;
    int cout = 0;
    int indexone;
    int endindex;
    List<List<Sleepfordata>> timeArray = new ArrayList<>();

    private List<List<Sleepfordata>> getSleepfordata(List<byte[]> newData, ST_sleep_info st_sleep_info) {
        timeArray.clear();
        for (int i = 0; i < newData.size(); i++) {
            List<Sleepfordata> DATA = new ArrayList<>();
            cout = 0;
            endindex = 0;
            AA = null;
            for (int j = 0; j < newData.get(i).length; j++) {
                if (AA == null) {
                    String dd = null;
                    indexone = j + st_sleep_info.getU16_start_time()[i];
                    if (indexone == st_sleep_info.getSt_sleep_period()[i].getA0()) {
                        dd = "P1";
                        cout++;
                    }
                    AA = new Sleepfordata();
                    AA.setCount(1);
                    AA.setIndex(Byte.valueOf(newData.get(i)[0]).intValue());
                    AA.setBegintime(getCountTimeer(indexone));
                    AA.setP_INDEX(dd);
                    DATA.add(AA);
                } else {
                    String dd = null;
                    int index = j + indexone;
                    endindex = index;
                    if (index == st_sleep_info.getSt_sleep_period()[i].getA0()) {
                        dd = "P1";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA1()) {
                        dd = "P2";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA2()) {
                        dd = "P3";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA3()) {
                        dd = "P4";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA4()) {
                        dd = "P5";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA5()) {
                        dd = "P6";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA6()) {
                        dd = "P7";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA7()) {
                        dd = "P8";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA8()) {
                        dd = "P9";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA9()) {
                        dd = "P10";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA10()) {
                        dd = "P11";
                    } else if (index == st_sleep_info.getSt_sleep_period()[i].getA11()) {
                        dd = "P12";
                    }
                    if (AA.getIndex() == Byte.valueOf(newData.get(i)[j]).intValue()) {
                        AA.setCount(AA.getCount() + 1);
                        AA.setIndex(Byte.valueOf(newData.get(i)[j]).intValue());
                    } else {
                        AA.setEndtime(getCountTimeer(endindex));
                        AA = new Sleepfordata();
                        AA.setCount(1);
                        AA.setP_INDEX(dd);
                        AA.setBegintime(getCountTimeer(endindex));
                        AA.setIndex(Byte.valueOf(newData.get(i)[j]).intValue());
                        DATA.add(AA);
                    }
                }
            }
            DATA.get(DATA.size() - 1).setEndtime(getCountTimeer(endindex + 1));
            timeArray.add(DATA);
        }

        return timeArray;
    }

    public static String getCountTimeer(int count) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String base = "12:00";
        long time = 0;
        try {
            time = format.parse(base).getTime() + count  * 60 * 1000l;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return format.format(new Date(time));
    }


    private int getcount(List<byte[]> newData, int type) {
        int cout = 0;
        for (int i = 0; i < newData.size(); i++) {
            for (int j = 0; j < newData.get(i).length; j++) {
                if (type == Byte.valueOf(newData.get(i)[j]).intValue()) {
                    cout++;
                }
            }
        }
        return cout;
    }

    /**
     * 分钟转小时
     * @param seconds
     * @return
     */
    public static String formatHour(int seconds) {
        long hours = (int) Math.floor(seconds / 60);
        return hours+"";
    }

    public static String formatMinte(int seconds) {
        long minute = seconds % 60;
        return minute +"";
    }
}