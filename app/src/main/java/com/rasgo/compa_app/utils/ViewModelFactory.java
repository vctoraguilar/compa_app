package com.rasgo.compa_app.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rasgo.compa_app.data.Repository;
import com.rasgo.compa_app.feature.auth.LoginViewModel;
import com.rasgo.compa_app.data.remote.ApiClient;
import com.rasgo.compa_app.data.remote.ApiService;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
       if (modelClass.isAssignableFrom(LoginViewModel.class)){
           return (T) new LoginViewModel(repository);
       }
       throw new IllegalArgumentException("ViewModel no está funcionando");
    }
}
