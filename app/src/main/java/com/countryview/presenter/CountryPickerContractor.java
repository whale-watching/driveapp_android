package com.countryview.presenter;



import com.countryview.model.Country;

import java.util.List;

public interface CountryPickerContractor {

    interface View {

        void setCountries(List<Country> countries);
    }

    interface Presenter {


        void filterSearch(String query);

       // void sort(CountryPicker.Sort sort);
    }
}
