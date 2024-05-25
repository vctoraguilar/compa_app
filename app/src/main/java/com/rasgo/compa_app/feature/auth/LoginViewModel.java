package com.rasgo.compa_app.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.rasgo.compa_app.data.Repository;
import com.rasgo.compa_app.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private Repository repository;

    public LoginViewModel(Repository repository){
        this.repository = repository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        return repository.login(userInfo);
    }

}
