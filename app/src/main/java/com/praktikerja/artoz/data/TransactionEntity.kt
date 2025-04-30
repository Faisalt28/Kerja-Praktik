package com.praktikerja.artoz.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,  // Pemasukan atau Pengeluaran
    val category: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(), // Menggunakan timestamp
    val iconResId: Int
) : Parcelable
