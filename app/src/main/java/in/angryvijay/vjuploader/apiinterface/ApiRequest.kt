package `in`.angryvijay.tubetamil.apiinterface

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.lang.StringBuilder

/**
 * Created by Vijay on 12/15/2021.
 */
abstract class ApiRequest {
    suspend fun<T:Any> apirequest(call:suspend() -> Response<T> ):T {

        val response = call.invoke()
            if (response.isSuccessful){
                return response.body()!!
            }else{
                val error = response.errorBody().toString()
                val message = StringBuilder()
                try {
                message.append(JSONObject(error).getString("message"))
                }catch (e:JSONException){
                    message.append("\n")
                }
                message.append("Error Code ${response.code().toString()}")
                throw (MyApiExepction(message.toString()))
            }

    }
}
class MyApiExepction(tostring:String):IOException(tostring)