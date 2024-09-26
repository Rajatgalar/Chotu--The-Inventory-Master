package com.itechnowizard.chotu.utils.Pdf

import android.content.Context
import com.itechnowizard.chotu.domain.model.PurchaseModel
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class PdfPurchase {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        document.open()
    }


    fun createUserTable(
        context: Context,
        purchase: PurchaseModel,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val file = PdfHelper.createFile(context, "purchase_${purchase.purchaseDate}_${purchase.purchaseNumber}")
        val document = PdfHelper.createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        //Add Supplier Data
        val paragraphs = PdfHelper.getSupplierDetailsParagraphs(purchase.supplierDetail!!,Element.ALIGN_CENTER,TITLE_FONT)
        paragraphs.forEach { element -> document.add(element) }
        PdfHelper.addLineSpace(document, 2)

        val headerTable = addSeller(purchase.sellerDetail!!.companyName!!,purchase.sellerDetail.gstin!!,
            purchase.sellerDetail.address!!,purchase.purchaseNumber!!,purchase.otherDetails!!.reverseCharge!!,purchase.purchaseDate!!,
            purchase.transportDetails!!.dateOfSupply!!)
        document.add(headerTable)
        PdfHelper.addLineSpace(document, 2)

        val productTable = PdfHelper.createProductTable(productDetails = purchase.productDetails!!,purchase.billFinalAmount!!)
        document.add(productTable)

        val chargeTable = PdfHelper.createChargeTable(otherDetailModel = purchase.otherDetails!!, purchase.totalTax,purchase.taxableAmount,
            purchase.totalAmount!!
        )
        document.add(chargeTable)

        val totalAmountTable = PdfHelper.createFinalAmountTable(purchase.taxableAmount!!,purchase.gst!!,
            purchase.cess,purchase.totalTax!!,purchase.billFinalAmount!!
        )
        document.add(totalAmountTable)

        PdfHelper.addLineSpace(document, 2)

        val tcAndForTable = PdfHelper.createTcAndForTable(purchase.termsAndCondition!!,
        purchase.supplierDetail!!.firmName!!,purchase.imageUrl!!)
        document.add(tcAndForTable)

        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }

    private fun addSeller(companyName: String, gstin: String, address: String,
                          purchaseNumber: String, reverseCharge: String, purchaseDate: String,
                          dateOfSupply: String
    ): PdfPTable {
            val table = PdfPTable(2)
            table.widthPercentage = 100f
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        table.addCell(PdfHelper.createBorderLessCell("Name : $companyName",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell("GSTIN : $gstin",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Address : $address",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell("Purchase Invoice Number : ${purchaseNumber.ifBlank { "1" }}",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Reverse Charge : ${reverseCharge.ifBlank { "No" }}",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell("Date : $purchaseDate",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell("Supply Date : ${dateOfSupply.ifBlank { "" }}",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell("    ",Element.ALIGN_LEFT))
            return table
        }


}