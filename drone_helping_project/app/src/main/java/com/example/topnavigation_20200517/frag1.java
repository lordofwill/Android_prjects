package com.example.topnavigation_20200517;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class frag1 extends Fragment {
    private View view;
    MyHandler myHandler;
    private FragmentActivity mcontext;

    SimpleDateFormat getDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat getTime = new SimpleDateFormat("kkmm");
    SimpleDateFormat getHours = new SimpleDateFormat("kk");
    SimpleDateFormat  getMinute = new SimpleDateFormat("mm");

   public int kind = 0;

    public String nx = "57";
    public String ny = "74";
    double wind = 0; // 풍속 double값 변환
    int humidityInt = 0;

    int[] time = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23}; //시간 변수

    Date date  = new Date();
    private String nowDate;  //현재 날짜
    private String nowTime; // 현재 시간
    private String nowHours; //현재 시
    private String nowMinute; //현재 분
    private int nowHoursInt;  //현재 시를 int 값으로 변환
    private int nowMinuteInt; //현재 분을 int 값으로 변환
    private boolean pty = true; //"PTY" 값이 변하도록 설정

    ImageView droneImage;
    TextView temperaturesText;
    TextView wind_speedText;
    TextView Advertising;
    TextView currentP;
    TextView humidityText;


    String skyStr; //하늘 상태
    String wind_speed; //풍속
    String temperatures; // 기온
    String humidity; //습도
    double longitude=126.97; //경도
    double latitude=37.56; //위도
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragsun, container, false);
        droneImage = view.findViewById(R.id.droneImage);
        humidityText = view.findViewById(R.id.humidity);
        temperaturesText = view.findViewById(R.id.temperaturesText);
        wind_speedText = view.findViewById(R.id.wind_speed);
        currentP = view.findViewById(R.id.currentP);
        myHandler = new MyHandler();

        frag1Thread thread = new frag1Thread();
        thread.start();
        return view;
    }

    //자기장 , 날씨 api 불러오기
        class frag1Thread extends Thread {

                    @Override
                    public void run() {
        nowDate = getDate.format(date);
        nowTime = getTime.format(date);
//
        getLocation();//위치값 받기

        nowHours = getHours.format(new Date());
        nowMinute = getMinute.format(new Date());

        nowHoursInt  = Integer.parseInt(nowHours);
        nowMinuteInt  = Integer.parseInt(nowMinute);
        getTime();

        getJson(1);
        getJson(2);


            }
        }

        //현위치를 받아오는 메소드
    public void getLocation() {
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        } else {
            Toast.makeText(this.getActivity(),"위치정보를 파악하려면 위치추적 허가를 하셔야 합니다.",Toast.LENGTH_SHORT).show();
        }
    }

    public void getTime(){

        for(int i = 0; i<= time.length; i++){
            if(nowHoursInt<= time[i]){
                if(nowHoursInt == time[i]){
                    if(nowMinuteInt <= 45) {

                        switch (time[i]){
                            case 1:
                                nowTime = "0030";
                                break;
                            case 2:
                                nowTime = "0130";
                                break;
                            case 3:
                                nowTime = "0230";
                                break;
                            case 4:
                                nowTime = "0330";
                                break;
                            case 5:
                                nowTime = "0430";
                                break;
                            case 6:
                                nowTime = "0530";
                                break;
                            case 7:
                                nowTime = "0630";
                                break;
                            case 8:
                                nowTime = "0730";
                                break;
                            case 9:
                                nowTime = "0830";
                                break;
                            case 10:
                                nowTime = "0930";
                                break;
                            default:
                                nowTime = (Integer.toString(time[i-1])) + "30";

                        }
                        getJson(2);
                        System.out.println("0 ~ 45분 : " + nowTime);
                        if(nowMinuteInt == 45) {
                            switch (time[i]){
                                case 1:
                                    nowTime = "0030";
                                    break;
                                case 2:
                                    nowTime = "0130";
                                    break;
                                case 3:
                                    nowTime = "0230";
                                    break;
                                case 4:
                                    nowTime = "0330";
                                    break;
                                case 5:
                                    nowTime = "0430";
                                    break;
                                case 6:
                                    nowTime = "0530";
                                    break;
                                case 7:
                                    nowTime = "0630";
                                    break;
                                case 8:
                                    nowTime = "0730";
                                    break;
                                case 9:
                                    nowTime = "0830";
                                    break;
                                case 10:
                                    nowTime = "0930";
                                    break;
                                default:
                                    nowTime = (Integer.toString(time[i-1])) + "30";

                            }
                        }
                        break;
                    }else{
                    //46분~
                        switch (time[i]){
                            case 1:
                                nowTime = "0030";
                                break;
                            case 2:
                                nowTime = "0130";
                                break;
                            case 3:
                                nowTime = "0230";
                                break;
                            case 4:
                                nowTime = "0330";
                                break;
                            case 5:
                                nowTime = "0430";
                                break;
                            case 6:
                                nowTime = "0530";
                                break;
                            case 7:
                                nowTime = "0630";
                                break;
                            case 8:
                                nowTime = "0730";
                                break;
                            case 9:
                                nowTime = "0830";
                                break;
                            case 10:
                                nowTime = "0930";
                                break;
                            default:
                                nowTime = (Integer.toString(time[i-1])) + "30";

                        }
                        getJson(2);
                        System.out.println("46분 ~ : " + nowTime);
                        break;
                     }
            }else {
                    switch (time[i]){
                        case 1:
                            nowTime = "0030";
                            break;
                        case 2:
                            nowTime = "0130";
                            break;
                        case 3:
                            nowTime = "0230";
                            break;
                        case 4:
                            nowTime = "0330";
                            break;
                        case 5:
                            nowTime = "0430";
                            break;
                        case 6:
                            nowTime = "0530";
                            break;
                        case 7:
                            nowTime = "0630";
                            break;
                        case 8:
                            nowTime = "0730";
                            break;
                        case 9:
                            nowTime = "0830";
                            break;
                        case 10:
                            nowTime = "0930";
                            break;
                        default:
                            nowTime = (Integer.toString(time[i-1])) + "30";

                    }
                    getJson(2);
                    System.out.println("46분 ~ : " + nowTime);
                    break;
                }
            }
        }

    }

    //자기장 파싱
    private String getJson(int kind){

        String value = "";

            if(kind == 1){ //자기장
                try{
                    String result = "";
                    String apiUrl = "https://spaceweather.rra.go.kr/api/kindex";
                    URL url = new URL(apiUrl);
                    BufferedReader br;

                    br =  new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    while ((line = br.readLine())!= null){
                        result += line;
                    }

                    JSONObject jsonObject1 = new JSONObject(result);
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("kindex");
                    int cu = jsonObject2.getInt("currentP");
                    value = String.valueOf(cu);
                    final String finalValue = value;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    currentP.setText(finalValue + "kp");
                                }
                            });
                        }
                    }).start();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(kind == 2){ //날씨
                String result = "";
                try {

                    String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst?"
                            +"serviceKey=X7eiuVFqij%2BqvTtwYaOUx4SfsrsSjGIDU8Mjgf0AB%2Bdd0sYqpbhrZK5LEqSc7ufTDFrGu8tm8j5iHkd2z3M%2FFg%3D%3D"
                            +"&pageNo=1&numOfRows=240&dataType=JSON"
                            +"&base_date="+nowDate+"&base_time="+nowTime+"&nx="+nx+"&ny="+ny+"&";
                    URL url = new URL(apiUrl);
                    BufferedReader br;

                    br =  new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    while ((line = br.readLine())!= null){
                        result += line;
                    }

                    JSONObject jsonObject1  = new JSONObject(result);
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("response");
                    JSONObject jsonObject3 = jsonObject2.getJSONObject("body");
                    JSONObject jsonObject4 = jsonObject3.getJSONObject("items");
                    JSONArray jsonArray = jsonObject4.getJSONArray("item");

                    for(int i = 0; i<jsonArray.length(); i++) {
                        JSONObject weatherJson = jsonArray.getJSONObject(i);
                        String category = weatherJson.getString("category"); // 자료구분코드
                        String fcstValue = weatherJson.getString("fcstValue"); //예보 값
                        value += "category : " + category;
                        switch (category) {
                            case "T1H": //기온
                                temperatures = weatherJson.getString("fcstValue");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                temperaturesText.setText(temperatures + "˚  |  " + skyStr);
                                            }
                                        });
                                    }
                                }).start();
                                continue;
                            case "PTY": // 비, 눈
                                skyStr = weatherJson.getString("fcstValue");
                                if (fcstValue.equals("0")) {
                                    continue;
                                } else {
                                    fcstValue = fcstValue;
                                    category = "PTY";
                                    pty = false;

                                    if (skyStr.equals("1")) {
                                        skyStr = "비";
                                    } else if (skyStr.equals("2")) {
                                        skyStr = "비/눈";
                                    } else if (skyStr.equals("3")) {
                                        skyStr = "눈";
                                    } else if (skyStr.equals("4")) {
                                        skyStr = "소나기";
                                    }
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                temperaturesText.setText(temperatures + "˚  |  " + skyStr);
                                            }
                                        });
                                    }
                                }).start();
                                    continue;

                                    case "SKY": //하늘
                                        skyStr = weatherJson.getString("fcstValue");
                                        if(category.equals("SKY") && pty){
                                            pty = true;

                                            if(skyStr.equals("1")){
                                                skyStr = "맑음";
                                                System.out.println("1, 맑음");
                                            }else if(skyStr.equals("3")){
                                                skyStr = "구름 많음";
                                                System.out.println("3, 구름 많음");
                                            }else if(skyStr.equals("4")){
                                                skyStr = "흐림";
                                                System.out.println("4, 흐림");
                                            }
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    myHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            temperaturesText.setText(temperatures + "˚  |  " + skyStr);
                                                        }
                                                    });
                                                }
                                            }).start();
                                            continue;
                                        }

                                        continue;
                                    case "REH": //습도
                                        humidity = weatherJson.getString("fcstValue");
                                        humidityInt = Integer.parseInt(humidity);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        humidityText.setText(humidity + "%");
                                                    }
                                                });
                                            }
                                        }).start();
                                        continue;
                                    case "WSD": //풍속
                                        wind_speed = weatherJson.getString("fcstValue");
                                        wind = Double.valueOf(wind_speed);

                                if(wind < 4){ //바람이 약하다
                                    wind_speed = wind + "m/s  |  보통";


                                }else if(wind >= 4 && wind <9) { //바람이 약간 강하다
                                    wind_speed = wind + "m/s  |  약간 강";

                                }else if(wind >= 9 && wind < 14){ //바람이 강하다
                                    wind_speed = wind + "m/s  |  강";

                                }else if(wind >= 14){ //바람이 매우 강하다
                                    wind_speed = wind + "m/s  |  매우 강";

                                }
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        wind_speedText.setText(wind_speed);
                                                    }
                                                });
                                            }
                                        }).start();
                                        continue;
                                }

                               continue;
                    }

                    wind = 4;
                    humidityInt= 81;
                    if(wind <=4 && humidityInt <= 80 && skyStr.equals("1")){ //적당
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        droneImage.setImageResource(R.drawable.droneok);
                                    }
                                });
                            }
                        }).start();
                    }else if(humidityInt >= 81 && 5 <= wind  && wind >=6 && skyStr.equals("3") && skyStr.equals("4")) {  //주의
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        droneImage.setImageResource(R.drawable.droneheed);
                                    }
                                });
                            }
                        }).start();
                        if(humidityInt >= 81){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            humidityText.setText("! " + humidity + "%");
                                        }
                                    });
                                }
                            }).start();
                            if(5 <= wind  && wind >=6 && skyStr.equals("3") && skyStr.equals("4")){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                wind_speedText.setText("! "+ wind_speed);
                                            }
                                        });
                                    }
                                }).start();
                                if(skyStr.equals("3") && skyStr.equals("4")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    temperaturesText.setText("! "+ temperatures + "˚  |  " + skyStr);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }else if(5 <= wind  && wind >=6){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            wind_speedText.setText("! "+ wind_speed);
                                        }
                                    });
                                }
                            }).start();
                            if(humidityInt >= 81){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                humidityText.setText("! " + humidity + "%");
                                            }
                                        });
                                    }
                                }).start();
                                if(skyStr.equals("3") && skyStr.equals("4")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    temperaturesText.setText("! "+ temperatures + "˚  |  " + skyStr);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }else if(skyStr.equals("3") && skyStr.equals("4")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            temperaturesText.setText("! "+ temperatures + "˚  |  " + skyStr);
                                        }
                                    });
                                }
                            }).start();
                            if(5 <= wind  && wind >=6) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                wind_speedText.setText("! " + wind_speed);
                                            }
                                        });
                                    }
                                }).start();
                                if(humidityInt >= 81){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    humidityText.setText("! " + humidity + "%");
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }
                    }else { //불가
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        droneImage.setImageResource(R.drawable.dronestop);
                                    }
                                });
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return value;
    }
    public static class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0){
                System.out.println("Handler sendEmptyMessage() 를 통한 handleMessage() 실행");
            }
        }
    }

}

