package `in`.angryvijay.vjuploader.viewmodel

import `in`.angryvijay.vjuploader.MainModel
import `in`.angryvijay.vjuploader.repository.LoginRepository
import `in`.angryvijay.vjuploader.utils.Controller
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vijay on 12/16/2021.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(var loginRepository: LoginRepository):ViewModel(){

    var uploadmutablelivedata: MutableLiveData<Controller<MainModel>> = MutableLiveData()

    private val executionHandler = CoroutineExceptionHandler{_,exception ->
        uploadmutablelivedata.postValue(exception.message?.let { Controller.error(it,null) })

    }

    fun login(username:String,password:String){
        viewModelScope.launch (executionHandler){
            uploadmutablelivedata.postValue(Controller.loading(null))
            val data = loginRepository.Login(username,password)
            uploadmutablelivedata.postValue(Controller.success(data))
        }
    }
}