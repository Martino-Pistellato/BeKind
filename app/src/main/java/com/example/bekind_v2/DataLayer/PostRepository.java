package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PostRepository {
    public static class Post {
        private String title;
        private String body;
        private String id;

        public Post() {
        }

        public Post(String title, String body, String id) {
            this.title = title;
            this.body = body;
            this.id = id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getBody() {
            return this.body;
        }

        public String getId() {
            return this.id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
    public static void createPost(String title, String body){
        String id =  UUID.randomUUID().toString();
        PostRepository.Post post = new PostRepository.Post(title, body, id);

        FirebaseFirestore.getInstance().collection("Posts").document(id).set(post); //TODO: make it asynchronous?
    }

    public static void updatePost(String id, String title, String body){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection("Posts").document(id);

        if(!title.isEmpty())
            doc.update("title", title);
        if(!body.isEmpty())
            doc.update("body", body);
    }

    public static void getPosts(MyCallback<ArrayList<Post>> myCallback){
        ArrayList<PostRepository.Post> res = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snap : task.getResult()) { //for each element in the query
                        PostRepository.Post post = snap.toObject(PostRepository.Post.class);
                        res.add(post);
                    }
                    myCallback.onCallback(res);
                }

            }
        });
    }
}
