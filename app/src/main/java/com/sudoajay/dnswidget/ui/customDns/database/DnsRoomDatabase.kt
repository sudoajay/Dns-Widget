package com.sudoajay.dnswidget.ui.customDns.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Dns::class], version = 1 , exportSchema = false)
abstract class DnsRoomDatabase : RoomDatabase() {

    abstract fun dnsDao(): DnsDao



    companion object {
        @Volatile
        private var INSTANCE: DnsRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): DnsRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DnsRoomDatabase::class.java,
                    "dns_Database"
                )

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}