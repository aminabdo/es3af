package com.codecaique.es3af

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.databinding.DataBindingUtil
import com.codecaique.es3af.databinding.ActivityUserDetailsBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class UserDetailsActivity : AppCompatActivity() {


    private val TAG = "UserDetailsActivity"
    var t = Request()
    private lateinit var binding: ActivityUserDetailsBinding
    var mPrefs = getPreferences(MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        setupUI()
        getData()
    }

    private fun setupUI() {
    }

    private fun getData() {
        t = retrive()
        if(t == null){
            return
        }

        binding.titFullName.setText(t.name.toString())
        binding.titId.setText(t.idnum.toString())
        binding.titBirthDate.setText(t.dob.toString())
        //binding.spGender.setText(t.gender.toString())
        binding.titTall.setText(t.height.toString())
        binding.titWeight.setText(t.weight.toString())
        binding.titPhoneNumber.setText(t.mobile.toString())
        binding.titAddress.setText(t.address.toString())
        //binding.titB.setText(t.blood.toString())
        //binding.titS.setText(t.sugar.toString())
        //binding.titP.setText(t.presure.toString())
        //binding.titH.setText(t.heart.toString())
        binding.titIssueAddress.setText(t.request_address.toString())
        //binding.titIssueAddress.setText(t.type.toString())
    }

    fun onSend(view: View) {

        t.name = findViewById<TextInputEditText>(R.id.tit_full_name).text.toString()
        t.idnum = findViewById<TextInputEditText>(R.id.tit_id).text.toString()
        t.dob = findViewById<TextInputEditText>(R.id.tit_birth_date).text.toString()
        t.gender =findViewById<Spinner>(R.id.sp_gender).selectedItem.toString()
        try {
            t.height = Integer.parseInt(findViewById<TextInputEditText>(R.id.tit_tall).text.toString())
        }catch (e: Exception){
            t.height = 0 ;
        }
        try {
            t.weight = Integer.parseInt(findViewById<TextInputEditText>(R.id.tit_weight).text.toString())
        }catch (e: Exception){
            t.weight = 0 ;
        }

        try {
            t.mobile = Integer.parseInt(findViewById<TextInputEditText>(R.id.tit_phone_number).text.toString())
        }catch (e: Exception){
            t.mobile = 0 ;
        }


        t.address = (findViewById<TextInputEditText>(R.id.tit_address).text.toString())
        t.blood = (findViewById<Spinner>(R.id.sp_blood).selectedItem.toString())
        t.sugar = (findViewById<Switch>(R.id.s1).isActivated)
        t.presure = (findViewById<Switch>(R.id.s2).isActivated)
        t.heart = (findViewById<Switch>(R.id.s3).isActivated)
        t.request_address = (findViewById<TextInputEditText>(R.id.tit_issue_address).text.toString())
        t.type = (findViewById<Spinner>(R.id.sp_issue_type).selectedItem.toString())
        //Log.e(TAG, "onSend: "+t)

        val paramObject = JSONObject()
        paramObject.put("name", t.name)
        paramObject.put("idnum", t.idnum)
        paramObject.put("dob", t.dob)
        paramObject.put("gender", t.gender)
        paramObject.put("height", t.height)
        paramObject.put("weight", t.weight)
        paramObject.put("mobile", t.mobile)
        paramObject.put("address", t.address)
        paramObject.put("blood", t.blood)
        paramObject.put("sugar", t.sugar)
        paramObject.put("presure", t.presure)
        paramObject.put("heart", t.heart)
        paramObject.put("request_address", t.request_address)
        paramObject.put("type", t.type)

        save(t)
        call(paramObject)
    }


    fun call(t: JSONObject) {


        val client = OkHttpClient.Builder()
            .connectTimeout(99999, TimeUnit.SECONDS)
            .readTimeout(99999, TimeUnit.SECONDS).build()

        Log.e(TAG, "call: " + t)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://77.91.143.19:3000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.callApi(t)


        call.enqueue(object : Callback<Model> {
            override fun onResponse(call: Call<Model>?, response: retrofit2.Response<Model>?) {
                Log.e(TAG, "onResponse: " + response.toString())
            }

            override fun onFailure(call: Call<Model>?, t: Throwable?) {
                Log.e(TAG, "onFailure: " + t.toString())
            }
        })
    }





    fun save(request: Request){
        mPrefs = getPreferences(MODE_PRIVATE)

        var myObject:Request = request

        var prefsEditor: SharedPreferences.Editor = mPrefs.edit();
        var gson = Gson();
        var json = gson.toJson(myObject);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    fun retrive() : Request{
        val gson = Gson()
        val json: String? = mPrefs.getString("MyObject", "")
        val obj: Request = gson.fromJson(json, Request::class.java)
        return  obj;
    }

    fun on_save(view: View) {
        if(t != null)
            save(t)
    }
}

