package `in`.angryvijay.vjuploader

import com.google.gson.annotations.SerializedName

/**
 * Created by Vijay on 12/15/2021.
 */
data class MainModel(
    @field:SerializedName("status")var status:String?= null,@field:SerializedName("msg") var msg:String? = null,
                    @field:SerializedName("data")var list:List<CatModel>? = null)

data class CatModel(@field:SerializedName("id")var id:String?=null,@field:SerializedName("cat_name")var name:String?=null)
