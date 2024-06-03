package com.rasgo.compa_app.data.remote;

import com.rasgo.compa_app.feature.auth.LoginActivity;
import com.rasgo.compa_app.model.auth.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

}
