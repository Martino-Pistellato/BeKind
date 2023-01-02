package com.example.bekind_v2.DataLayer;

import static com.example.bekind_v2.Utilities.Utilities.day;
import static java.lang.Thread.sleep;

import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.Pair;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.RepublishTypes;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class ProposalRepository {
    public static class Proposal {
        private String title;
        private String body;
        private String id;
        private String publisherID; //we could save the ID of the publisher
        private ArrayList<String> acceptersID; //and the ID of the accepter
        private int maxParticipants;
        private RepublishTypes republishTypes;
        @ServerTimestamp
        private Date publishingDate;
        @ServerTimestamp
        private Date expiringDate; //must be a date, when stored to database is mapped to a firestore timestamp and viceversa when reading
        private ArrayList<String> filters;
        private String neighbourhoodID;
        private ArrayList<String> flagsUsers;

        public Proposal(){}

        public Proposal(String title, String body, Date expiringDate, Date publishingDate, String publisherId, String neighbourhoodID, String id, int maxParticipants, RepublishTypes choice, ArrayList<String> filters){
            this.title = title;
            this.body = body;
            this.expiringDate = expiringDate;
            this.publisherID = publisherId;
            this.neighbourhoodID = neighbourhoodID;
            this.acceptersID = new ArrayList<>();
            this.id = id;
            this.maxParticipants = maxParticipants;
            this.filters = filters;
            this.flagsUsers = new ArrayList<>();
            this.publishingDate = publishingDate;
            this.republishTypes = choice;
        }

        public String getTitle(){return this.title;}
        public String getBody(){return this.body;}
        public Date getExpiringDate(){return this.expiringDate;}

        public Date getPublishingDate() {
            return publishingDate;
        }

        public String getPublisherID() {
            return this.publisherID;
        }
        public ArrayList<String> getAcceptersID(){return this.acceptersID;}
        public String getId(){return this.id;}
        public int getMaxParticipants() {return this.maxParticipants;}
        public ArrayList<String> getFilters(){return this.filters;}
        public String getNeighbourhoodID(){return this.neighbourhoodID;}
        public ArrayList<String> getFlagsUsers() {return flagsUsers;}

        public RepublishTypes getRepublishTypes() {
            return republishTypes;
        }

        public void setTitle(String title){this.title=title;}
        public void setBody(String body){this.body=body;}
        public void setExpiringDate(Date expiringDate){this.expiringDate=expiringDate;}
        public void setAcceptersID(ArrayList<String> acceptersID){this.acceptersID=acceptersID;}
        public void setMaxParticipants(int maxParticipants) {this.maxParticipants = maxParticipants;}
        public void setNeighbourhoodID(String neighbourhoodID) {this.neighbourhoodID = neighbourhoodID;}
        public void setFilters(ArrayList<String> filters){this.filters=filters;}

        public void setRepublishTypes(RepublishTypes republishTypes) {
            this.republishTypes = republishTypes;
        }

        public void addParticipant(String participantId){
            this.acceptersID.add(participantId);
        }
        public void removeParticipant(String participantId){
            this.acceptersID.remove(participantId);
        }

        public void addFlagUser(String userId) {
            if(!flagsUsers.contains(userId)) {
                flagsUsers.add(userId);
            }
        }

        public void deleteFlagUser(String userId){
            if(flagsUsers.contains(userId))
                flagsUsers.remove(userId);
        }

        public boolean hasUserFlagged(String userId){
            return flagsUsers.contains(userId);
        }
    }

    public static void createProposal(String title, String body, Date expiringDate, String publisherID, String neighbourhoodId, int max, RepublishTypes choice, ArrayList<String> filters){
        String id =  UUID.randomUUID().toString();
        Date publishDate = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(publishDate.toInstant(), ZoneId.systemDefault());
        publishDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        Proposal proposal = new Proposal(title, body, expiringDate, publishDate, publisherID, neighbourhoodId, id, max, choice, filters);

        FirebaseFirestore.getInstance().collection("Proposals").document(id).set(proposal); //TODO: make it asynchronous?
    }

    public static void getProposals(LocalDate day, String userId, ArrayList<String> filters, Types type, MyCallback<ArrayList<Proposal>> myCallback){
        ArrayList<Proposal> res = new ArrayList<>();
        LocalDateTime start = (day == null) ? LocalDateTime.MIN : day.atTime(0,0,0), end = (day == null) ? LocalDateTime.MAX : day.atTime(23,59,59);

        FirebaseFirestore.getInstance().collection("Proposals").orderBy("expiringDate").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    UserManager.getUser(userId, user -> {
                        for (DocumentSnapshot snap :  task.getResult()) { //for each element in the query
                            Proposal prop = snap.toObject(Proposal.class); //cast the result to a real proposal
                            LocalDateTime expD = prop.getExpiringDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime(); //gets the expiring date of the proposal
                            LocalDateTime pubD = prop.getPublishingDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime(); //gets the publishing date of the proposal

                            if (expD.isAfter(start) && expD.isBefore(end) && pubD.isBefore(LocalDateTime.now(ZoneId.of("ECT")))) {//if the proposal expiring date is between the limits
                                if (filters != null && prop.getFilters().containsAll(filters)) {
                                    switch (type) {
                                        case PROPOSED: if (prop.getPublisherID().equals(userId)) res.add(prop); break; //adds the proposal to the Proposal to be shown
                                        case ACCEPTED: if (prop.getAcceptersID().contains(userId)) res.add(prop); break;
                                        case AVAILABLE: if (!prop.getPublisherID().equals(userId) && (!prop.getAcceptersID().contains((String) userId)) && (prop.getMaxParticipants() - prop.getAcceptersID().size() > 0)) {
                                                            if (user.getNeighbourhoodID().equals(prop.getNeighbourhoodID()))
                                                                res.add(prop);
                                                        }
                                                        break;
                                    }
                                }

                            }
                        }

                        myCallback.onCallback(res);
                    });

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
        ProposalRepository.getProposal(documentId, new MyCallback<Proposal>() {
            @Override
            public void onCallback(Proposal result) {
                result.addParticipant(userId);
            }
        });
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).update("acceptersID", FieldValue.arrayUnion(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void rejectProposal(String documentId, String userId, MyCallback<Boolean> myCallback){
        ProposalRepository.getProposal(documentId, new MyCallback<Proposal>() {
            @Override
            public void onCallback(Proposal result) {
                result.removeParticipant(userId);
            }
        });
        FirebaseFirestore.getInstance().collection("Proposals"). document(documentId).update("acceptersID", FieldValue.arrayRemove(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void editProposal(String documentId, String title, String body, Date expiringDate,Date publishingDate, ArrayList<String> filters, int maxPartecipants, RepublishTypes type, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).update("title", title, "body", body, "expiringDate", expiringDate, "publishingDate", publishingDate, "filters", filters, "maxParticipants", maxPartecipants, "republishTypes", type).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }
    
    public static void deleteProposal(String documentId){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).delete();
    }

    public static void deleteProposal(String documentId, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }
    
    public static void clearProposals(){
        FirebaseFirestore.getInstance().collection("Proposals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    Proposal prop = snap.toObject(Proposal.class); //cast the result to a real proposal
                    LocalDateTime expD = prop.getExpiringDate().toInstant().atZone(ZoneId.of("ECT")).toLocalDateTime();
                    if (!expD.isAfter(LocalDateTime.now())) {
                        if (prop.getRepublishTypes() == RepublishTypes.NEVER)
                            deleteProposal(prop.getId());
                        else {
                            updatePeriodicProposal(prop, new MyCallback<Boolean>() {
                                @Override
                                public void onCallback(Boolean result) {

                                }
                            });
                        }
                    }
                }
            }
        });
    }
    public static void updatePeriodicProposal(Proposal prop, MyCallback<Boolean> myCallback) {
        Date newPublishing = null, newExpiring = null;
        LocalDateTime ldtPublish, ldtExpiring;
        switch (prop.getRepublishTypes()) {
            case DAILY:
                ldtPublish = LocalDateTime.ofInstant(prop.getPublishingDate().toInstant(), ZoneId.systemDefault()).plusDays(1).toLocalDate().atTime(0, 0, 0);
                ldtExpiring = LocalDateTime.ofInstant(prop.getExpiringDate().toInstant(), ZoneId.systemDefault()).plusDays(1);
                newExpiring = Date.from(ldtExpiring.atZone(ZoneId.systemDefault()).toInstant());
                newPublishing = Date.from(ldtPublish.atZone(ZoneId.systemDefault()).toInstant());
                break;
            case WEEKLY:
                ldtPublish = LocalDateTime.ofInstant(prop.getPublishingDate().toInstant(), ZoneId.systemDefault()).plusDays(7).toLocalDate().atTime(0, 0, 0);
                ldtExpiring = LocalDateTime.ofInstant(prop.getExpiringDate().toInstant(), ZoneId.systemDefault()).plusDays(7);
                newExpiring = Date.from(ldtExpiring.atZone(ZoneId.systemDefault()).toInstant());
                newPublishing = Date.from(ldtPublish.atZone(ZoneId.systemDefault()).toInstant());
                break;
            case MONTHLY:
                ldtPublish = LocalDateTime.ofInstant(prop.getPublishingDate().toInstant(), ZoneId.systemDefault()).plusMonths(1).toLocalDate().atTime(0, 0, 0);
                ldtExpiring = LocalDateTime.ofInstant(prop.getExpiringDate().toInstant(), ZoneId.systemDefault()).plusMonths(1);
                newExpiring = Date.from(ldtExpiring.atZone(ZoneId.systemDefault()).toInstant());
                newPublishing = Date.from(ldtPublish.atZone(ZoneId.systemDefault()).toInstant());
                break;
            case ANNUALLY:
                ldtPublish = LocalDateTime.ofInstant(prop.getPublishingDate().toInstant(), ZoneId.systemDefault()).plusYears(1).toLocalDate().atTime(0, 0, 0);
                ldtExpiring = LocalDateTime.ofInstant(prop.getExpiringDate().toInstant(), ZoneId.systemDefault()).plusYears(1);
                newExpiring = Date.from(ldtExpiring.atZone(ZoneId.systemDefault()).toInstant());
                newPublishing = Date.from(ldtPublish.atZone(ZoneId.systemDefault()).toInstant());
                break;
        }
        editProposal(prop.getId(), prop.getTitle(), prop.getBody(), newExpiring, newPublishing, prop.getFilters(), prop.getMaxParticipants(), prop.getRepublishTypes(), myCallback);
    }

    public static void updateProposalFlag(String documentId, ArrayList<String> flagsUsers, MyCallback<Boolean> myCallback) {
        FirebaseFirestore.getInstance().collection("Proposals").document(documentId).update("flagsUsers", flagsUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    public static void addFlagUser(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getProposal(documentId, new MyCallback<Proposal>() {
            @Override
            public void onCallback(Proposal result) {
                if(result != null) {
                    result.addFlagUser(userId);
                    updateProposalFlag(documentId, result.getFlagsUsers(), myCallback);
                }
            }
        });
    }

    public static void deleteFlagUser(String documentId, String userId, MyCallback<Boolean> myCallback) {
        getProposal(documentId, new MyCallback<Proposal>() {
            @Override
            public void onCallback(Proposal result) {
                if(result != null) {
                    result.deleteFlagUser(userId);
                    updateProposalFlag(documentId, result.getFlagsUsers(), myCallback);
                }
            }
        });
    }


    public static void hasUserFlagged(String documentId, String userId, MyCallback<Boolean> myCallback){
        getProposal(documentId, new MyCallback<Proposal>() {
            @Override
            public void onCallback(Proposal result) {
                if(result != null){
                    myCallback.onCallback(result.hasUserFlagged(userId));
                }
            }
        });
    }
}
