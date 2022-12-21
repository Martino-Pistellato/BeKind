package com.example.bekind_v2.Utilities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;

import java.util.ArrayList;

public class PostsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<PostRepository.Post>> myPosts;
    private MutableLiveData<ArrayList<PostRepository.Post>> otherPosts;

    public MutableLiveData<ArrayList<PostRepository.Post>> getMyPosts() {
        if (myPosts == null) {
            myPosts = new MutableLiveData<ArrayList<PostRepository.Post>>();
        }
        return myPosts;
    }

    public MutableLiveData<ArrayList<PostRepository.Post>> getOtherPosts() {
        if (otherPosts == null) {
            otherPosts = new MutableLiveData<ArrayList<PostRepository.Post>>();
        }
        return otherPosts;
    }
}
