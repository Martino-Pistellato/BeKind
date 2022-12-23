package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PostRepository {
    public static class Post {
        private String title;
        private String body;
        private String id;
        private String publisherID;
        @ServerTimestamp
        private Date publishingDate;
        private ArrayList<String> filters;

        public Post() {
        }

        public Post(String title, String body, String id, String publisherID, Date publishingDate, ArrayList<String> filters) {
            this.title = title;
            this.body = body;
            this.id = id;
            this.publisherID = publisherID;
            this.publishingDate = publishingDate;
            this.filters = filters;
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

        public String getPublisherID() {
            return this.publisherID;
        }

        public Date getPublishingDate() {
            return this.publishingDate;
        }

        public ArrayList<String> getFilters() { return filters; }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public void setPublishingDate(Date publishingDate) {
            this.publishingDate = publishingDate;
        }

        public void setFilters(ArrayList<String> filters) {
            this.filters = filters;
        }
    }
    public static void createPost(String title, String body, ArrayList<String> filters){
        String id =  UUID.randomUUID().toString();
        String publisherID = UserManager.getUserId();
        Date date = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        PostRepository.Post post = new PostRepository.Post(title, body, id, publisherID, date, filters);

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

    public static void getPosts(String userdID, ArrayList<String> filters, PostTypes type, MyCallback<ArrayList<Post>> myCallback){
        ArrayList<PostRepository.Post> res = new ArrayList<>();
        CollectionReference db = FirebaseFirestore.getInstance().collection("Posts");
        Query postsQuery = null;
        switch (type){
            case MYPOSTS: postsQuery = db.whereEqualTo("publisherID", userdID); break;
            case OTHERSPOSTS: postsQuery = db.whereNotEqualTo("publisherID", userdID);break;
        }

        postsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snap : task.getResult()) { //for each element in the query
                        PostRepository.Post post = snap.toObject(PostRepository.Post.class);
                        if (filters != null && post.getFilters().containsAll(filters))
                            res.add(post); //adds the proposal to the Proposal to be shown
                    }
                    myCallback.onCallback(res);
                }

            }
        });
    }
}
