package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.core.content.FileProvider
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URLConnection


class MainActivity : Activity()  {

    private lateinit var binding: ActivityMainBinding

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var activeListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
// 1
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
// 2
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        activeListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {



            }
            override fun onSensorChanged(event: SensorEvent) {
                val acc_x: MutableList<String> = mutableListOf()
                if (event != null) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {



                        acc_x.add(event.values[0].toString())

                        val data = JSONObject()
                        try {
                            data.put("acceleration_x", acc_x)
                            binding.acc.text=data.toString()
                            Handler().postDelayed({

                                sensorManager!!.unregisterListener(this)
                                save(applicationContext,data.toString())
                                binding.acc.text="done"
                            }, 1000)

                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {

                        binding.meg.text = "x: " + event.values[0].toString() + "\n"+
                                "y: " +event.values[1].toString() +  "\n"+
                                "z: " + event.values[2].toString() + "\n"


                    }else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {

                        binding.gyroscope.text = "x: " + event.values[0].toString() + "\n"+
                                "y: " +event.values[1].toString() +  "\n"+
                                "z: " + event.values[2].toString() + "\n"
                    }
                }
            }
        }

        sensorManager!!.registerListener(activeListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    private fun shareFile(file: File) {
        val contentUri = FileProvider.getUriForFile(this,"com.example.myapplication.fileprovider",file)
        /** FILE_PROVIDER_AUTHORITY - "applicationId" + ".fileprovider" */

        if (contentUri != null) {
            var shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setType("message/rfc822")

            /** set the corresponding mime type of the file to be shared */
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

            startActivity(Intent.createChooser(shareIntent, "Share to"))
        }
    }
    @Throws(IOException::class)
    fun save(context: Context, jsonString: String?) {
        val rootFolder: File? = Environment.getExternalStorageDirectory();
        val jsonFile = File(rootFolder, "file.json")
        val writer = FileWriter(jsonFile)
        writer.write(jsonString)
        writer.close()
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir: File = File(jsonFile.toURI())

       shareFile(file = jsonFile)
        //or IOUtils.closeQuietly(writer);
    }

    }


