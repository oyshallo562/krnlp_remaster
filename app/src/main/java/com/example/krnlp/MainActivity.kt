package com.example.krnlp

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)



        submitButton.setOnClickListener {
            val text = inputEditText.text.toString()
            fetchSentiment(text)
        }
    }

    private fun fetchSentiment(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://10.0.2.2:8000/predict?sentence=$text")
                // POST 대신 GET을 사용
                .build()

            val response = client.newCall(request).execute()
            val sentimentData = response.body?.string() ?: ""

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && sentimentData.isNotEmpty()) {
                    val jsonObject = JSONObject(sentimentData)
                    val sentiment = jsonObject.getString("sentiment")
                    val probability = jsonObject.getDouble("probability")*100.0

                    val sentimentTextView = findViewById<TextView>(R.id.sentimentTextView)
                    val InnerIcon = findViewById<ImageView>(R.id.InnerIcon)
                    val Con2 = findViewById<ConstraintLayout>(R.id.Con2)

                    Log.d("MainActivity", "sentiment : $sentiment")
                    Log.d("MainActivity", "probability : $probability")

                    // 배경색과 글자 설정
                    if (sentiment == "positive") {
                        //tint #1BE955 로 설정
                        Con2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1BE955"))
                        sentimentTextView.text = "${String.format("%.2f", probability)}% 긍정"
                        sentimentTextView.setTextColor(Color.WHITE)
                        InnerIcon.setBackgroundResource(R.drawable.mood)
                        //InnerIcon.setColorFilter(Color.WHITE)
                    } else {
                        //tint #FA1F1F 로 설정
                        Con2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FA1F1F"))
                        sentimentTextView.text = "${String.format("%.2f", probability)}% 부정"
                        sentimentTextView.setTextColor(Color.WHITE)
                        InnerIcon.setBackgroundResource(R.drawable.mood_bad)
                        //InnerIcon.setColorFilter(Color.WHITE)
                    }

                } else {
                    Toast.makeText(this@MainActivity, "Error fetching sentiment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}