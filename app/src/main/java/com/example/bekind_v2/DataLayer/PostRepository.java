package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
        private boolean priority;

        //empty constructor, necessary
        public Post() {}

        //constructor with all fields
        public Post(String title, String body, String id, String publisherID,String neighbourhoodID, final Date publishingDate, ArrayList<String> filters, boolean priority) {
            this.title = title;
            this.body = body;
            this.id = id;
            this.publisherID = publisherID;
            this.publishingDate = publishingDate;
            this.filters = filters;
            this.neighbourhoodID = neighbourhoodID;
            this.usersFlag = new ArrayList<>();
            this.usersLike = new ArrayList<>();
            this.priority = priority;
        }

        //getters and setters
        public String getTitle() { return this.title;}
        public String getBody() { return this.body;}
        public String getId() { return this.id;}
        public String getPublisherID() { return this.publisherID;}
        public Date getPublishingDate() { return this.publishingDate;}
        public boolean getPriority() {return this.priority;}
        public ArrayList<String> getFilters() { return filters; }
        public String getNeighbourhoodID() {return neighbourhoodID;}
        public ArrayList<String> getUsersFlag() { return usersFlag;}
        public ArrayList<String> getUsersLike() { return usersLike;}

        public void setTitle(String title) { this.title = title;}
        public void setBody(String body) {this.body = body;}
        public void setFilters(ArrayList<String> filters) {this.filters = filters;}
        public void setNeighbourhoodID(String neighbourhoodID) {this.neighbourhoodID = neighbourhoodID;}
        public void setPriority(boolean priority){this.priority = priority;}

        //method used to add a user id to the arraylist of users that flagged this post
        public void addUserFlag(String userId) {
            if(!usersFlag.contains(userId)) {
                usersFlag.add(userId);
            }
        }
        //method used to remove a user id from the arraylist of users that flagged this post
        public void deleteUserFlag(String userId) {
            if(usersFlag.contains(userId)) {
                usersFlag.remove(userId);
            }
        }

        //method used to check if a certain user has flagged this post
        public boolean hasUserFlagged(String userId){
            return usersFlag.contains(userId);
        }

        //method used to add a user id to the arraylist of users that liked this post
        public void addUserLike(String userId) {
            if(!usersLike.contains(userId)) {
                usersLike.add(userId);
            }
        }

        //method used to remove a user id from the arraylist of users that liked this post
        public void deleteUserLike(String userId) {
            if(usersLike.contains(userId)) {
                usersLike.remove(userId);
            }
        }

        //method used to check if a certain user has liked this post
        public boolean hasUserLiked(String userId){
            return usersLike.contains(userId);
        }
    }

    //method used to create a post and add it to the database
    public static void createPost(String title, String body, ArrayList<String> filters, MyCallback<Boolean> myCallback){
        String id =  UUID.randomUUID().toString();
        String publisherID = UserManager.getUserId();
        LocalDateTime ldt = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        Date publishingDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        UserManager.getUser(publisherID, user -> {
            if(user != null) {
                PostRepository.Post post = new PostRepository.Post(title, body, id, publisherID, user.getNeighbourhoodID(), publishingDate, filters, Utilities.isOldAge(user.getBirth()));
                FirebaseFirestore.getInstance().collection("Posts").document(id).set(post).addOnCompleteListener((t)->{ myCallback.onCallback(t.isSuccessful()); });
            }
        });
    }

    //method used to edit a certain post
    public static void editPost(String documentId, String title, String body, ArrayList<String> filters, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("title", title, "body", body, "filters", filters).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    //method used to delete a certain post
    public static void deletePost(String documentId){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).delete();
    }

    //method used to delete a post, giving information about the success or failure of the task
    public static void deletePost(String documentId, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    //method used to delete posts from the database
    public static void clearPosts(){
        //we get all the posts in the database
        FirebaseFirestore.getInstance().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    Post post = snap.toObject(Post.class); //cast the result to a real post
                    LocalDateTime pubD = post.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime(); //we get the publishingDate
                    LocalDateTime expD = LocalDateTime.now().minusMonths(3); //we set a life span for a post equivalent to 3 months
                    if (pubD.isBefore(expD)) //if the post has been published more than 3 months ago, we delete it
                        deletePost(post.getId());
                }
            }
        });
    }

    //method use to collect the posts present in the database, depending on the page we are currently in
    public static void getPosts(LocalDate day, String userID, ArrayList<String> filters, PostTypes type, MyCallback<ArrayList<Post>> myCallback){
        //we create two arraylist, one for the posts with priority, one for the others
        ArrayList<Post> withPriority = new ArrayList<>(), withoutPriority = new ArrayList<>();

        //we set a min and max time, set to the very beginning (00:00:00) and very ending (23:59:59) of the given day in input. If day is null, we use today
        LocalDateTime start = (day == null) ? LocalDateTime.MIN : day.atTime(0,0,0), end = (day == null) ? LocalDateTime.MAX : day.atTime(23,59,59);

        CollectionReference db = FirebaseFirestore.getInstance().collection("Posts");
        Query postsQuery = null;
        //depending on which page we are currently in, we collect the user's posts or the other's posts. In both cases, posts are ordered chronologically by ascending publishingDate
        //TODO should we order posts by publishingDate?
        switch (type){
            case MYPOSTS: postsQuery = db.whereEqualTo("publisherID", userID); break; //we are in profile page
            case OTHERSPOSTS: postsQuery = db.whereNotEqualTo("publisherID", userID);break; //we are in dashboard page
        }

        postsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    UserManager.getUser(userID, user -> {
                        for (DocumentSnapshot snap : task.getResult()) { //for each element in the query
                            PostRepository.Post post = snap.toObject(PostRepository.Post.class); //we cast the element to a real post
                            LocalDateTime pubD = post.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime(); //we get the post publishingDate
                            if (pubD.isAfter(start) && pubD.isBefore(end)){ //we show only the posts published on the selected day
                                if (filters != null && post.getFilters().containsAll(filters)) //that are associated with the selected filters, if any
                                    if(user.getNeighbourhoodID().equals(post.getNeighbourhoodID())) //associated with the same neighbourhood of the current user
                                        if(post.getUsersFlag().size() < 5) // and not flagged by more than 4 users
                                            if(post.getPriority()) withPriority.add(post); //if it has priority, we add it to the first arraylist
                                            else withoutPriority.add(post); //else to the other
                            }
                        }
                        withPriority.addAll(withoutPriority); //we merge the two arraylists, in this way the ones with priority will be shown on top of the others
                        myCallback.onCallback(withPriority);
                    });
                }
            }
        });
    }

    //method used to get a certain post
    public static void getPost(String documentId, MyCallback<Post> myCallback){
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(Post.class));
            }
        });
    }

    //method used to update the arraylist of users that flagged a certain post, in the database
    public static void updatePostFlag(String documentId, ArrayList<String> usersFlag, MyCallback<Boolean> myCallback) {
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("usersFlag", usersFlag).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    //method used to update the arraylist of users that liked a certain post, in the database
    public static void updatePostLike(String documentId, ArrayList<String> usersLike, MyCallback<Boolean> myCallback) {
        FirebaseFirestore.getInstance().collection("Posts").document(documentId).update("usersLike", usersLike).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    //method used to add a user to the users that flagged a certain post
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

    //method used to remove a user from the users that flagged a certain post
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

    //method used to check if a user has flagged a certain post
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

    //method used to add a user to the users that liked a certain post
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

    //method used to remove a user from the users that liked a certain post
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

    //method used to check if a user has liked a certain post
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
