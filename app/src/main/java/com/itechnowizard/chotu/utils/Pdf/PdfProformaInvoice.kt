package com.itechnowizard.chotu.utils.Pdf

import android.content.Context
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Annas Surdyanto on 05/02/22.
 *
 */
class PdfProformaInvoice {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter


    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        //Open the document
        document.open()
    }


    fun createUserTable(
        context: Context,
        invoice: InvoiceModel,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val file = PdfHelper.createFile(context, "proforma_invoice_${invoice.invoiceDate}_${invoice.invoiceNumber}")
        val document = PdfHelper.createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        //Add Supplier Dat
        val table = addHeader(invoice.supplierDetail!!,invoice.invoiceNumber!!, invoice.invoiceDate!!)
        document.add(table)

        PdfHelper.addLineSpace(document, 1)

        val para = Paragraph("Proforma Invoice")
        para.alignment = Element.ALIGN_CENTER
        document.add(para)

        PdfHelper.addLineSpace(document, 1)

        val sellerDetailTable = PdfHelper.addBuyerAndConsignee(invoice.buyerDetail!!,invoice.consigneeDetailType!!,invoice.consigneeModel!!)
        document.add(sellerDetailTable)

        PdfHelper.addLineSpace(document,1)

        val productTable = PdfHelper.createProductTable(productDetails = invoice.productDetails!!,invoice.billFinalAmount!!)
        document.add(productTable)

        val chargeTable = PdfHelper.createChargeTable(
            otherDetailModel = invoice.otherDetails!!,
            totalTax = invoice.totalTax,
            taxableAmount = invoice.taxableAmount,
            billFinalAmount = invoice.totalAmount!!
        )
        document.add(chargeTable)

        val totalAmountTable = PdfHelper.createFinalAmountTable(invoice.taxableAmount!!,invoice.gst!!,
            invoice.cess,invoice.totalTax!!,invoice.billFinalAmount!!
        )
        document.add(totalAmountTable)

        PdfHelper.addLineSpace(document, 2)

        val tcAndForTable = PdfHelper.createTcAndForTable(invoice.termsAndCondition!!,
            invoice.supplierDetail!!.firmName!!,invoice.imageUrl!!)
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

    private fun addHeader(supplierDetail: SupplierModel, invoiceNumber: String, invoiceDate: String): PdfPTable {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        val paragraph = Paragraph( "${supplierDetail.firmName}" + "\n"+
            "${supplierDetail.address ?: ""}, " +
                "${supplierDetail.city ?: ""} " +
                "${supplierDetail.state ?: ""} " +
                "${supplierDetail.pincode ?: ""} "+ "\n" +
                "${supplierDetail.mobile ?: ""}," +
                "${supplierDetail.email ?: ""}," + "\n" +
                "${supplierDetail.panCard}"
        )

        val paragraph2 = Paragraph("Invoice Number : #$invoiceNumber \nInvoice Date : $invoiceDate")
        table.addCell(PdfHelper.createBorderLessCell(paragraph.content,Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(paragraph2.content,Element.ALIGN_RIGHT))


        return table

    }
}