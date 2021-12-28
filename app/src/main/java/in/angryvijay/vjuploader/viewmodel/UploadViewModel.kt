package `in`.angryvijay.vjuploader.viewmodel

import `in`.angryvijay.vjuploader.MainModel
import `in`.angryvijay.vjuploader.repository.UploadRepository
import `in`.angryvijay.vjuploader.utils.Controller
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(val uploadRepository: UploadRepository): ViewModel() {
    var uploadmutablelivedata:MutableLiveData<Controller<MainModel>> = MutableLiveData()
    var catmutablelivedata:MutableLiveData<Controller<MainModel>> = MutableLiveData()

    private val executionHandler = CoroutineExceptionHandler{_,exception ->
        uploadmutablelivedata.postValue(exception.message?.let { Controller.error(it,null) })
        catmutablelivedata.postValue(exception.message?.let { Controller.error(it,null) })

    }

    fun upload(userid:RequestBody,songname:RequestBody,moviename:RequestBody,cat:RequestBody,imgfile:MultipartBody.Part,videofile:MultipartBody.Part){
        viewModelScope.launch(executionHandler) {
            uploadmutablelivedata.postValue(Controller.loading(null))
            val data = uploadRepository.uploadimage(userid,songname, moviename,cat,imgfile,videofile)
            uploadmutablelivedata.postValue(Controller.success(data))
        }
    }

    fun getcat(){
        viewModelScope.launch {
            catmutablelivedata.postValue(Controller.loading(null))
            val data = uploadRepository.getcat()
            catmutablelivedata.postValue(Controller.success(data))
        }
    }
}