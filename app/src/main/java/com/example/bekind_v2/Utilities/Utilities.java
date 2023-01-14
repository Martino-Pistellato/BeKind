package com.example.bekind_v2.Utilities;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class Utilities {
    public static LocalDate day = LocalDate.now();

    public static class SharedViewModel{
        public static ProposalsViewModel proposalsViewModel;
        public static PostsViewModel postsViewModel;
        public static MapViewModel mapViewModel;
    }

    public static class BetterCalendar {
        public Calendar c;

        public BetterCalendar() {
            this.c = java.util.Calendar.getInstance();
        }
        public BetterCalendar(Date date) {
            this.c = java.util.Calendar.getInstance();
            setDate(date);
        }

        public void setDate(Date date) {
            this.c.setTime(date);
        }

        public int getMonth() {
            return this.c.get(Calendar.MONTH);
        }
        public int getDay() {
            return this.c.get(Calendar.DAY_OF_MONTH);
        }
        public int getYear() {
            return this.c.get(Calendar.YEAR);
        }
        public int getHour(){
            return this.c.get(Calendar.HOUR);
        }
        public int getMinute(){
            return this.c.get(Calendar.MINUTE);
        }
    }

    public static void manageFilter(String filter, ArrayList<String> filters){
        if(filters.contains(filter))
            filters.remove(filter);
        else
            filters.add(filter);
    }

    public static void getProposals(LocalDate day, String userId, ArrayList<String> filters, Types type){
        ProposalRepository.getProposals(day, userId, filters, type, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                switch(type){
                    case ACCEPTED: SharedViewModel.proposalsViewModel.getAccepted().setValue(result); break;
                    case AVAILABLE: SharedViewModel.proposalsViewModel.getAvailable().setValue(result); break;
                    case PROPOSED: SharedViewModel.proposalsViewModel.getProposed().setValue(result); break;
                }
            }
        });
    }
    
    public static void getPosts(LocalDate day, String userId, ArrayList<String> filters, PostTypes type){
        PostRepository.getPosts(day, userId, filters, type, new MyCallback<ArrayList<PostRepository.Post>>() {
            @Override
            public void onCallback(ArrayList<PostRepository.Post> result) {
                switch(type){
                    case MYPOSTS: SharedViewModel.postsViewModel.getMyPosts().setValue(result); break;
                    case OTHERSPOSTS: SharedViewModel.postsViewModel.getOtherPosts().setValue(result); break;
                }
            }
        });
    }

    //the following method returns true when there are at least 60 years between the year of the input Date object and the year of today
    public static boolean isOldAge(Date birth){
        return (LocalDate.now()).getYear() - (new BetterCalendar(birth)).getYear() >= 60;
    }

    public static String convertToProperForm(String s) {
        String newForm = "";
        StringTokenizer st2 = new StringTokenizer(s, " ");

        if(st2.hasMoreElements()){
            String piece = (String)st2.nextElement();
            piece = piece.substring(0,1).toUpperCase() + piece.substring(1).toLowerCase();
            newForm+=piece;
        }
        while (st2.hasMoreElements()){
            String piece = (String)st2.nextElement();
            piece = piece.substring(0,1).toUpperCase() + piece.substring(1).toLowerCase();
            newForm += " " + piece;
        }
        return newForm;
    }
}
