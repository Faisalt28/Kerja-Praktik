package com.praktikerja.artoz.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.praktikerja.artoz.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setChipClick(view, R.id.chip_money_icon, "https://idn.freepik.com/ikon/money_159933")
        setChipClick(view, R.id.chip_payment_method, "https://www.svgrepo.com/svg/113541/payment-method")
        setChipClick(view, R.id.chip_food, "https://www.svgrepo.com/svg/317513/food")
        setChipClick(view, R.id.chip_motorcycle, "https://www.freepik.com/icon/motorcycle_1023346")
        setChipClick(view, R.id.chip_house, "https://idn.freepik.com/ikon/house_14316843")
        setChipClick(view, R.id.chip_internet_globe, "https://www.svgrepo.com/svg/302047/internet-earth-globe")
        setChipClick(view, R.id.chip_student, "https://www.svgrepo.com/svg/406481/man-student")
        setChipClick(view, R.id.chip_arcade, "https://www.svgrepo.com/svg/475521/arcade")
        setChipClick(view, R.id.chip_shopping_bags, "https://www.svgrepo.com/svg/398294/shopping-bags")
        setChipClick(view, R.id.chip_health_clinic, "https://www.svgrepo.com/svg/270131/health-clinic-pharmacy")
        setChipClick(view, R.id.chip_weightlifting, "https://www.flaticon.com/free-icon/weightlifting_363338")
        setChipClick(view, R.id.chip_coffee, "https://www.svgrepo.com/svg/530366/coffee")
        setChipClick(view, R.id.chip_id_card, "https://www.svgrepo.com/svg/475624/id-card")
        setChipClick(view, R.id.chip_laptop, "https://www.svgrepo.com/svg/286183/laptop")
        setChipClick(view, R.id.chip_charity, "https://www.svgrepo.com/svg/270104/loving-charity")
        setChipClick(view, R.id.chip_tickets, "https://www.svgrepo.com/svg/240496/tickets-ticket")
        setChipClick(view, R.id.chip_camping, "https://www.svgrepo.com/svg/429241/camp-camping-holiday")
        setChipClick(view, R.id.chip_family, "https://creazilla.com/id/media/emoji/42836/pria-wanita-anak-perempuan-anak-laki-laki-keluarga")
        setChipClick(view, R.id.chip_scholarship, "https://www.flaticon.com/free-icon/scholarship_5609483")
        setChipClick(view, R.id.chip_salesman, "https://www.svgrepo.com/svg/302116/salesman-salesman")
        setChipClick(view, R.id.chip_internship, "https://www.flaticon.com/free-icon/internship_1945674")
        setChipClick(view, R.id.chip_writer, "https://www.svgrepo.com/svg/274880/writer")
        setChipClick(view, R.id.chip_ecommerce, "https://www.svgrepo.com/svg/425163/online-store-ecommerce")
        setChipClick(view, R.id.chip_medal, "https://www.svgrepo.com/svg/381283/medal-award-winner-prize-badge")
        setChipClick(view, R.id.chip_loan, "https://www.svgrepo.com/svg/163357/loan")
        setChipClick(view, R.id.chip_red_envelope, "https://www.svgrepo.com/svg/474414/red-envelope")
        setChipClick(view, R.id.chip_cash, "https://www.svgrepo.com/svg/152297/cash")
    }

    private fun setChipClick(view: View, chipId: Int, url: String) {
        view.findViewById<Chip>(chipId)?.setOnClickListener {
            openLink(requireContext(), url)
        }
    }

    private fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
