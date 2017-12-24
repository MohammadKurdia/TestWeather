package com.example.mohammadkurdia.testweatherapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;


import com.example.mohammadkurdia.testweatherapp.data.model.OWResponse;
import com.example.mohammadkurdia.testweatherapp.data.remote.OWService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Defining Variables and Views used in Main Activity
    String cityName ;
    @BindView(R.id.lvFavoriteCities) ListView lvFavoriteCities;
    @BindView(R.id.tvFavoriteCities) TextView tvFavoriteCities;
    ArrayAdapter<String> citiesAdapter;
    ArrayList<String> citiesList;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //Binding Butter Knife with this Activity
        ButterKnife.bind(this);

        //Creating the SQLite Database
        databaseHelper= new DatabaseHelper(this);

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
                //When a place is selected, the city name is stored as extra and sent to Details Activity
                cityName = place.getName().toString();
                Intent i = new Intent(getApplicationContext(), Details.class);
                i.putExtra("cityName", cityName);
                startActivity(i);
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
                    Intent i = new Intent(getApplicationContext(), Details.class);
                    i.putExtra("cityName", cityName);
                    startActivity(i);
                }
            });
        }
    }
}


