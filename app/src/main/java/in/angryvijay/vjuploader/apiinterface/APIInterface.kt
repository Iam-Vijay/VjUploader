package `in`.angryvijay.tubetamil.apiinterface

import `in`.angryvijay.vjuploader.MainModel
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.*

/**
 * Created by Vijay on 12/15/2021.
 */
interface APIInterface {
    @Multipart
    @POST("uploadvideos.php")
    fun uploadimage(@Part("user_id") userid:RequestBody,
        @Part("songname") songname:RequestBody,
                    @Part("moviename") moviename:RequestBody,
                    @Part("category") cat:RequestBody,
                    @Part imgfile: MultipartBody.Part,
                    @Part videofile:MultipartBody.Part ):Deferred<retrofit2.Response<MainModel>>

    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("username")username:String,@Field("password")password:String):Deferred<retrofit2.Response<MainModel>>

    @GET("getcategory.php")
    fun getcategory():Deferred<retrofit2.Response<MainModel>>
}