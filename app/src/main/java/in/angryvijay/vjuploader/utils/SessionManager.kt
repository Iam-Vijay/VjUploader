package `in`.angryvijay.vjuploader.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.channels.produce
import javax.inject.Inject

/**
 * Created by Vijay on 11/15/2021.
 */


@ActivityScoped
class SessionManager @Inject constructor(@ApplicationContext context: Context){
     var context: Context = context
    var name = "mysession"
     var sharedPreferences: SharedPreferences
     var editor: SharedPreferences.Editor
    val privatemode = 0
    init {
        sharedPreferences = context.getSharedPreferences(name,privatemode)
        editor = sharedPreferences.edit()
    }

    public fun savevalue(key: String,value:String) {
        editor.putString(key,value)
        editor.commit()
    }
    public fun setlogin(value: Boolean){
        editor.putBoolean("islogged",value)
    }
    public fun Isloggedin(value: String ="islogged"):Boolean{
        return sharedPreferences.getBoolean(value,false)
    }

    public fun getvalue(key: String): String? {
        return sharedPreferences.getString(key,"")
    }
    public fun clear(){
        editor.clear()
        editor.commit()
    }
}