package com.itechnowizard.chotu.presentation.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityDashboardBinding
import com.itechnowizard.chotu.presentation.auth.LoginActivity
import com.itechnowizard.chotu.presentation.creditnote.CreditNote
import com.itechnowizard.chotu.presentation.debitnote.DebitNote
import com.itechnowizard.chotu.presentation.expense.Expense
import com.itechnowizard.chotu.presentation.expiry.Expiry
import com.itechnowizard.chotu.presentation.inventory.Inventory
import com.itechnowizard.chotu.presentation.invoice.Invoice
import com.itechnowizard.chotu.presentation.invoice.addinvoice.AddInvoiceActivity
import com.itechnowizard.chotu.presentation.ledger.Ledger
import com.itechnowizard.chotu.presentation.lists.buyer.Buyer
import com.itechnowizard.chotu.presentation.lists.product.Product
import com.itechnowizard.chotu.presentation.lists.seller.Seller
import com.itechnowizard.chotu.presentation.payment.made.PaymentMade
import com.itechnowizard.chotu.presentation.payment.receipt.PaymentReceipt
import com.itechnowizard.chotu.presentation.proforma.ProformaInvoice
import com.itechnowizard.chotu.presentation.purchase.Purchase
import com.itechnowizard.chotu.presentation.quotation.Quotation
import com.itechnowizard.chotu.presentation.report.Report
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra(Constants.NEW_USER, false)) {
            showNameDialog()
        }
        initNavigationView()

        binding.apply {

            btnExpense.setOnClickListener { startNewActivity(Expense::class.java) }

            btnInvoice.setOnClickListener { startNewActivity(Invoice::class.java) }

            btnPerforma.setOnClickListener { startNewActivity(ProformaInvoice::class.java) }

            btnPurchase.setOnClickListener { startNewActivity(Purchase::class.java) }

            btnPaymentReceipt.setOnClickListener { startNewActivity(PaymentReceipt::class.java) }

            btnQuotation.setOnClickListener { startNewActivity(Quotation::class.java) }

            btnExpiry.setOnClickListener { startNewActivity(Expiry::class.java) }

            btnDashboard.setOnClickListener { startNewActivity(Report::class.java) }

            btnPaymentMade.setOnClickListener { startNewActivity(PaymentMade::class.java) }

            btnDebitNote.setOnClickListener { startNewActivity(DebitNote::class.java) }

            btnCreditNote.setOnClickListener { startNewActivity(CreditNote::class.java) }

            btnLedger.setOnClickListener { startNewActivity(Ledger::class.java) }

            btnInventory.setOnClickListener { startNewActivity(Inventory::class.java) }

            btnGstfiling.setOnClickListener { Toast.makeText(this@DashboardActivity,"Coming Soon",Toast.LENGTH_SHORT).show() }

            toolbarLayout.toolbarMenuImage.setOnClickListener { startNewActivity(AddInvoiceActivity::class.java) }
        }
    }

    private fun startNewActivity(activityClass: Class<out Activity>) {
        startActivity(Intent(this@DashboardActivity, activityClass))
    }

    private fun showNameDialog() {
//        val builder = AlertDialog.Builder(this)
//        val inflater = LayoutInflater.from(this)
//        val dialogView = inflater.inflate(R.layout.name_dialog, null)
//        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)
//
//        builder.setView(dialogView)
//            .setTitle("Enter your name")
//            .setPositiveButton("OK") { dialog, _ ->
//                val name = nameEditText.text.toString()
//                saveNameToDatabase(name)
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                // User cancelled the dialog
//                dialog.dismiss()
//            }
//
//        val dialog = builder.create()
//        dialog.show()
    }

    private fun saveNameToDatabase(name: String) {
        dashboardViewModel.saveNameToFirebase(name)

        dashboardViewModel.isNameSaved.observe(this, Observer {
//            if (it)
//                Toast.makeText(this, "Name Saved", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(this, "Name Not Saved", Toast.LENGTH_SHORT).show()
        })
    }

    private fun initNavigationView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            com.itechnowizard.chotu.R.string.nav_open, com.itechnowizard.chotu.R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
       // supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_nav) // replace ic_navigation_icon with your custom icon
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarLayout.toolbarMenuImage.visibility = View.VISIBLE

        ToolbarUtils.setToolbar(
            binding.toolbarLayout,
            false,
            Constants.TOOLBAR_APP_NAME,
            Constants.TOOLBAR_NO_MENU_TEXT
        )

        binding.navigationView.itemIconTintList = null

        binding.navigationView.setNavigationItemSelectedListener { it->
            when(it.itemId){
                 R.id.nav_productList->{
                     openActivity(Product::class.java)
                    true
                }
                R.id.nav_buyerList ->{
                    openActivity(Buyer::class.java)
                    true
                }
                R.id.nav_sellerList ->{
                    openActivity(Seller::class.java)
                    true
                }
                R.id.nav_share ->{
                    val myIntent = Intent(Intent.ACTION_SEND)
                    myIntent.type = "text/plain"
                    val message =
                        "Here's a best app for creating Invoices and manage your expense !! Download on https://play.google.com/store/apps/details?id=" + this.packageName
                    myIntent.putExtra(Intent.EXTRA_TEXT, message)
                    startActivity(Intent.createChooser(myIntent, "Share"))
                    true
                }
                R.id.nav_rateus ->{
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + this.packageName)
                        )
                    )
                    true
                }
                R.id.logout ->{
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            binding.drawerLayout.closeDrawer(GravityCompat.START)
                        }
                        .setNegativeButton("No", null)
                        .show()

                    true
                }
                else -> false
            }
        }

    }

    private fun openActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.putExtra(Constants.RETURN_RESULT, false)
        startActivity(intent)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }
}

////Errors
/*
        When changing buyer from invoice, it should be change from different places tooo
 */