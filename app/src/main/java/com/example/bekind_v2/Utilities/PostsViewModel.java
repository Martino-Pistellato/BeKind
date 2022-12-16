package com.example.bekind_v2.Utilities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;

import java.util.ArrayList;

public class PostsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<PostRepository.Post>> posts;

    public MutableLiveData<ArrayList<PostRepository.Post>> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<ArrayList<PostRepository.Post>>();
        }
        return posts;
    }
}
