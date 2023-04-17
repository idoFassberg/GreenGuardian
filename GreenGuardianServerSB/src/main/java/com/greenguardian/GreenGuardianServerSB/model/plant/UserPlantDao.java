/*
package com.greenguardian.GreenGuardianServerSB.model.plant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserPlantDao {
    @Autowired
    private UserPlantRepository repository;

    public UserPlant save(UserPlant userPlant){
        return repository.save(userPlant);
    }

    public void delete(UserPlant userPlant){
        repository.delete(userPlant);
    }

    public List<UserPlant> getAllUserPlants(){
        List<UserPlant> userPlantList = new ArrayList<UserPlant>();
        repository.findAll().forEach(userPlantList :: add);

        return userPlantList;
    }

    public List<UserPlant> getAllUserPlantsById(int userId){
        List<UserPlant> userPlantList = new ArrayList<UserPlant>();
        repository.findAll().forEach(userPlant-> {
            if(userPlant.getUserId() == userId){
                userPlantList.add(userPlant);
            }
        });

        return userPlantList;
    }
}
*/
