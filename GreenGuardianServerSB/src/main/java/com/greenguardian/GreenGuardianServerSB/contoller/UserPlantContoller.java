package com.greenguardian.GreenGuardianServerSB.contoller;

import com.greenguardian.GreenGuardianServerSB.model.plant.UserPlant;

import com.greenguardian.GreenGuardianServerSB.serviceFirebase.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class UserPlantContoller {
    @Autowired
    private FirebaseService firebaseServices;

   /* @GetMapping("/user-plant/get-all")
    public List<UserPlant> getAllUserPlants(String plantName) throws ExecutionException, InterruptedException {
        //return userPlantDao.getAllUserPlantsById(LoggedInUser.Id) *later*
        return (List<UserPlant>) firebaseServices.getUserDetails(plantName);
    }*/
   @GetMapping("/user-plant/get-all")
   public UserPlant getUserPlant(String plantName) throws ExecutionException, InterruptedException {
       //return userPlantDao.getAllUserPlantsById(LoggedInUser.Id) *later*
       return firebaseServices.getUserDetails(plantName);
   }

    @PostMapping("/createUser")
    public UserPlant postExample(@RequestBody UserPlant plant) throws InterruptedException, ExecutionException {
        return firebaseServices.saveUserDetails(plant);    }

    @PutMapping("/updateUser")
    public String putExample(@RequestBody UserPlant plant) throws InterruptedException, ExecutionException {
        return "Updated User" + plant.getPlantName();
    }

    @DeleteMapping("/deleteUser")
    public String deleteExample(@RequestHeader String name) {
        return "Deleted User " + name;
    }
}