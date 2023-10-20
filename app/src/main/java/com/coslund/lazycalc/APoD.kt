package com.coslund.lazycalc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.AsyncTask
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.net.HttpURLConnection
import java.net.URL


class APoD (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var screenWidth : Float = 0.0f;

    public var titleText : TextView = TextView(context)
    public var descriptionText : TextView = TextView(context)
    public var copyrightText : TextView = TextView(context)

    public var imageView : ImageView = ImageView(context)
    public var dateButton : Button = Button(context)
    public var dateText : TextView = TextView(context)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        descriptionText.setMovementMethod(ScrollingMovementMethod());

        dateButton.setOnClickListener {
            setDate()
        }

        fetchInfo()
    }

    fun setDate() {
        titleText.text = dateText.text
        fetchInfo()
    }

    fun fetchInfo() {
        titleText.text = "Fetching"
        copyrightText.text = ""
        descriptionText.text = ""

        var url =
            "https://api.nasa.gov/planetary/apod?api_key=XZofmehtWe1c8qD6TrhEtzvIFdzxlEvzpzjX0yKh"

        if (dateText.text != "") {
            url = url + "&date=" + dateText.text
        }

        val queue = Volley.newRequestQueue(context)

        var img: String = ""

        val jsonRequest = JsonObjectRequest(Request.Method.GET, // what sort of request
            url, // url as a string
            null, // json data to throw at the server
            { response ->
                // a closure of what to do if the request succeeds
                titleText.text =
                    response.getString("title")    // note we don't need thread shenanigans
                descriptionText.text = response.getString("explanation")
                if (response.has("copyright")) {
                    copyrightText.text = response.getString("copyright")
                } else {
                    copyrightText.text = ""
                }

                img = response.getString("url")

                val renderTask = RenderImageTask()
                renderTask.execute(img)
                val image = renderTask.get()
                imageView.setImageBitmap(image)
            },
            { error ->
                // what to do if things fail
                descriptionText.text = "That didn't work: ${error.localizedMessage}"
            })

        queue.add(jsonRequest)
    }
}

internal class RenderImageTask :
    AsyncTask<String?, Void?, Bitmap?>() {
    private var exception: Exception? = null
    override fun doInBackground(vararg urls: String?) : Bitmap? {
        return try {
            val url = URL(urls[0])
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            exception = e
            null
        }
    }
}