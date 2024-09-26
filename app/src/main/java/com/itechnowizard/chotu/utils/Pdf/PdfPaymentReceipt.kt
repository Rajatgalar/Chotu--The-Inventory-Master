package com.itechnowizard.chotu.utils.Pdf

import android.content.Context
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.utils.NumToWordConvertor
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Annas Surdyanto on 05/02/22.
 *
 */
class PdfPaymentReceipt {
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
        receipt: PaymentReceiptModel,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val file = PdfHelper.createFile(context, "receipt_${receipt.paymentDate}_${receipt.receiptNumber.toString().ifBlank { "1" }}")
        val document = PdfHelper.createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        PdfHelper.addLineSpace(document, 1)
        val para = Paragraph("PAYMENT RECEIPT", TITLE_FONT)
        para.alignment = Element.ALIGN_CENTER
        document.add(para)
        PdfHelper.addLineSpace(document, 1)
        val paragraphs = PdfHelper.getSupplierDetailsParagraphs(receipt.supplierDetail!!,Element.ALIGN_CENTER,TITLE_FONT)
        paragraphs.forEach { element -> document.add(element) }
        PdfHelper.addLineSpace(document, 2)

        val headerTable = addSeller(receipt.receiptNumber!!,receipt.paymentDate!!
            , receipt.buyerName!!,receipt.paymentMode!!
            ,receipt.treatment!!,receipt.totalAmount!!)
        document.add(headerTable)
        PdfHelper.addLineSpace(document, 5)

        val signature = "Authorized Signatory"
        val signaturePara = Paragraph(signature, TITLE_FONT)
        signaturePara.alignment = Element.ALIGN_RIGHT
        signaturePara.spacingBefore = 20f
        signaturePara.spacingAfter = 10f
        document.add(signaturePara)

        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }

    private fun addSeller(
        receiptNumber: String,
        paymentDate: String,
        buyerName: String,
        paymentMode: String,
        treatment: String,
        totalAmount: Double
    ): PdfPTable {
        val table = PdfPTable(2)

        val words = NumToWordConvertor.convertToIndianCurrency(totalAmount.toString())
//        table.widthPercentage = 50f
//        table.defaultCell.verticalAlignment = Element.ALIGN_LEFT
//        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        table.addCell(PdfHelper.createBorderLessCell("Receipt No ",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $receiptNumber",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Date",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $paymentDate",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Received From",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $buyerName",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Payment Mode",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $paymentMode",Element.ALIGN_LEFT))

        table.addCell(PdfHelper.createBorderLessCell("Treatment Type",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $treatment",Element.ALIGN_LEFT))


        table.addCell(PdfHelper.createBorderLessCell("Amount",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $totalAmount",Element.ALIGN_LEFT))


        table.addCell(PdfHelper.createBorderLessCell("Amount in words",Element.ALIGN_LEFT))
        table.addCell(PdfHelper.createBorderLessCell(":  $words",Element.ALIGN_LEFT))
        return table
    }

}