package com.itechnowizard.chotu.utils.Pdf

import android.content.Context
import android.os.AsyncTask
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.utils.NumToWordConvertor
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import java.io.File
import java.net.URL

object PdfHelper {

    private val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)

    fun createDocument(): Document {
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

     fun createFile(context: Context, name: String?): File {
        //Prepare file
        val title = "$name.pdf"
        val file = File(context.cacheDir, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    fun addLineSpace(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    //Usually used for Creating the Heading of Inovicesss
    private fun createParagraph(content: String,font: Font,alignment: Int): Paragraph {
        val paragraph = Paragraph(content, font)
        //paragraph.firstLineIndent = 25f
        paragraph.alignment = alignment
        return paragraph
    }

    fun getSupplierDetailsParagraphs(supplierDetail: SupplierModel,alignment: Int,font: Font): List<Element> {
        val elements = mutableListOf<Element>()
        elements.add(createParagraph(supplierDetail.firmName!!,font,alignment))
        elements.add(
            createParagraph(
                "${supplierDetail.address ?: ""} " +
                        "${supplierDetail.city ?: ""} " +
                        "${supplierDetail.state ?: ""} " +
                        "${supplierDetail.pincode ?: ""} "
            ,font,alignment
            )
        )
        if (supplierDetail.mobile!!.isNotBlank()) {
            elements.add(
                createParagraph(
                    ("${supplierDetail.mobile ?: ""} ") +
                            (supplierDetail.email ?: " ")
                ,font,alignment
                )
            )
        }
        if (supplierDetail.panCard!!.isNotBlank()) {
            elements.add(
                createParagraph(
                    "${supplierDetail.panCard}",font,alignment
                )
            )
        }
        return elements
    }

    fun createProductTable(productDetails : List<SelectedProductModel>, billFinalAmount: Double): PdfPTable {

        val table = setProductHeader()

        val numRows = kotlin.math.max(5, productDetails.size)
        for (i in 0 until numRows) {
            if (i < (productDetails.size ?: 0)) {
                table.addCell(createCell(productDetails[i].itemName.toString()))
                table.addCell(createCell(productDetails[i].quantity.toString()))
                table.addCell(createCell(productDetails[i].discount.toString()))
                table.addCell(createCell(productDetails[i].unitPrice.toString()))
                table.addCell(createCell(productDetails[i].taxableAmount.toString()))
                table.addCell(createCell(productDetails[i].gstValue.toString()))
                val regex = "(\\d+(\\.\\d+)?%)".toRegex()
                val matchResult = regex.find(productDetails[i].gstValue.toString())
                var gstValue = "0"
                if (matchResult != null) {
                    val matchText = matchResult.value.replace("%", "")
                    gstValue = "" + matchText.toDouble()
                }
                table.addCell(createCell(gstValue))
                table.addCell(createCell(productDetails[i].cessValue.toString()))
                var cessValue = "0"
                cessValue = if (productDetails[i].cessType.contentEquals("(% Percent Wise)"))
                    productDetails[i].cess.toString() + "%"
                else
                    "${productDetails[i].cess}"
                table.addCell(createCell(cessValue))
                table.addCell(createCell(productDetails[i].totalAmount.toString()))
            } else {
                // Add blank cells for the remaining rows
                table.addCell(createCell(" "))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
                table.addCell(createCell(""))
            }
        }


        return table
    }

    private fun setProductHeader(): PdfPTable {
        //Define Table
        val productNameWidth = 18f
        val qtWidth = 8f
        val discountWidth = 10f
        val priceWidth = 10f
        val taxAmountWidth = 10f
        val gstWidth = 10f
        val gstRateWidth = 7f
        val cessWidth = 10f
        val cessRateWidth = 7f
        val totalWidth = 10f
        val columnWidth = floatArrayOf(
            productNameWidth,
            qtWidth,
            discountWidth,
            priceWidth,
            taxAmountWidth,
            gstWidth,
            gstRateWidth,
            cessWidth,
            cessRateWidth,
            totalWidth
        )
        val table = setProductTableProperty(10, columnWidth)
        //Table header (first row)
        val tableHeaderContent = listOf("Product Name", "Qt", "Discount", " Price", "Taxable Amount",
            "GST", "Rate", "Cess", "Rate", "Total"
        )
        tableHeaderContent.forEach {
            val cell = createCell(it)
            table.addCell(cell)
        }
        return table
    }

    private fun setProductTableProperty(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

   fun createChargeTable(
       otherDetailModel: OtherDetailModel,
       totalTax: String?,
       taxableAmount: String?,
       billFinalAmount: String
   ) : PdfPTable{
       //Define Charge Table
       //Define Table
       val chargeNameWidth = 46f
       val chargeTaxWidth = 10f
       val chargeGstWidth = 34f
       val chargeTotalWidth = 10f
       val chargeColumnWidth = floatArrayOf(
           chargeNameWidth,
           chargeTaxWidth,
           chargeGstWidth,
           chargeTotalWidth
       )
       val chargeTable = setChargeTableProperty(4, chargeColumnWidth)
       var totalTaxableAmount = taxableAmount!!.toDouble()
       var totalTaxAmount = totalTax!!.toDouble()
       var totalAmount = billFinalAmount.toDouble()

       for (i in 0 until 5){

           when(i){
               0 ->{
                   if(otherDetailModel.freightChargeAmount!!.isNotEmpty()){
                       chargeTable.addCell(createCell("Freight Charge"))
                       chargeTable.addCell(createCell(otherDetailModel.freightCharge.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.freightChargeGst.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.freightChargeAmount.toString().ifBlank { "0" }))

                       totalTaxableAmount += otherDetailModel.freightCharge.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalTaxAmount += otherDetailModel.freightChargeGst.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalAmount += otherDetailModel.freightChargeAmount.toString().ifBlank { 0.0 }.toString().toDouble()
                   }
               }
               1->{
                   if(otherDetailModel.insuranceChargeAmount!!.isNotEmpty()){
                       chargeTable.addCell(createCell("Insurance Charge"))
                       chargeTable.addCell(createCell(otherDetailModel.insuranceCharge.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.insuranceChargeGst.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.insuranceChargeAmount.toString().ifBlank { "0" }))

                       totalTaxableAmount += otherDetailModel.insuranceCharge.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalTaxAmount += otherDetailModel.insuranceChargeGst.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalAmount += otherDetailModel.insuranceChargeAmount.toString().ifBlank { 0.0 }.toString().toDouble()
                   }
               }
               2 ->{
                   if(otherDetailModel.loadingChargeAmount!!.isNotEmpty()){
                       chargeTable.addCell(createCell("Loading Charge"))
                       chargeTable.addCell(createCell(otherDetailModel.loadingCharge.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.loadingChargeGst.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.loadingChargeAmount.toString().ifBlank { "0" }))

                       totalTaxableAmount += otherDetailModel.loadingCharge.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalTaxAmount += otherDetailModel.loadingChargeGst.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalAmount += otherDetailModel.loadingChargeAmount.toString().ifBlank { 0.0 }.toString().toDouble()
                   }
               }
               3 ->{
                   if(otherDetailModel.packagingChargeAmount!!.isNotEmpty()){
                       chargeTable.addCell(createCell("Packaging Charge"))
                       chargeTable.addCell(createCell(otherDetailModel.packagingCharge.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.packagingChargeGst.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.packagingChargeAmount.toString().ifBlank { "0" }))

                       totalTaxableAmount += otherDetailModel.packagingCharge.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalTaxAmount += otherDetailModel.packagingChargeGst.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalAmount += otherDetailModel.packagingChargeAmount.toString().ifBlank { 0.0 }.toString().toDouble()
                   }
               }
               4 ->{
                   if(otherDetailModel.otherChargeAmount!!.isNotEmpty()){
                       chargeTable.addCell(createCell(otherDetailModel.otherChargeName.toString().ifBlank {"Freight Charge"}))
                       chargeTable.addCell(createCell(otherDetailModel.otherCharge.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.otherChargeGst.toString().ifBlank { "0" }))
                       chargeTable.addCell(createCell(otherDetailModel.otherChargeAmount.toString().ifBlank { "0" }))

                       totalTaxableAmount += otherDetailModel.otherCharge.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalTaxAmount += otherDetailModel.otherChargeGst.toString().ifBlank { 0.0 }.toString().toDouble()
                       totalAmount += otherDetailModel.otherChargeAmount.toString().ifBlank { 0.0 }.toString().toDouble()
                   }
               }
           }
       }

       chargeTable.addCell(createCell("Total",1,1,PdfPCell.BOX))
       chargeTable.addCell(createCell(totalTaxableAmount.toString(),1,1,PdfPCell.BOX))
       chargeTable.addCell(createCell(totalTaxAmount.toString(),1,1,PdfPCell.BOX))
       chargeTable.addCell(createCell(totalAmount.toString().toString(),1,1,PdfPCell.BOX))


       return chargeTable

   }

    fun addBuyerAndConsignee(
        buyerDetail: BuyerModel,
        consigneeDetailType: String,
        consigneeModel: ConsigneeModel
    ) : PdfPTable {
        val table = PdfPTable(4)
        table.widthPercentage = 100f
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        val billingAddress = Paragraph( "${buyerDetail.companyName}" +
                (if (!buyerDetail.address.isNullOrBlank()) "\n${buyerDetail.address}," else "") +
                (if (!buyerDetail.city.isNullOrBlank()) "\n${buyerDetail.city}," else "") +
                (if (!buyerDetail.state.isNullOrBlank()) " ${buyerDetail.state}" else "") +
                (if (!buyerDetail.pincode.isNullOrBlank()) " ${buyerDetail.pincode}" else "") +
                (if (!buyerDetail.mobile.isNullOrBlank()) "\n${buyerDetail.mobile}," else "") +
                (if (!buyerDetail.email.isNullOrBlank()) "\n${buyerDetail.email}" else "") +
                (if (!buyerDetail.gstin.isNullOrBlank()) "\n${buyerDetail.gstin}" else ""))

        table.addCell(createBorderLessCell("Billing Address : ",Element.ALIGN_RIGHT))
        table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))

        when(consigneeDetailType){
            "Show Consignee (Same as above)" -> {
                table.addCell(createBorderLessCell("Shipping Address : ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))
            }
            "Consignee Not Required" -> {
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
            }
            "Add Consignee (If different from above)" -> {
                val shippingAddress = Paragraph("${consigneeModel.companyName}" +
                        (if (!consigneeModel.address.isNullOrBlank()) "\n${consigneeModel.address}," else "") +
                        (if (!consigneeModel.city.isNullOrBlank()) "\n${consigneeModel.city}" else "") +
                        (if (!consigneeModel.state.isNullOrBlank()) " ${consigneeModel.state}" else "") +
                        (if (!consigneeModel.pincode.isNullOrBlank()) " ${consigneeModel.pincode}" else "") +
                        (if (!consigneeModel.mobile.isNullOrBlank()) "\n${consigneeModel.mobile}" else "") +
                        (if (!consigneeModel.email.isNullOrBlank()) "\n${consigneeModel.email}" else "") +
                        (if (!consigneeModel.gstin.isNullOrBlank()) "\n${consigneeModel.gstin}" else ""))

                table.addCell(createBorderLessCell("Shipping Address : ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(shippingAddress.content,Element.ALIGN_LEFT))
            }
        }

        return table
    }

    fun addSellerAndConsignee(
        buyerDetail: SellerModel,
        consigneeDetailType: String,
        consigneeModel: ConsigneeModel
    ) : PdfPTable {
        val table = PdfPTable(4)
        table.widthPercentage = 100f
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        val billingAddress = Paragraph( "${buyerDetail.companyName}" +
                (if (!buyerDetail.address.isNullOrBlank()) "\n${buyerDetail.address}," else "") +
                (if (!buyerDetail.city.isNullOrBlank()) "\n${buyerDetail.city}," else "") +
                (if (!buyerDetail.state.isNullOrBlank()) " ${buyerDetail.state}" else "") +
                (if (!buyerDetail.pincode.isNullOrBlank()) " ${buyerDetail.pincode}" else "") +
                (if (!buyerDetail.mobile.isNullOrBlank()) "\n${buyerDetail.mobile}," else "") +
                (if (!buyerDetail.email.isNullOrBlank()) "\n${buyerDetail.email}" else "") +
                (if (!buyerDetail.gstin.isNullOrBlank()) "\n${buyerDetail.gstin}" else ""))

        table.addCell(createBorderLessCell("Billing Address : ",Element.ALIGN_RIGHT))
        table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))

        when(consigneeDetailType){
            "Show Consignee (Same as above)" -> {
                table.addCell(createBorderLessCell("Shipping Address : ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))
            }
            "Consignee Not Required" -> {
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
            }
            "Add Consignee (If different from above)" -> {
                val shippingAddress = Paragraph("${consigneeModel.companyName}" +
                        (if (!consigneeModel.address.isNullOrBlank()) "\n${consigneeModel.address}," else "") +
                        (if (!consigneeModel.city.isNullOrBlank()) "\n${consigneeModel.city}" else "") +
                        (if (!consigneeModel.state.isNullOrBlank()) " ${consigneeModel.state}" else "") +
                        (if (!consigneeModel.pincode.isNullOrBlank()) " ${consigneeModel.pincode}" else "") +
                        (if (!consigneeModel.mobile.isNullOrBlank()) "\n${consigneeModel.mobile}" else "") +
                        (if (!consigneeModel.email.isNullOrBlank()) "\n${consigneeModel.email}" else "") +
                        (if (!consigneeModel.gstin.isNullOrBlank()) "\n${consigneeModel.gstin}" else ""))

                table.addCell(createBorderLessCell("Shipping Address : ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(shippingAddress.content,Element.ALIGN_LEFT))
            }
        }

        return table
    }

    fun addBuyerConsigneeAndContactPerson(
        buyerDetail: BuyerModel,
        consigneeDetailType: String,
        consigneeModel: ConsigneeModel,
        contactModel: ContactModel,
        document: Document
    ) : PdfPTable {
        val table = PdfPTable(6)
        table.widthPercentage = 100f
        table.totalWidth = document.pageSize.width - document.leftMargin() - document.rightMargin()
        table.setWidths(floatArrayOf(0.3f, 0.75f, 0.3f, 0.75f, 0.25f, 0.40f))

        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        val billingAddress = Paragraph( "${buyerDetail.companyName}" +
                (if (!buyerDetail.address.isNullOrBlank()) "\n${buyerDetail.address}" else "") +
                (if (!buyerDetail.city.isNullOrBlank()) "\n${buyerDetail.city}" else "") +
                (if (!buyerDetail.state.isNullOrBlank()) " ${buyerDetail.state}" else "") +
                (if (!buyerDetail.pincode.isNullOrBlank()) " ${buyerDetail.pincode}" else "") +
                (if (!buyerDetail.mobile.isNullOrBlank()) "\n${buyerDetail.mobile}" else "") +
                (if (!buyerDetail.email.isNullOrBlank()) "\n${buyerDetail.email}" else "") +
                (if (!buyerDetail.gstin.isNullOrBlank()) "\n${buyerDetail.gstin}" else ""))

        table.addCell(createBorderLessCell("Billing Address ",Element.ALIGN_RIGHT))
        table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))

        when(consigneeDetailType){
            "Show Consignee (Same as above)" -> {
                table.addCell(createBorderLessCell("Shipping Address ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(billingAddress.content,Element.ALIGN_LEFT))
            }
            "Consignee Not Required" -> {
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
                table.addCell(createBorderLessCell(" ",Element.ALIGN_LEFT))
            }
            "Add Consignee (If different from above)" -> {
                val shippingAddress = Paragraph("${consigneeModel.companyName}" +
                        (if (!consigneeModel.address.isNullOrBlank()) "\n${consigneeModel.address}," else "") +
                        (if (!consigneeModel.city.isNullOrBlank()) "\n${consigneeModel.city}" else "") +
                        (if (!consigneeModel.state.isNullOrBlank()) " ${consigneeModel.state}" else "") +
                        (if (!consigneeModel.pincode.isNullOrBlank()) " ${consigneeModel.pincode}" else "") +
                        (if (!consigneeModel.mobile.isNullOrBlank()) "\n${consigneeModel.mobile}" else "") +
                        (if (!consigneeModel.email.isNullOrBlank()) "\n${consigneeModel.email}" else "") +
                        (if (!consigneeModel.gstin.isNullOrBlank()) "\n${consigneeModel.gstin}" else ""))


                table.addCell(createBorderLessCell("Shipping Address ",Element.ALIGN_RIGHT))
                table.addCell(createBorderLessCell(shippingAddress.content,Element.ALIGN_LEFT))
            }
        }

        val contactDetail = Paragraph( "${contactModel.name}" +
                (if (!contactModel.phone.isNullOrBlank()) "\n${contactModel.phone}," else "") +
                (if (!contactModel.email.isNullOrBlank()) "\n${contactModel.email}" else "")
        )

        table.addCell(createBorderLessCell("Contact Person ",Element.ALIGN_RIGHT))
        table.addCell(createBorderLessCell(contactDetail.content,Element.ALIGN_LEFT))

        return table
    }

    private fun setChargeTableProperty(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    fun createTcAndForTable(termsAndCondition: String, firmName: String, imageUrl: String) : PdfPTable{

        val table = PdfPTable(2)
        table.widthPercentage = 100f

// Create the left-hand side paragraph
        val leftParagraph = Paragraph(termsAndCondition)
        val leftCell =  createCell(leftParagraph,PdfPCell.NO_BORDER,Element.ALIGN_LEFT)
        table.addCell(leftCell)

// Create the right-hand side paragraph
        val rightParagraph = Paragraph("FOR, $firmName", TITLE_FONT)
        val rightCell = createCell(rightParagraph,PdfPCell.NO_BORDER,Element.ALIGN_RIGHT)
        table.addCell(rightCell)

        table.addCell(createCell(Paragraph("\n \n E & O.E.", TITLE_FONT),PdfPCell.NO_BORDER,Element.ALIGN_LEFT))

//        if(imageUrl.isEmpty())
//         table.addCell(createCell(Paragraph(""),PdfPCell.NO_BORDER,Element.ALIGN_RIGHT))
//        else{
//            val loadImageTask = LoadImageTask()
//            loadImageTask.execute(imageUrl)
//            try {
//                val image = loadImageTask.get() // Wait for the image to load
//                if (image != null) {
//                    rightCell.addElement(image)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            table.addCell(rightCell)
//        }

        // image url is currently set to not display anything
        table.addCell(createCell(Paragraph(""),PdfPCell.NO_BORDER,Element.ALIGN_RIGHT))

        return table
    }

    fun createFinalAmountTable(taxableAmount: String, gst: String, cess:
    String?, totalTax: String, billFinalAmount: Double): PdfPTable{
        val terms = 60f
        val charge = 20f
        val amount = 20f
        val chargeColumnWidth = floatArrayOf(terms, charge, amount)

        val table = setChargeTableProperty(3, chargeColumnWidth)

        val words = NumToWordConvertor.convertToIndianCurrency(billFinalAmount.toString())

        table.addCell(createCell("Total Invoice Amount In Words \n\n $words", 1, 5, PdfPCell.BOX));
        table.addCell(createCell("Taxable Amount", 1, 1, PdfPCell.BOX));
        table.addCell(createCell(taxableAmount, 1, 1, PdfPCell.BOX));
        table.addCell(createCell("GST", 1, 1, PdfPCell.BOX));
        table.addCell(createCell(gst, 1, 1, PdfPCell.BOX));
        table.addCell(createCell("CESS ", 1, 1, PdfPCell.BOX));
        table.addCell(createCell(cess, 1, 1, PdfPCell.BOX));
        table.addCell(createCell("Total Tax", 1, 1, PdfPCell.BOX));
        table.addCell(createCell(totalTax, 1, 1, PdfPCell.BOX));
        table.addCell(createCell("Total Amount", 1, 1, PdfPCell.BOX));
        table.addCell(createCell(billFinalAmount.toString(), 1, 1, PdfPCell.BOX));

        return table

    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun createCell(content: String?, colspan: Int, rowspan: Int, border: Int): PdfPCell? {
        val cell = PdfPCell(Phrase(content))
        cell.colspan = colspan
        cell.rowspan = rowspan
        cell.border = border
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

     private fun createCell(content: Paragraph?, border: Int, alignment: Int): PdfPCell {
        val cell = PdfPCell(content)
        cell.border = border
        cell.horizontalAlignment = alignment
        return cell
    }

    fun createBorderLessCell(content: String,alignment: Int): PdfPCell {
        val cell = PdfPCell(Paragraph(content))
        cell.border = Rectangle.NO_BORDER
        cell.horizontalAlignment = alignment
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

}