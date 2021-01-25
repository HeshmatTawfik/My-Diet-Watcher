package com.heshmat.mydietwatcher;

import com.heshmat.mydietwatcher.models.EdamamModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("parser?app_id=c1fbc4bd&app_key=86af1ac898ab31437a01e5bf1dd7dd06")
    Call<EdamamModel> getParsed(@Query("ingr") String foodName);

}
