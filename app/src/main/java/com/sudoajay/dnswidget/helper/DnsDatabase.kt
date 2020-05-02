package com.sudoajay.dnswidget.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DnsDatabase(context: Context?) : SQLiteOpenHelper(context, databaseName, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + databaseTableName + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    "Name Text,Dns1 INTEGER,Dns2 INTEGER ,Dns3 INTEGER,Dns4 INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $databaseTableName")
        onCreate(db)
    }

    fun deleteData() {
        val db = this.writableDatabase
        db.delete(databaseTableName, "ID =?", arrayOf(1.toString() + ""))
    }

    fun fillIt(name: String, dns1:Int, dns2: Int, dns3:Int, dns4: Int) {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(col2, name)
        contentValues.put(col3, dns1)
        contentValues.put(col4, dns2)
        contentValues.put(col3, dns3)
        contentValues.put(col4, dns4)

        sqLiteDatabase.insert(databaseTableName, null, contentValues)
    }

    fun checkForEmpty(): Boolean {
        val sqLiteDatabase = this.writableDatabase
        val cursor = sqLiteDatabase.rawQuery("select * from $databaseTableName", null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count <= 0
    }

    fun getTheValueFromId(): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery("SELECT * FROM $databaseTableName", null)
    }

    fun getTheRepeatedlyWeekdays(): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery("SELECT  Repeatedly ,Weekdays FROM $databaseTableName", null)
    }

    fun getTheChooseTypeRepeatedlyEndlessly(): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery(
            "SELECT Repeatedly,Weekdays,Endlessly FROM $databaseTableName",
            null
        )
    }

    fun updateTheTable(id: String, name: String, dns1:Int, dns2: Int, dns3:Int, dns4: Int) {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(col1, id)
        contentValues.put(col2, name)
        contentValues.put(col3, dns1)
        contentValues.put(col4, dns2)
        contentValues.put(col3, dns3)
        contentValues.put(col4, dns4)
        sqLiteDatabase.update(databaseTableName, contentValues, "ID = ?", arrayOf(id))
    }

    companion object {
        private const val databaseName = "BackgroundTimerDataBase.db"
        private const val databaseTableName = "BackgroundTimerDATABASE_TABLE_NAME"
        private const val col1 = "ID"
        private const val col2 = "Name"
        private const val col3 = "Dns1"
        private const val col4 = "Dns2"
        private const val col5 = "Dns3"
        private const val col6 = "Dns4"

    }
}