package com.example.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE=100
    private var mTimer:Timer?=null
    private var mTimerSec=0.0
    private var mHandler=Handler()
    private var flag=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            }else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            }else{
            getContentsInfo()
        }

    }

    fun OnRequestPermissionsResult(requestCode:Int,permissions:Array<String>,grantResults:IntArray){
        when(requestCode){
            PERMISSIONS_REQUEST_CODE ->
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo(){
        //画像取得
        val resolver=contentResolver
        val cursor=resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        //進むボタンを押し画像が最後の場合最初に戻る
        next_button.setOnClickListener {
            if(cursor.moveToNext()==false){
                cursor.moveToFirst()
            }
                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id=cursor.getLong(fieldIndex)
                val imageUri=
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                imageView.setImageURI(imageUri)
        }

        //戻るボタンを押し画像が最初の場合最後表示
        back_button.setOnClickListener {
            if (cursor.moveToPrevious()==false){
                cursor.moveToFirst()
            }
            val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id=cursor.getLong(fieldIndex)
            val imageUri=
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

            imageView.setImageURI(imageUri)
        }

        startStop_button.setOnClickListener {
            if(flag){
                next_button.isEnabled=false
                back_button.isEnabled=false
                flag=false
                //タイマー作成
                if (mTimer==null){
                    mTimer= Timer()
                    mTimer!!.schedule(object:TimerTask(){
                        override fun run() {
                            mTimerSec += 2
                            mHandler.post {
                                if(cursor.moveToNext()==false){
                                    cursor.moveToFirst()
                                }

                                val fieldIndex=cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id=cursor.getLong(fieldIndex)
                                val imageUri=
                                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                                imageView.setImageURI(imageUri)
                            }
                        }
                    },2000,2000)
                }
            }else{
                startStop_button.text="再生"
                next_button.isEnabled=true
                back_button.isEnabled=true
                mTimer!!.cancel()

                mTimer= null
                flag=true
            }
        }
    }
}

