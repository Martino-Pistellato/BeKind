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
        private String neighbourhoodID;
        @ServerTimestamp
        private Date publishingDate;
        private ArrayList<String> filters;
        private ArrayList<String> usersFlag;
        private ArrayList<String> usersLike;

        public Post() {}

        public Post(String title, String body, String id, String publisherID,String neighbourhoodID, final Date publishingDate, ArrayList<String> filters) {
            this.title = title;
            this.body = body;
            this.id = id;
            this.publisherID = publisherID;
            this.publishingDate = publishingDate;
            this.filters = filters;
            this.neighbourhoodID = neighbourhoodID;
            this.usersFlag = new ArrayList<>();
            this.usersLike = new ArrayList<>();
        }

        public String getTitle() { return this.title;}
        public String getBody() { return this.body;}
        public String getId() { return this.id;}
        public String getPublisherID() { return this.publisherID;}
        public Date getPublishingDate() { return this.publishingDate;}
        public ArrayList<String> getFilters() { return filters; }
        public String getNeighbourhoodID() {return neighbourhoodID;}
        public ArrayList<String> getUsersFlag() { return usersFlag;}
        public ArrayList<String> getUsersLike() { return usersLike;}

        public void setTitle(String title) { this.title = title;}
        public void setBody(String body) {this.body = body;}
        public void setFilters(ArrayList<String> filters) {this.filters = filters;}
        public void setNeighbourhoodID(String neighbourhoodID) {this.neighbourhoodID = neighbourhoodID;}

        public void addUserFlag(String userId) {
            if(!usersFlag.contains(userId)) {
                usersFlag.add(userId);
            }
        }

        public void deleteUserFlag(String userId) {
            if(usersFlag.contains(userId)) {
                usersFlag.remove(userId);
            }
        }

        public boolean hasUserFlagged(String userId){
            return usersFlag.contains(userId);
        }

        public void addUserLike(String userId) {
            if(!usersLike.contains(userId)) {
                usersLike.add(userId);
            }
        }

        public void deleteUserLike(String userId) {
            if(usersLike.contains(userId)) {
                usersLike.remove(userId);
            }
        }

        public boolean hasUserLiked(String userId){
            return usersLike.contains(userId);
        }
    }

    public static void createPost(String title, String body, ArrayList<String> filters, MyCallback<Boolean> myCallback){
        String id =  UUID.randomUUID().toString();
        String publisherID = UserManager.getUserId();
        LocalDateTime ldt = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        UserManager.getUser(publisherID, user -> {
            if(user != null) {
                PostRepository.Post post = new PostRepository.Post(title, body, id, publisherID, user.getNeighbourhoodID(), date, filters);
                FirebaseFirestore.getInstance().collection("Posts").document(id).set(post).addOnCompleteListener((t)->{ myCallback.onCallback(t.isSuccessful()); });
            }
        });
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
                    UserManager.getUser(userID, user -> {
                        for (DocumentSnapshot snap : task.getResult()) { //for each element in the query
                            PostRepository.Post post = snap.toObject(PostRepository.Post.class);
                            LocalDateTime pubD = post.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime();
                            if (pubD.isAfter(start) && pubD.isBefore(end)){
                                if (filters != null && post.getFilters().containsAll(filters))
                                    if(user.getNeighbourhoodID().equals(post.getNeighbourhoodID()))
                                        if(post.getUsersFlag().size() < 5)
                                            res.add(post); //adds the proposal to the Proposal to be shown
                            }
                        }
                        myCallback.onCallback(res);
                    });
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

    public static void updatePostFlag(String documentId, ArrayList<String> usersFlag, MyCallback<Boolean> myCallback) {
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("usersFlag", usersFlag).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void updatePostLike(String documentId, ArrayList<String> usersLike, MyCallback<Boolean> myCallback) {
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("usersLike", usersLike).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void addUserFlag(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null) {
                    result.addUserFlag(userId);
                    updatePostFlag(documentId, result.getUsersFlag(), myCallback);
                }
            }
        });
    }

    public static void deleteUserFlag(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null) {
                    result.deleteUserFlag(userId);
                    updatePostFlag(documentId, result.getUsersFlag(), myCallback);
                }
            }
        });
    }

    public static void hasUserFlagged(String documentId, String userId, MyCallback<Boolean> myCallback){
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null){
                    myCallback.onCallback(result.hasUserFlagged(userId));
                }
            }
        });
    }

    public static void addUserLike(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null) {
                    result.addUserLike(userId);
                    updatePostLike(documentId, result.getUsersLike(), myCallback);
                }
            }
        });
    }

    public static void deleteUserLike(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null) {
                    result.deleteUserLike(userId);
                    updatePostLike(documentId, result.getUsersLike(), myCallback);
                }
            }
        });
    }

    public static void hasUserLiked(String documentId, String userId, MyCallback<Boolean> myCallback){
        getPost(documentId, new MyCallback<Post>() {
            @Override
            public void onCallback(Post result) {
                if(result != null){
                    myCallback.onCallback(result.hasUserLiked(userId));
                }
            }
        });
    }
}
