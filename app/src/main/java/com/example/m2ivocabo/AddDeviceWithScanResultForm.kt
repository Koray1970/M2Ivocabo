package com.example.m2ivocabo

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlin.reflect.typeOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MACADDRESS = "macaddress"
private const val ARG_LATLNG = "latlng"

/**
 * A simple [Fragment] subclass.
 * Use the [AddDeviceWithScanResultForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDeviceWithScanResultForm : Fragment() {
    // TODO: Rename and change types of parameters
    private var macaddress: String? = null
    private var latlng: String? = null
    private var latLng: LatLng? = null
    private var TAG: String = AddDeviceWithScanResultForm.javaClass.simpleName
    var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            macaddress = it.getString(ARG_MACADDRESS)
            latlng = it.getString(ARG_LATLNG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_add_device_with_scan_result_form, container)
        var txtmacaddress = view.findViewById<EditText>(R.id.txtmacaddress)
        txtmacaddress.isClickable = false
        txtmacaddress.isEnabled = false
        macaddress=AppHelper().StringToMacaddress(macaddress.toString())
        txtmacaddress?.setText(macaddress)

        var txtdevicename = view.findViewById<EditText>(R.id.txtdevicename)

        val fragManager = requireActivity().supportFragmentManager
        val transaction = fragManager.beginTransaction()
        transaction.replace(
            R.id.flmainframe,
            Dashboard()
        )
        transaction.addToBackStack(null)

        var btnsubmit = view.findViewById<Button>(R.id.btnsubmit)
        btnsubmit.setOnClickListener {
            if (txtmacaddress.text.isNullOrEmpty() || txtdevicename.text.isNullOrEmpty()) {
                if (txtmacaddress.text.isNullOrEmpty()) {
                    txtmacaddress.setError("no text")
                }
                if (txtdevicename.text.isNullOrEmpty()) {
                    var errormessage = R.string.form_pleaseinputdevicename.toString();
                    txtdevicename.setError(errormessage)
                }
            } else {
                var dbevent = DBDeviceHelper(requireContext())

                dbevent.addDevice(
                    DeviceItem(
                        id = null,
                        code = txtmacaddress.text.toString(),
                        name = txtdevicename.text.toString(),
                        codetype = DeviceCodeType.MACADDRESS,
                        latlng = latlng
                    )
                )
                transaction.commit()
            }
        }
        var btncancel = view.findViewById<Button>(R.id.btncancel)
        btncancel.setOnClickListener {


            transaction.commit()
        }
        return inflater.inflate(
            R.layout.fragment_add_device_with_scan_result_form,
            container,
            false
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddDeviceWithScanResultForm.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddDeviceWithScanResultForm().apply {
                arguments = Bundle().apply {
                    putString(ARG_MACADDRESS, param1)
                    putString(ARG_LATLNG, param2)
                }
            }
    }
}