package com.countryview.presenter;

import com.countryview.model.Country;
import com.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountryPickerPresenter implements CountryPickerContractor.Presenter {
    private CountryPickerContractor.View mView;
    private List<Country> mCountries;

    public CountryPickerPresenter(List<Country> mCountries, CountryPickerContractor.View view) {

        this.mCountries = mCountries;
        mView = view;
    }


    private List<Country> exceptCountriesByName(List<Country> countries, List<String> exceptCountriesName) {

        for (String countryName : exceptCountriesName) {
            for (Country country : countries) {
                if (country.getName().toLowerCase().equals(countryName.toLowerCase())) {
                    countries.remove(country);
                    break;
                }
            }
        }
        return countries;
    }

    Country lastHeader;

    @Override
    public void filterSearch(String query) {
        if (mCountries != null) {

            List<Country> filteredCountries = new ArrayList<>();
            for (Country country : mCountries) {
                if (capitalize(country.getName()).startsWith(capitalize(query))) {
                    if (query.length() == 1 && country.getName().equalsIgnoreCase(query)) {
                        lastHeader = country;
                    }

                    if (!filteredCountries.contains(lastHeader)) {
                        filteredCountries.add(lastHeader);

                    }
                    if (lastHeader != country) {
                        filteredCountries.add(country);
                    }
                }


                if (country.getDialCode().toLowerCase().startsWith(query.toLowerCase())) {


                        //lastHeader = getHeader(country.getName().charAt(0));
                        lastHeader = country.getSetHeaderSection();
                        if (lastHeader != null && !filteredCountries.contains(lastHeader)) {
                            filteredCountries.add(lastHeader);
                        }


                    if (lastHeader != country) {
                        filteredCountries.add(country);
                    }
                }
            }
            mView.setCountries(filteredCountries);
        }
    }

    public Country getHeader(char headerTxt) {
        Country mCountry = null;

        Logger.d("getHeader", "::" + headerTxt);
        for (Country country : mCountries) {
            if (capitalize(country.getName()).equalsIgnoreCase(capitalize(headerTxt + ""))) {
                Logger.d("getHeader", "::111111111111::" + headerTxt + ":::::" + country.getName());
                mCountry = country;
                return country;
            }
        }

        return mCountry;
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

//    @Override
//    public void sort(CountryPicker.Sort sort) {
//
//        switch (sort) {
//            case CODE: {
//                Comparator<Country> comparator = (country1, country2) -> country1.getCode().compareTo(country2.getCode());
//                Collections.sort(mCountries, comparator);
//                break;
//            }
//
//            case COUNTRY: {
//                Comparator<Country> comparator = (country1, country2) -> country1.getName().compareTo(country2.getName());
//                Collections.sort(mCountries, comparator);
//                break;
//            }
//
//            case DIALCODE: {
//                Comparator<Country> comparator = (o1, o2) -> o1.getDialCode().compareTo(o2.getDialCode());
//                Collections.sort(mCountries, comparator);
//                break;
//            }
//        }
//    }
}