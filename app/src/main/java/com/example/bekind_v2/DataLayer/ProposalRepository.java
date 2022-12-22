package com.example.bekind_v2.DataLayer;

import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.Types;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ProposalRepository {
    public static class Proposal {
        private String title;
        private String body;
        private String id;
        private String publisherId; //we could save the ID of the publisher
        private String accepterId; //and the ID of the accepter
        @ServerTimestamp
        private Date expiringDate; //must be a date, when stored to database is mapped to a firestore timestamp and viceversa when reading
        private ArrayList<String> filters;

        public Proposal(){}

        public Proposal(String title, String body, Date expiringDate, String publisherId, String accepterId, String id, ArrayList<String> filters){
            this.title = title;
            this.body = body;
            this.expiringDate = expiringDate;
            this.publisherId = publisherId;
            this.accepterId = accepterId;
            this.id = id;
            this.filters = filters;
        }

        public String getTitle(){return this.title;}
        public String getBody(){return this.body;}
        public Date getExpiringDate(){return this.expiringDate;}
        public String getPublisherID(){return this.publisherId;}
        public String getAccepterID(){return this.accepterId;}
        public String getId(){return this.id;}
        public ArrayList<String> getFilters(){return this.filters;}

        public void setTitle(String title){this.title=title;}
        public void setBody(String body){this.body=body;}
        public void setExpiringDate(Date expiringDate){this.expiringDate=expiringDate;}
        public void setPublisherID(String publisherID){this.publisherId=publisherID;}
        public void setAccepterID(String accepterID){this.accepterId=accepterID;}
        public void setFilters(ArrayList<String> filters){this.filters=filters;}
    }

    public static void createProposal(String title, String body, Date expiringDate, String publisherID, String accepterID, ArrayList<String> filters){
        String id =  UUID.randomUUID().toString();
        Proposal proposal = new Proposal(title, body, expiringDate, publisherID, accepterID, id, filters);

        FirebaseFirestore.getInstance().collection("Proposals").document(id).set(proposal); //TODO: make it asynchronous?
    }


    public static void getProposals(LocalDate day, String userId, ArrayList<String> filters, Types type, MyCallback<ArrayList<Proposal>> myCallback){
        ArrayList<Proposal> res = new ArrayList<>();
        LocalDateTime start = (day == null) ? LocalDateTime.MIN : day.atTime(0,0,0), end = (day == null) ? LocalDateTime.MAX : day.atTime(23,59,59);
        Log.e("DAYS SET", "start : " + start.toString() + " end : " + end.toString());
        CollectionReference db = FirebaseFirestore.getInstance().collection("Proposals");
        Query activity_query = null;

        switch(type){
            case PROPOSED: activity_query = db.whereEqualTo("publisherID", userId).orderBy("expiringDate"); break;
            case ACCEPTED: activity_query = db.whereEqualTo("accepterID", userId).orderBy("expiringDate"); break;
            case AVAILABLE: activity_query = db.whereNotEqualTo("publisherID", userId).whereEqualTo("accepterID", null).orderBy("publisherID", Query.Direction.ASCENDING).orderBy("expiringDate", Query.Direction.ASCENDING); break;
        }

        activity_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot snap :  task.getResult()) { //for each element in the query
                        Proposal prop = snap.toObject(Proposal.class); //cast the result to a real proposal
                        LocalDateTime expD = prop.getExpiringDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime(); //gets the expiring date of the proposal
                        if (expD.isAfter(start) && expD.isBefore(end)) {//if the proposal expiring date is between the limits
                            if (filters != null && prop.getFilters().containsAll(filters))
                                res.add(prop); //adds the proposal to the Proposal to be shown
                        }
                    }
                    myCallback.onCallback(res);
                }
            }
        });
    }

    public static void getProposal(String documentId, MyCallback<Proposal> myCallback){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(Proposal.class));
            }
        });
    }

    public static void acceptProposal(String documentId, String userId, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).update("accepterID", userId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void rejectProposal(String documentId, MyCallback<Boolean> myCallback){
        Log.e("QUERY", "sono nella query");
        FirebaseFirestore.getInstance().collection("Proposals"). document(documentId).update("accepterID", null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("SUCCESS", "task successful: " + task.isSuccessful());
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void editProposal(String documentId, String title, String body, Date expiringDate, ArrayList<String> filters, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).update("title", title, "body", body, "expiringDate", expiringDate, "filters", filters).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }
    
    public static void deleteProposal(String documentId){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).delete();
    }
    
    public static void clearProposals(){
        FirebaseFirestore.getInstance().collection("Proposals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    Proposal prop = snap.toObject(Proposal.class); //cast the result to a real proposal
                    LocalDateTime expD = prop.getExpiringDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime();
                    if (!expD.isAfter(LocalDateTime.now()))
                        deleteProposal(prop.getId());
                }
            }
        });
    }
}
