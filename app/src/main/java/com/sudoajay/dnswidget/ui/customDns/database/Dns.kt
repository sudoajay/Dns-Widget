package com.sudoajay.dnswidget.ui.customDns.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "DnsTable")
class Dns(
    @PrimaryKey(autoGenerate = true ) var id: Long?,
    @ColumnInfo(name = "Name") val dnsName: String,
    @ColumnInfo(name = "Dns1") val dns1: String,
    @ColumnInfo(name = "Dns2") val dns2: String,
    @ColumnInfo(name = "Dns3") val dns3: String,
    @ColumnInfo(name = "Dns4") val dns4: String,
    @ColumnInfo(name = "Filter") val filter: String,
    @ColumnInfo(name = "Custom") val custom: Boolean


)