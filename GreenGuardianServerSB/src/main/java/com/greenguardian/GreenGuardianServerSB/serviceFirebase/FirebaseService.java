package com.greenguardian.GreenGuardianServerSB.serviceFirebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.greenguardian.GreenGuardianServerSB.model.plant.UserPlant;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {
    public UserPlant saveUserDetails(UserPlant plant) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection("UserPlant").document("hZpfDfUsbZ3hNolRQc0R").set(plant);
        future.get();
        return plant;
    }
    public UserPlant getUserDetails(String name) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("UserPlant").document(name);
        // asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        // block on response
        DocumentSnapshot document = future.get();
        UserPlant plant = null;
        if (document.exists()) {
            // convert document to POJO
            plant = document.toObject(UserPlant.class);
            System.out.println(plant);
            return plant;
        } else {
            System.out.println("No such document!");
            return null;
        }
    }
}
