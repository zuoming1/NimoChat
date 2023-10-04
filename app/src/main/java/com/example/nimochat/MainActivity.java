package com.example.nimochat;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String TAG ="DTEST";
    private static final String API_KEY = "pppdAoqcAGQmspvGgRyTMUId";
    private static final String SECRET_KEY = "xB7vFFeo1YzkngyYv5c6VH4vY9hrQC1Y";

    // 百度 UNIT 机器人对话 API 的基础 URL
    private static final String API_BASE_URL = "https://aip.baidubce.com/rpc/2.0/unit/service";

    // 百度 UNIT 机器人的 Access Token
    private String accessToken;
    Button Btn_Req;
    EditText Edit_Req;
    TextView Text_Show;
    private TextToSpeech tts;
    private TextToSpeech textToSpeech;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Btn_Req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = "";
                String restlt1 = "";
                question = Edit_Req.getText().toString();
                Log.e(TAG, question);
                String finalQuestion = question;
                CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                            String AccessToken = "";
                            String restlt = "";
                            AccessToken = getAuth();
                            restlt = utterance(AccessToken, finalQuestion);
                            Log.e(TAG, restlt );
                            return restlt;
                        });
                try {
                    restlt1 = future1.get();
                    Text_Show.setText(restlt1);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String finalRestlt = restlt1;
                tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {//实例化自带语音对象
                    @Override
                    public void onInit(int i) {
                        if(i==TextToSpeech.SUCCESS){//设置语音
                            tts.setLanguage(Locale.CHINESE);//中文
                            tts.speak(finalRestlt    ,TextToSpeech.QUEUE_FLUSH,null);//播报
                        }
                    }
                });

            }

        });




    }
    public void init(){
        Btn_Req = findViewById(R.id.Btn_Requst);
        Edit_Req = findViewById(R.id.edit_input);
        Text_Show = findViewById(R.id.Text_show);

    }

    private static String utterance(String AccessToken,String input) {
        // 请求URL
        String talkUrl = "https://aip.baidubce.com/rpc/2.0/unit/service/v3/chat";
        try {
            // 请求参数
            String params1 = "{\"version\":\"3.0\",\"service_id\":\"S85712\",\"session_id\":\"\",\"log_id\":\"7758521\",\"request\":{\"terminal_id\":\"88888\",\"query\":\""+input+"\"}}";
            String accessToken = AccessToken;
            String result = HttpUtil.post(talkUrl, accessToken, "application/json", params1);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject result1 = jsonObject.optJSONObject("result");
            JSONObject context = result1.optJSONObject("context");
            JSONArray historyArray = context.optJSONArray("SYS_PRESUMED_HIST");
            String result2 = "";
            result2 = historyArray.getString(1);
            return result2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getAuth() {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        // 获取access_token的url参数
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getAccessTokenUrl).get().build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            String accessToken = jsonObject.getString("access_token");
            return accessToken;
        } catch (IOException | JSONException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }


}