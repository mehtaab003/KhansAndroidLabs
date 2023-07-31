package algonquin.cst2335.khan0498;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import algonquin.cst2335.khan0498.databinding.ActivityMainBinding;

/** Main Activity first screen of the Application
 * @author Mohammad Mehtaab Khan
 * @version 0.1
 */
public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    private String cityName;
    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.forecastButton.setOnClickListener(view -> {
            cityName = binding.cityTextField.getText().toString();

            String url = null;
            try {
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(cityName, "UTF-8") + "&appid=9f46f0fd4d10d67e4f006c30274a790e&units=metric";
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONObject coord = response.getJSONObject("coord");
                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject position0 = weatherArray.getJSONObject(0);
                            String description = position0.getString("description");
                            String iconName = position0.getString("icon");
                            int vis = response.getInt("visibility");
                            String name = response.getString("name");
                            JSONObject mainObject = response.getJSONObject("main");
                            double current = mainObject.getDouble("temp");
                            double min = mainObject.getDouble("temp_min");
                            double max = mainObject.getDouble("temp_max");
                            int humidity = mainObject.getInt("humidity");

                            String pathname = getFilesDir()+"/"+iconName+".png";
                            File file = new File(pathname);
                            if (file.exists()){
                                image = BitmapFactory.decodeFile(pathname);
                            } else {
                                ImageRequest imgReq = new ImageRequest("https://openweathermap.org/img/w/" + iconName + ".png", new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        try {
                                            image = response;
                                            image.compress(Bitmap.CompressFormat.PNG,100,MainActivity.this.openFileOutput(iconName+".png", Activity.MODE_PRIVATE));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },1024,1024,ImageView.ScaleType.CENTER,null,(error) -> { });
                                queue.add(imgReq);
                            }

                            /*String imageUrl = "https://openweathermap.org/img/w/" + iconName + ".png";
                            ImageRequest imgReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    FileOutputStream fOut = null;
                                    try {
                                        fOut = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);

                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                        binding.icon.setImageBitmap(bitmap);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },1024,1024,ImageView.ScaleType.CENTER,null,(error) -> {

                            });*/
                            runOnUiThread(( )->{
                                binding.temp.setText("The current temperature is "+current);
                                binding.temp.setVisibility(View.VISIBLE);
                                binding.maxTemp.setText("The max temperature is "+max);
                                binding.maxTemp.setVisibility(View.VISIBLE);
                                binding.minTemp.setText("The min temperature is "+min);
                                binding.minTemp.setVisibility(View.VISIBLE);
                                binding.humidity.setText("The humidity is "+humidity);
                                binding.humidity.setVisibility(View.VISIBLE);
                                binding.icon.setImageBitmap(image);
                                binding.icon.setVisibility(View.VISIBLE);
                                binding.description.setText(description);
                                binding.description.setVisibility(View.VISIBLE);
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                    });

            queue.add(request);
        });
    }
   }