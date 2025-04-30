package com.praktikerja.artoz.ui.add_transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.praktikerja.artoz.R
import com.praktikerja.artoz.adapter.Option
import com.praktikerja.artoz.adapter.OptionAdapter
import com.praktikerja.artoz.data.TransactionEntity
import com.praktikerja.artoz.databinding.ActivityAddBinding
import com.praktikerja.artoz.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var selectedcategory: String? = null
    private var selectedIconResId: Int = R.drawable.baseline_edit_note_24_large // Default ikon
    private var selectedDate: Long = System.currentTimeMillis()
    private var transaction: TransactionEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_up, 0)

        // Ambil data dari intent (mode edit jika data ada)
        val transactionId = intent.getIntExtra("TRANSACTION_ID", -1)
        val amount = intent.getDoubleExtra("TRANSACTION_AMOUNT", 0.0)
        val category = intent.getStringExtra("TRANSACTION_CATEGORY")
        val date = intent.getLongExtra("TRANSACTION_DATE", System.currentTimeMillis())
        val type = intent.getStringExtra("TRANSACTION_TYPE")
        val iconResId = intent.getIntExtra("TRANSACTION_ICON", R.drawable.baseline_edit_note_24_large)

        if (transactionId != -1 && category != null && type != null) {
            transaction = TransactionEntity(transactionId, type, category, amount, date, iconResId)
            setupEditMode(transaction!!)
        }

        setupUI()
    }

    private fun setupEditMode(transaction: TransactionEntity) {
        binding.etAmount.setText(transaction.amount.toString())
        binding.etCategory.setText(transaction.category)
        selectedcategory = transaction.category
        selectedIconResId = transaction.iconResId
        selectedDate = transaction.date
        binding.btnSelectDate.text = formatDate(transaction.date)
        binding.rgType.check(if (transaction.type == "Pemasukan") R.id.rbIncome else R.id.rbOutcome)
        setupOptions(if (transaction.type == "Pemasukan") incomeOptions else expenseOptions)
        binding.btnSave.text = getString(R.string.btn_update_transaction)
    }

    private fun setupUI() {
        binding.rbOutcome.isChecked = true
        setupOptions(expenseOptions)

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbOutcome -> setupOptions(expenseOptions)
                R.id.rbIncome -> setupOptions(incomeOptions)
            }
        }

        binding.etCategory.visibility = View.GONE
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveTransaction() }
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.slide_out_down)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate

        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            selectedDate = selectedCalendar.timeInMillis
            binding.btnSelectDate.text = formatDate(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun saveTransaction() {
        val amountText = binding.etAmount.text.toString()
        val amount = amountText.toDoubleOrNull()
        val type = if (binding.rbOutcome.isChecked) "Pengeluaran" else "Pemasukan"
        val customCategory = binding.etCategory.text.toString()
        val category = if (customCategory.isNotBlank()) customCategory else selectedcategory

        if (amount != null && category != null) {
            if (transaction == null) {
                val newTransaction = TransactionEntity(0, type, category, amount, selectedDate, selectedIconResId)
                transactionViewModel.insert(newTransaction)
                Toast.makeText(this, getString(R.string.transaction_saved), Toast.LENGTH_SHORT).show()
            } else {
                val updatedTransaction = transaction!!.copy(
                    type = type,
                    category = category,
                    amount = amount,
                    date = selectedDate,
                    iconResId = selectedIconResId
                )
                transactionViewModel.update(updatedTransaction)
                Toast.makeText(this, getString(R.string.transaction_updated), Toast.LENGTH_SHORT).show()
            }
            finish()
            overridePendingTransition(0, R.anim.slide_out_down)
        } else {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOptions(options: List<Option>) {
        binding.rvOptions.layoutManager = GridLayoutManager(this, 4)
        binding.rvOptions.adapter = OptionAdapter(options) { selectedOption ->
            if (selectedOption.label == "Lainnya") {
                binding.etCategory.visibility = View.VISIBLE
                binding.etCategory.requestFocus()
                selectedcategory = null
                selectedIconResId = R.drawable.baseline_edit_note_24_large
            } else {
                binding.etCategory.visibility = View.GONE
                selectedcategory = selectedOption.label
                selectedIconResId = selectedOption.icon
                binding.etCategory.setText("")
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private val expenseOptions = listOf(
        Option(R.drawable.makan, "Makan"),
        Option(R.drawable.motorcycle, "Transportasi"),
        Option(R.drawable.house, "Sewa Kos"),
        Option(R.drawable.internet, "Internet"),
        Option(R.drawable.college, "Kebutuhan Kuliah"),
        Option(R.drawable.games, "Hiburan"),
        Option(R.drawable.shopping, "Belanja"),
        Option(R.drawable.health, "Kesehatan"),
        Option(R.drawable.sport, "Olahraga"),
        Option(R.drawable.coffee, "Jajan / Ngopi"),
        Option(R.drawable.personal, "Keperluan Pribadi"),
        Option(R.drawable.electronic, "Peralatan Elektronik"),
        Option(R.drawable.charity, "Donasi / Amal"),
        Option(R.drawable.event, "Acara / Kegiatan Kampus"),
        Option(R.drawable.holiday, "Liburan"),
        Option(R.drawable.baseline_edit_note_24_large, "Lainnya")
    )

    private val incomeOptions = listOf(
        Option(R.drawable.parent, "Orang Tua"),
        Option(R.drawable.scholarship, "Beasiswa"),
        Option(R.drawable.salary, "Gaji Part-Time"),
        Option(R.drawable.internship, "Magang"),
        Option(R.drawable.freelance, "Freelance"),
        Option(R.drawable.jualonline, "Jualan Online"),
        Option(R.drawable.prize, "Hadiah Lomba"),
        Option(R.drawable.loan, "Pinjaman dari Teman/Keluarga"),
        Option(R.drawable.secondhand, "Jual Barang Bekas"),
        Option(R.drawable.amplop, "Uang Kaget"),
        Option(R.drawable.cash, "Bayaran pinjaman"),
        Option(R.drawable.baseline_edit_note_24_large, "Lainnya")
    )
}
