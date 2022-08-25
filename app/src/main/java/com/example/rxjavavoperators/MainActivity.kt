package com.example.rxjavavoperators

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)


    }

    /**
     * This method send an observable data using observable
     * Using the following operators to save the cost of API and make it more efficient
     * debounce ->That uses to organize transferring data to observer with specific time limit
     * distinctUntilChanged->That uses to compare current search with before search
     * filter->That uses to filter the entry search
     * map -> That uses to handle downstream data
     */
    @SuppressLint("CheckResult")
    private fun autoCompleteSearch() {
        Observable.create(ObservableOnSubscribe() { emitter ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length != 0)
                        emitter.onNext(s.toString())
                }
            })

        }).doOnNext { c -> Log.d(TAG, "upstream=" + c) }
            .debounce(2, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .filter { it != "12" }
            .map { it -> (Integer.parseInt(it) * 2).toString() }
            .subscribe { o ->
                Log.d(TAG, "downstream=" + o)
                sendDataToAPI(o)
            }
    }

    /**
     * This method uses to simulate invoke api call like retrofit
     */
    @SuppressLint("CheckResult")
    private fun sendDataToAPI(data: String): Observable<String> {
        val observable = Observable.just("send data to API   = " + data)
        observable.subscribe { c -> Log.d(TAG, "Data send it to API" + c) }
        return observable

    }
}