package com.codecaique.es3af

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codecaique.es3af.databinding.ActivityMainBinding
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var btn_user:Button
    lateinit var btn_car:Button
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    var mPrefs = getPreferences(MODE_PRIVATE)
    var userType = "car";


    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.floating.setOnClickListener({
            var t: JSONObject = retrive()
            if (t == null) {
                return@setOnClickListener
            }
            call(t)
        })
        setupUI();
        setupSocket();

    }

    private fun setupSocket() {

        try {
            mSocket = IO.socket("http://chat.socket.io")
        } catch (e: URISyntaxException) {

        }

        val onNewMessage = Emitter.Listener { args ->
            this.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val message: String
                try {
                    username = data.getString("username")
                    message = data.getString("message")
                } catch (e: JSONException) {
                    return@Runnable
                }

                // add the message to view
                addMessage(username, message)
            })
        }

        mSocket?.on("new message", onNewMessage);
        mSocket?.connect();
    }

    private fun addMessage(username: String, message: String) {

    }

    private fun setupUI() {
        btn_car = findViewById(R.id.btn_car)
        btn_user = findViewById(R.id.btn_user)

        switchButtons(0)
    }

    fun on1(view: View) {
        var intent = Intent(this, UserDetailsActivity::class.java);
        startActivity(intent)
    }

    fun on2(view: View) {
        var intent = Intent(this, NewIssueActivity::class.java);
        startActivity(intent)
    }
    fun on3(view: View) {
        var intent = Intent(this, HotNumbersActivity::class.java);
        startActivity(intent)
    }
    fun on4(view: View) {
        var intent = Intent(this, EmergencyActivity::class.java);
        startActivity(intent)
    }

    fun onCheck_user(view: View) {
        switchButtons(0)
    }


    fun onCheck_car(view: View) {
        switchButtons(1)
    }

    fun switchButtons(i: Int){
        if(i == 0){
            btn_car.setBackgroundColor(Color.GRAY)
            btn_user.setBackgroundColor(Color.BLUE)
            userType = "user"
        }
        else {
            btn_user.setBackgroundColor(Color.GRAY)
            btn_car.setBackgroundColor(Color.BLUE)
            userType = "car"
        }
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


    fun retrive() : JSONObject{
        val gson = Gson()
        val json: String? = mPrefs.getString("MyObject", "")
        val t: Request = gson.fromJson(json, Request::class.java)



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


        return  paramObject;
    }


    private fun attemptSend() {
        val message: String = "message"
        if (TextUtils.isEmpty(message)) {
            return
        }
        mSocket!!.emit("new message", message)
    }
}