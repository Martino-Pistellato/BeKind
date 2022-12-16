package com.example.bekind_v2.Utilities;

import java.time.LocalDate;
import java.util.ArrayList;

public class Utilities {
    public static class SharedViewModel{
        public static ProposalsViewModel proposalsViewModel;
        public static LocalDate day;
        public static ArrayList<String> filters;
    }
    public static void manageFilter(String filter, ArrayList<String> filters){
        if(filters.contains(filter))
            filters.remove(filter);
        else
            filters.add(filter);
    }
}
