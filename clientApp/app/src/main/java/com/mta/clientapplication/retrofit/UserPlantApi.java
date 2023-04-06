package com.mta.clientapplication.retrofit;
import com.mta.clientapplication.model.UserPlant;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserPlantApi {

    @GET("/user-plant/get-all")
    Call<List<UserPlant>> getAllUserPlants();

    @POST("/user-plant/save")
    Call<UserPlant> saveUserPlant(@Body UserPlant userPlant);
}
