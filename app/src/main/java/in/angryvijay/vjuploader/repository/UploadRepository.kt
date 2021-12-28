package `in`.angryvijay.vjuploader.repository

import `in`.angryvijay.tubetamil.apiinterface.APIInterface
import `in`.angryvijay.tubetamil.apiinterface.ApiRequest
import `in`.angryvijay.vjuploader.MainModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * Created by Vijay on 12/15/2021.
 */
class UploadRepository @Inject constructor(private val apiInterface: APIInterface):ApiRequest(){

    suspend fun uploadimage(userid:RequestBody,songname:RequestBody,imagename:RequestBody,cat:RequestBody,image:MultipartBody.Part,video:MultipartBody.Part): MainModel {
        return apirequest { apiInterface.uploadimage(userid,songname,imagename,cat,image,video).await() }
    }

    suspend fun getcat(): MainModel {
        return apirequest { apiInterface.getcategory().await() }
    }
}