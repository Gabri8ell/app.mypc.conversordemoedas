package app.mypc.conversordemoedas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.view.get
import androidx.core.view.size
import app.mypc.conversordemoedas.api.Endpoint
import app.mypc.conversordemoedas.databinding.ActivityMainBinding
import app.mypc.conversordemoedas.util.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.create

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        getCurrencies()

        binding.btnConverter.setOnClickListener {
            converterMoedas()
        }

    }

    private fun converterMoedas() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(binding.spinnerFrom.selectedItem.toString(), binding.spinnerTo.selectedItem.toString())
            .enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val data = response.body()?.entrySet()?.find {
                        it.key == binding.spinnerTo.selectedItem.toString()
                    }

                    val rate : Double = data?.value.toString().toDouble()

                    binding.txvTitle.text = (binding.edtValor.text.toString().toDouble() * rate).toString() + " "+
                            binding.spinnerTo.selectedItem.toString().uppercase()
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    TODO("Not yet implemented")
                }

            })
    }


    private fun getCurrencies() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }
                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)

                binding.spinnerFrom.adapter = adapter
                binding.spinnerTo.adapter = adapter

                binding.spinnerFrom.setSelection(data.indexOf("brl"))
                binding.spinnerTo.setSelection(data.indexOf("usd"))

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                print(t)
            }


        })
    }
}