package com.example.m2ivocabo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.ViewDebug.IntToString
import androidx.annotation.Nullable

class DBDeviceHelper(context: Context, factory: SQLiteDatabase.CursorFactory) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + "id" + " INTEGER PRIMARY KEY, " +
                "name" + " TEXT," +
                "code" + " TEXT" +
                "codetype" + " INTEGER" + ")")

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addDevice(deviceItem: DeviceItem) {
        var values = ContentValues()
        values.put(NAME, deviceItem.name)
        values.put(CODE, deviceItem.code)
        values.put(CODETYPE, deviceItem.codetype.ordinal)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateDevice(deviceItem: DeviceItem) {
        var values = ContentValues()
        values.put(NAME, deviceItem.name)
        values.put(CODE, deviceItem.code)
        values.put(CODETYPE, deviceItem.codetype.ordinal)

        val db = this.writableDatabase
        db.update(TABLE_NAME, values, "id=$deviceItem.id", arrayOf("id"))
        db.close()
    }

    fun deleteDevice(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "id=$id", arrayOf("id"))
        db.close()
    }

    fun deviceList(): ArrayList<DeviceItem>? {
        val db = this.readableDatabase
        var dbset = db.rawQuery("SELECT * FROM " + TABLE_NAME + "ORDER BY name ASC", null)
        if (dbset != null) {
            try {
                var deviceitems:ArrayList<DeviceItem> = ArrayList<DeviceItem>()
                do{
                    var deviceItem = DeviceItem(
                        dbset.getInt(getColumnIndex(dbset, ID)),
                        dbset.getString(getColumnIndex(dbset, NAME)),
                        dbset.getString(getColumnIndex(dbset, CODE)),
                        DeviceCodeType.values().get(dbset.getInt(getColumnIndex(dbset, CODETYPE))),
                        dbset.getString(getColumnIndex(dbset, LATLNG)))
                    deviceitems.add(deviceItem)
                }
                while (dbset.moveToNext())
            } catch (exception: Exception) {

            } finally {
                dbset.close()
            }

        }
        dbset.close()
        return null
    }

    fun deviceItem(id: Int): DeviceItem? {
        val db = this.readableDatabase
        var dbset = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=$id LIMIT 1", null)
        if (dbset != null) {
            var deviceItem = DeviceItem(
                dbset.getInt(getColumnIndex(dbset, ID)),
                dbset.getString(getColumnIndex(dbset, NAME)),
                dbset.getString(getColumnIndex(dbset, CODE)),
                DeviceCodeType.values().get(dbset.getInt(getColumnIndex(dbset, CODETYPE))),
                dbset.getString(getColumnIndex(dbset, LATLNG))
            )
            dbset.close()
            return deviceItem
        }
        dbset?.close()
        return null
    }

    private fun getColumnIndex(cursor: Cursor, columnName: String): Int {
        return cursor.getColumnIndex(columnName)
    }

    companion object {
        private var DATABASE_NAME: String = "ivocabodb"
        private val DATABASE_VERSION: Int = 1
        private val TABLE_NAME: String = "devicelist"
        private val ID: String = "id"
        private var CODE: String = "code"
        private var CODETYPE: String = "codetype"
        private var NAME: String = "name"
        private var LATLNG: String = "latlng"
    }
}