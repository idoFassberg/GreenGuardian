package com.mta.greenguardian.model.plant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlantRepository extends CrudRepository<UserPlant,Integer> {

}