package com.example.mohammadkurdia.testweatherapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mohammadkurdia.testweatherapp.data.model.OWResponse;
import com.example.mohammadkurdia.testweatherapp.data.remote.OWService;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Details extends AppCompatActivity {
    //Defining Variables and Views needed to display the Data
    String cityName;
    String maxTemp;
    String minTemp;
    String weatherType;
    String pressure;
    String seaLevel;
    WeatherAdapter weatherAdapter;
    DatabaseHelper databaseHelper;
    ArrayList<String> citiesList;
    @BindView(R.id.btnDeleteFavorite)
    Button btnDeleteFavorite;
    @BindView(R.id.btnFavorite)
    Button btnFavorite;
    @BindView(R.id.tvCityName) TextView tvCityName;
    public ArrayList<WeatherReport> weatherReportArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //**********
        ButterKnife.bind(this);
        //Getting the city name from the previous activity
        Bundle extras = getIntent().getExtras();
        cityName = extras.getString("cityName");
        //to add Cities to the Database
        databaseHelper = new DatabaseHelper(this);
        //Updating the State of the Add to Favorites Button
        citiesList = databaseHelper.getAllCitiesList();
        for(int i = 0; i < citiesList.size();i++){
            if(citiesList.get(i).equals(cityName)){
            btnFavorite.setVisibility(View.GONE);
            btnDeleteFavorite.setVisibility(View.VISIBLE);
        }
        }
        //Using the Api Interface to get the Json Data based on the City Name
        final OWService owService = OWService.retrofit.create(OWService.class);
        Call<OWResponse> call = owService.getResponse(cityName, "05463fa07d1695e52e81d369c0f193f0");
        // interpretResponse(owService);
        call.enqueue(new Callback<OWResponse>() {
            @Override
            public void onResponse(Call<OWResponse> call, Response<OWResponse> response) {
                //When getting the response successfully, we parse the data into a report for each data set
                if(response.isSuccessful()){
                    for (int i =0;i<16;i++){
                        maxTemp = String.valueOf(response.body().getList().get(i).getMain().getTempMax())+ " C°";
                        minTemp =  String.valueOf(response.body().getList().get(i).getMain().getTempMin())+ " C°";
                        weatherType = response.body().getList().get(i).getWeather().get(0).getMain();
                        pressure = String.valueOf(response.body().getList().get(i).getMain().getPressure());
                        seaLevel = String.valueOf(response.body().getList().get(i).getMain().getSeaLevel());
                        WeatherReport report =  new WeatherReport(cityName,maxTemp,minTemp,weatherType,pressure,seaLevel);
                        weatherReportArrayList.add(report);
                    }
                    //the Title Textview
                    tvCityName.setText(cityName);
                    //Showing the Weather data in a recycler view
                    weatherAdapter = new WeatherAdapter(weatherReportArrayList);
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.weatherDetails);
                    recyclerView.setAdapter(weatherAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(layoutManager);
                    weatherAdapter.notifyDataSetChanged();
                }else{
                    int logError = response.code();
                    Log.d("Response Error",String.valueOf(logError));
                }
            }
            @Override
            public void onFailure(Call<OWResponse> call, Throwable t) {
                Log.d("Response Error",t.getMessage());
            }
        });
    }
//Click listener to add Favorite city to the SQlite Database
        public void btnSaveFavorite(View v){
            databaseHelper.addFavoriteCity(cityName);
            Toast.makeText(this, cityName + " City Saved", Toast.LENGTH_SHORT).show();
            v.setVisibility(View.GONE);
            btnDeleteFavorite.setVisibility(View.VISIBLE);
    }
    //Click listener to delete Favorite city from the SQlite Database
    public void btnDeleteFavorite(View v){
        databaseHelper.deleteFavoriteCity(cityName);
        Toast.makeText(this, cityName + " City Deleted", Toast.LENGTH_SHORT).show();
        v.setVisibility(View.GONE);
        btnFavorite.setVisibility(View.VISIBLE);
    }

//Recycler View Adapter
    public  class WeatherAdapter extends  RecyclerView.Adapter<WeatherDetails>{
        public ArrayList<WeatherReport>  mDailyWeatherReport;
        public WeatherAdapter(ArrayList<WeatherReport> dailyWeatherReport) {
            mDailyWeatherReport = dailyWeatherReport ;
        }

        @Override
        public WeatherDetails onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather_day,parent,false);
            return new WeatherDetails(card);
        }

        @Override
        public void onBindViewHolder(WeatherDetails holder, int position) {
            WeatherReport report = mDailyWeatherReport.get(position);
            holder.updateUI(report);
        }

        @Override
        public int getItemCount() {
            return     mDailyWeatherReport.size();
        }
    }

//Recycler View ViewHolder
    public class WeatherDetails extends RecyclerView.ViewHolder{

        @BindView (R.id.tvDayTemp)  TextView tvDayTemp;
        @BindView(R.id.tvNightTemp) TextView tvNightTemp;
        @BindView(R.id.tvPressure) TextView tvPressure;
        @BindView(R.id.tvSeaLevel) TextView tvSeaLevel;
        @BindView(R.id.iconWeather)  ImageView imgIcon;
        @BindView(R.id.tvWeatherDescription) TextView tvWeatherDescription;


        public WeatherDetails(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        //to Update the UI with the Correct info for Each element and to Display the correct icon
        public void updateUI(WeatherReport report) {
            tvDayTemp.setText("Max "+report.getMaxTemp());
            tvNightTemp.setText("Min "+report.getMinTemp());
            tvPressure.setText("Pressure "+report.getPressure());
            tvSeaLevel.setText("Sea Level "+report.getSeaLevel());
            tvWeatherDescription.setText(report.getWeatherType());

            //Choosing the Icon to show in the Image View
            if(report.getWeatherType().equals("Clouds") ){
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
            }else if(report.getWeatherType().equals("Snow")){
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
            } else if (report.getWeatherType().equals("Rain") ) {
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
            }else  if (report.getWeatherType().equals("Wind")){
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.partially_cloudy));
            }else{
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            }
        }
    }
}



