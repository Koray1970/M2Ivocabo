package com.example.m2ivocabo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

class AddDeviceAfterScanResultForm : AppCompatActivity() {
    companion object {
        private var TAG: String = AddDeviceAfterScanResultForm.javaClass.simpleName
        private var macaddress: String? = null
        var intentlatlng: String? = null
        private var latLng: LatLng? = null

        var gson = Gson()
        var intentMainActivity: Intent? = null
        var appHelper = AppHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device_after_scan_result_form)
        intentMainActivity = Intent(this@AddDeviceAfterScanResultForm, MainActivity::class.java)

        intentlatlng = intent.getStringExtra("latlng")
        latLng = gson.fromJson(intentlatlng, LatLng::class.java)

        var ismanual = intent.getBooleanExtra("ismanual", false)
        var txtmacaddress = findViewById<EditText>(R.id.txtmacaddress)
        txtmacaddress.isClickable = false
        txtmacaddress.isEnabled = ismanual
        macaddress = intent.getStringExtra("macaddress")
        if (macaddress != null) {
            macaddress = AppHelper().StringToMacaddress(macaddress.toString())
            txtmacaddress?.setText(macaddress)
        }
        var txtdevicename = findViewById<EditText>(R.id.txtdevicename)

        var btnsubmit = findViewById<Button>(R.id.btnsubmit)
        btnsubmit.setOnClickListener {
            var macaddressisvalid = false
            if (txtmacaddress.text.isNullOrEmpty() || txtdevicename.text.isNullOrEmpty()) {
                if (txtmacaddress.text.isNullOrEmpty()) {
                    txtmacaddress.setError("no text")

                }
                if (txtdevicename.text.isNullOrEmpty()) {
                    var errormessage = R.string.form_pleaseinputdevicename.toString();
                    txtdevicename.setError(errormessage)
                }
            } else {
                macaddressisvalid = AppHelper().CheckMacAddress(txtmacaddress.text.toString())
                Log.v(TAG, "macaddressisvalid : $macaddressisvalid")
                if (!macaddressisvalid)
                    txtmacaddress.setError("Mac Address not well formatted!")
                else {

                    var dbevent = DBDeviceHelper(this)
                    dbevent.addDevice(
                        DeviceItem(
                            id = null,
                            code = txtmacaddress.text.toString(),
                            name = txtdevicename.text.toString(),
                            codetype = DeviceCodeType.MACADDRESS,
                            latlng = intentlatlng
                        )
                    )
                    startActivity(intentMainActivity)
                }
            }
        }
        var btncancel = findViewById<Button>(R.id.btncancel)
        btncancel.setOnClickListener {
            startActivity(intentMainActivity)
        }
    }
}