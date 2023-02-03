package com.example.m2ivocabo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        values.put("name", deviceItem.name)
        values.put("code", deviceItem.code)
        values.put("codetype", deviceItem.codetype.ordinal)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateDevice(deviceItem: DeviceItem) {
        var values = ContentValues()
        values.put("name", deviceItem.name)
        values.put("code", deviceItem.code)
        values.put("codetype", deviceItem.codetype.ordinal)

        val db = this.writableDatabase
        db.update(TABLE_NAME, values, "id=$deviceItem.id", arrayOf("id"))
        db.close()
    }

    fun deleteDevice(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "id=$id", arrayOf("id"))
        db.close()
    }

    fun deviceList(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME +"ORDER BY name ASC", null)
    }

    fun deviceItem(id: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=$id LIMIT 1", null)
    }

    companion object {
        private var DATABASE_NAME: String = "ivocabodb"
        private val DATABASE_VERSION: Int = 1
        private val TABLE_NAME: String = "devicelist"
    }
}