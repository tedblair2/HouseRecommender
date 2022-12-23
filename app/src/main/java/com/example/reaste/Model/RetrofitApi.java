package com.example.reaste.Model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST("content")
    Call<Api> createpost(
            @Field("postid") String post
    );

    @FormUrlEncoded
    @POST("collaborative")
    Call<Api> getPosts(
            @Field("userid") String userid
    );
}
