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

import java.time.LocalDate;
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

        public Post() {}

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
        public void setBody(String body) {this.body = body;}
        public void setFilters(ArrayList<String> filters) {this.filters = filters;}
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


    public static void editPost(String documentId, String title, String body, ArrayList<String> filters, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("title", title, "body", body, "filters", filters).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void deletePost(String documentId){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).delete();
    }

    public static void deletePost(String documentId, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void clearPosts(){
        FirebaseFirestore.getInstance().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    Post post = snap.toObject(Post.class); //cast the result to a real proposal
                    LocalDateTime pubD = post.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime();
                    LocalDateTime expD = LocalDateTime.now().minusMonths(3);
                    if (pubD.isBefore(expD))
                        deletePost(post.getId());
                }
            }
        });
    }

    public static void getPosts(LocalDate day, String userID, ArrayList<String> filters, PostTypes type, MyCallback<ArrayList<Post>> myCallback){
        ArrayList<PostRepository.Post> res = new ArrayList<>();
        LocalDateTime start = (day == null) ? LocalDateTime.MIN : day.atTime(0,0,0), end = (day == null) ? LocalDateTime.MAX : day.atTime(23,59,59);
        CollectionReference db = FirebaseFirestore.getInstance().collection("Posts");
        Query postsQuery = null;
        switch (type){
            case MYPOSTS: postsQuery = db.whereEqualTo("publisherID", userID); break;
            case OTHERSPOSTS: postsQuery = db.whereNotEqualTo("publisherID", userID);break;
        }

        postsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snap : task.getResult()) { //for each element in the query
                        PostRepository.Post post = snap.toObject(PostRepository.Post.class);
                        LocalDateTime pubD = post.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime();
                        if (pubD.isAfter(start) && pubD.isBefore(end)){
                            if (filters != null && post.getFilters().containsAll(filters))
                                res.add(post); //adds the proposal to the Proposal to be shown
                        }
                    }
                    myCallback.onCallback(res);
                }

            }
        });
    }

    public static void getPost(String documentId, MyCallback<Post> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(Post.class));
            }
        });
    }
}
