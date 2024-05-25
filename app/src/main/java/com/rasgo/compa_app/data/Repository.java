package com.rasgo.compa_app.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rasgo.compa_app.feature.auth.LoginActivity;
import com.rasgo.compa_app.feature.data.remote.ApiService;
import com.rasgo.compa_app.model.auth.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static Repository instance = null;
    private final ApiService apiService;

    private Repository(ApiService apiService) {
        this.apiService=apiService;
    }

    public static Repository getRrepository(ApiService apiService) {
        if (instance == null) {
            instance = new Repository(apiService);
        }
        return instance;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        MutableLiveData<AuthResponse> auth = new MutableLiveData<>();
        Call<AuthResponse> call = apiService.login(userInfo);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
               if (response.isSuccessful()) {
                   auth.postValue(response.body());
               }else{

               }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {

            }
        });
        return auth;
    }
}
