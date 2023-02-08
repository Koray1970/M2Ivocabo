package com.example.m2ivocabo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeviceRecyclerViewAdapter(val context:Context ,val devicelist: ArrayList<DeviceItem>) :
    RecyclerView.Adapter<DeviceRecyclerViewAdapter.ModelViewHolder>() {
    class ModelViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val txtdevicecode = view.findViewById<TextView>(R.id.devicecode)
        val txtdevicename = view.findViewById<TextView>(R.id.devicename)

        fun bindItem(itm: DeviceItem) {
            if (itm != null) {
                txtdevicecode.text=itm.code
                txtdevicename.text=itm.name
                itemView.setOnClickListener {
                    val intent = Intent(it.context, BLEDeviceActionForm::class.java)
                    intent.putExtra("id", itm.id);
                    it.context.startActivity(intent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_devicelist, parent, false)

        return ModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (devicelist != null)
            return devicelist.size
        return 0
    }

    fun getData(): ArrayList<DeviceItem> {
        return devicelist
    }

    fun removeItem(position: Int,id:Int) {
        devicelist.removeAt(position)
        var dbDeviceHelper=DBDeviceHelper(context )
        dbDeviceHelper.deleteDevice(id)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItem(devicelist.get(position))
    }


}