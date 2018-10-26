package com.example.mohammadkurdia.testweatherapp;


import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.example.mohammadkurdia.testweatherapp.data.model.OWResponse;
import com.example.mohammadkurdia.testweatherapp.data.remote.OWService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Defining Variables and Views used in Main Activity
    String cityName ;
    @BindView(R.id.lvFavoriteCities) ListView lvFavoriteCities;
    @BindView(R.id.tvFavoriteCities) TextView tvFavoriteCities;
   @BindView(R.id.imageView2)
    ImageView imageView2;
    ArrayAdapter<String> citiesAdapter;
    ArrayList<String> citiesList;
    DatabaseHelper databaseHelper;
    String maxTemp;
    String minTemp;
    String weatherType;
    String pressure;
    String seaLevel;
    @BindView(R.id.tvCityName) TextView tvCityName;
    @BindView(R.id.tvWeather) TextView tvWeather;
    @BindView(R.id.tvJoke) TextView tvJoke;
    WeatherReport report;
    double maxTempDouble;

    Random r = new Random();
    String[] sunnyJokes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //Binding Butter Knife with this Activity
        ButterKnife.bind(this);

        //Creating the SQLite Database
        databaseHelper= new DatabaseHelper(this);

        updateWeather("Amman",imageView2);

        Resources res = getResources();

      sunnyJokes  = res.getStringArray(R.array.Sunny_Jokes);


        //Using Google Places Autocomplete to search Cities around the World
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                cityName = place.getName().toString();
                //When a place is selected, the city name is stored as extra and sent to Details Activity
                updateWeather(cityName,imageView2);
            }

            @Override
            public void onError(Status status) {
                //A code to handle any error could be used here
            }
        });
        //Calling a function to refresh the list that contains all the favorite cities
        refreshCityList();
    }

    //
    @Override
    public void onRestart() {

        super.onRestart();  // Always call the superclass method first
//Calling a function to refresh the list that contains all the favorite cities
        refreshCityList();
    }



    public void refreshCityList(){
        //to display the items in the SQLite database into a List View
        citiesList = databaseHelper.getAllCitiesList();
        if(citiesList.size()==0){
            //To remove the word, Favorites Cities, if there is no favorite cities
             tvFavoriteCities.setVisibility(View.GONE);
             lvFavoriteCities.setVisibility(View.GONE);
        }else{
            tvFavoriteCities.setVisibility(View.VISIBLE);
            lvFavoriteCities.setVisibility(View.VISIBLE);
            citiesAdapter = new ArrayAdapter<String>(this, R.layout.favorite_city, citiesList);
            lvFavoriteCities.setAdapter(citiesAdapter);
            lvFavoriteCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //on clicking on any item,, city name is sent to Details Ctivity
                    cityName = citiesList.get(position);
                    updateWeather(cityName,imageView2);
                }
            });
        }
    }

public void btnDetailsFunc(View view){
        if (cityName != null){
            Intent i = new Intent(getApplicationContext(), Details.class);
            i.putExtra("cityName", cityName);
            startActivity(i);
        }
}

public void updateWeather(final String myCity, final ImageView imgIcon){
    tvCityName.setText(myCity);
   final int i1 = r.nextInt(4);
    final OWService owService = OWService.retrofit.create(OWService.class);
    Call<OWResponse> call = owService.getResponse(myCity, "05463fa07d1695e52e81d369c0f193f0");
    // interpretResponse(owService);
    call.enqueue(new Callback<OWResponse>() {
        @Override
        public void onResponse(Call<OWResponse> call, Response<OWResponse> response) {
            //When getting the response successfully, we parse the data into a report for each data set
            if (response.isSuccessful()) {
                for (int i = 0; i < 2; i++) {
                    maxTempDouble=Math.round(response.body().getList().get(i).getMain().getTempMax());
                    maxTemp = String.valueOf(maxTempDouble)+ " C°";
                    minTemp = String.valueOf(response.body().getList().get(i).getMain().getTempMin()) + " C°";
                    weatherType = response.body().getList().get(i).getWeather().get(0).getMain();
                    pressure = String.valueOf(response.body().getList().get(i).getMain().getPressure());
                    seaLevel = String.valueOf(response.body().getList().get(i).getMain().getSeaLevel());
                   report = new WeatherReport(myCity, maxTemp, minTemp, weatherType, pressure, seaLevel);
                }
                if(report.getWeatherType().equals("Clouds") ){
                    imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                }else if(report.getWeatherType().equals("Snow")){
                    imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                } else if (report.getWeatherType().equals("Rain") ) {
                    imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                }else  if (report.getWeatherType().equals("Wind")){
                    imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.partially_cloudy));
                }else{
                    if(maxTempDouble>30) {
                        imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                        tvJoke.setText(sunnyJokes[i1]);
                    }else {
                        imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.clear));
                        tvJoke.setText("clear");
                    }
                }
                tvWeather.setText(maxTemp);
                //the Title Textview
            } else {
                int logError = response.code();
                Log.d("Response Error", String.valueOf(logError));
            }
        }

        @Override
        public void onFailure(Call<OWResponse> call, Throwable t) {
            Log.d("Response Error", t.getMessage());
        }
    });


}
}


