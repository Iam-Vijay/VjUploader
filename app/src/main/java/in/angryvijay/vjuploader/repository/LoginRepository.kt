package `in`.angryvijay.vjuploader.repository

import `in`.angryvijay.tubetamil.apiinterface.APIInterface
import `in`.angryvijay.tubetamil.apiinterface.ApiRequest
import `in`.angryvijay.vjuploader.MainModel
import javax.inject.Inject

/**
 * Created by Vijay on 12/16/2021.
 */
class LoginRepository @Inject constructor(private var apiInterface: APIInterface):ApiRequest() {
    suspend fun Login(user:String,pass:String): MainModel {
        return apirequest { apiInterface.login(user,pass).await() }
    }
}