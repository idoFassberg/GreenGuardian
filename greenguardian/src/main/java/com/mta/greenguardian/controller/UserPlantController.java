package com.mta.greenguardian.controller;

import com.mta.greenguardian.model.plant.UserPlant;
import com.mta.greenguardian.model.plant.UserPlantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserPlantController {
    @Autowired
    private UserPlantDao userPlantDao;

    @GetMapping("/user-plant/get-all")
    public List<UserPlant> getAllUserPlants(){
        //return userPlantDao.getAllUserPlantsById(LoggedInUser.Id) *later*
        return userPlantDao.getAllUserPlants();
    }

    @PostMapping("/user-plant/save")
    public UserPlant saveUserPlant(@RequestBody UserPlant userPlant) {
        return userPlantDao.save(userPlant);
    }
}
