package com.codecaique.es3af

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class Model (val msg:String?)


class Request {
    @SerializedName("name")
    var name:String? = null;
    @SerializedName("idnum")
    var idnum:String?= null;
    @SerializedName("dob")
    var dob:String?= null;
    @SerializedName("gender")
    var gender:String?= null;
    @SerializedName("height")
    var height:Int?= null;
    @SerializedName("weight")
    var weight:Int?= null;
    @SerializedName("mobile")
    var mobile:Int?= null;
    @SerializedName("address")
    var address:String?= null;
    @SerializedName("blood")
    var blood:String?= null;
    @SerializedName("sugar")
    var sugar:Boolean?= null;
    @SerializedName("presure")
    var presure:Boolean?= null;
    @SerializedName("heart")
    var heart:Boolean?= null;
    @SerializedName("request_address")
    var request_address:String?= null;
    @SerializedName("type")
    var type:String?= null;
}

interface ApiService {
    @POST("new_request/")
    fun callApi(@Body t: JSONObject): Call<Model>
}