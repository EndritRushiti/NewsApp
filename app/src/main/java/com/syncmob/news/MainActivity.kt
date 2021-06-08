package com.syncmob.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.syncmob.news.databinding.ActivityMainBinding
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var pageNumber = 1
    var list = mutableListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.searchBtn.setOnClickListener {
            list = mutableListOf()
            SendRequest()
        }

        binding.loadMoreBtn.setOnClickListener {
            pageNumber += 1
            SendRequest()
        }
    }

    private fun getURL(): String {
        val topic = binding.searchEditText.text
        val apiKey = "be0263c1-9dd8-472d-92d6-569f1695e7b7"
        val pageSize = 10
        return "https://content.guardianapis.com/" +
                "$topic?page=$pageNumber&page-size=$pageSize&api-key=$apiKey"
    }

    private fun extractJson(response: String) {
        val jsonObject = JSONObject(response)
        val jsonResponseBody = jsonObject.getJSONObject("response")
        val results = jsonResponseBody.getJSONArray("results")

        for (i in 0..9) {
            val item = results.getJSONObject(i)
            val webTitle = item.getString("webTitle")
            val webUrl = item.getString("webUrl")
            val data = Data(webTitle, webUrl)
            list.add(data)
        }

        val adapter = NewsAdapter(list)
        binding.listView.adapter = adapter

    }

    private fun SendRequest() {
        val url = getURL()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    extractJson(response)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }
}