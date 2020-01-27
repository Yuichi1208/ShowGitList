package com.httori.yuichi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

class MainActivity : AppCompatActivity(), RecyclerViewHolder.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var NETWORK_ERROR = "ネットワークへの接続に失敗しました。\nネット環境の良いところで再度お試しください"

    interface IGetRepos{
        @GET("{id}/repos")
        fun getRepos(@Path("id") userID : String) : Call<List<Repo>>
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .baseUrl("https://api.github.com/users/")
        .build()

    private val service: IGetRepos = retrofit.create(IGetRepos::class.java)

    //=============================================================================
    //
    //=============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 通信処理
        val call = service.getRepos("k163377")
        //val call = service.getRepos("Yuichi1208")
        call.enqueue(object : Callback<List<Repo>> {

            // 通信成功
            override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                try{
                    // 通信結果を受け取り
                    var arr: List<Repo>? = response!!.body()
                    val list: MutableList<String> = mutableListOf()

                    // 全てを一覧として表示
                    for( i in 0..(arr!!.size-1) ) {
                        list.add(arr!![i].full_name)
                        Log.d("onResponse", arr!![i].full_name)
                    }

                    // 受け取ったデータをリストに表示させる
                    viewAdapter = RecyclerAdapter(this@MainActivity, this@MainActivity, list)
                    viewManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

                    recyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
                        setHasFixedSize(true)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }

                }catch (e: IOException){
                    Log.d("onResponse", "IOException" )
                }
            }

            // 通信失敗
            override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                Toast.makeText(applicationContext, NETWORK_ERROR, Toast.LENGTH_SHORT).show()
                Log.d("onResponse", "ネットワークの接続に失敗しました")
            }
        })
    }

    //=============================================================================
    // リストを押した時に呼ばれる関数
    //=============================================================================
    override fun onItemClick(view: View, position: Int) {
        Toast.makeText(applicationContext, "position $position was tapped", Toast.LENGTH_SHORT).show()
    }
}
