import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.StrictMode
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.itechnowizard.chotu.BuildConfig
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.utils.FileHandler
import com.itechnowizard.chotu.utils.Pdf.*
import java.io.File

object PdfUtils {
    fun createPdfForInvoice(
        context: Context,
        invoiceModel: InvoiceModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfInvoice()
        pdfInvoice.createUserTable(
            context = context,
            invoice = invoiceModel,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForProformaInvoice(
        context: Context,
        invoiceModel: InvoiceModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfProformaInvoice()
        pdfInvoice.createUserTable(
            context = context,
            invoice = invoiceModel,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForPaymentReceipt(
        context: Context,
        receipt: PaymentReceiptModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfPaymentReceipt()
        pdfInvoice.createUserTable(
            context = context,
            receipt = receipt,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForPaymentMade(
        context: Context,
        receipt: PaymentMadeModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfPaymentMade()
        pdfInvoice.createUserTable(
            context = context,
            receipt = receipt,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForCreditNote(
        context: Context,
        receipt: CreditNoteModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfCreditNote()
        pdfInvoice.createUserTable(
            context = context,
            invoice = receipt,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForDebitNote(
        context: Context,
        receipt: DebitNoteModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfDebitNote()
        pdfInvoice.createUserTable(
            context = context,
            invoice = receipt,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForPurchase(
        context: Context,
        invoiceModel: PurchaseModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfPurchase()
        pdfInvoice.createUserTable(
            context = context,
            purchase = invoiceModel,
            onFinish = onFinish,
            onError = onError
        )
    }

    fun createPdfForQuotation(
        context: Context,
        invoiceModel: QuotationModel,
        onFinish: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val pdfInvoice = PdfQuotation()
        pdfInvoice.createUserTable(
            context = context,
            invoice = invoiceModel,
            onFinish = onFinish,
            onError = onError
        )
    }


    fun openFile(
        context: Context,
        file: File,
        packageName: String?
    ) {

        val path =  FileHandler().getPathFromUri(context, file.toUri())
        val pdfFile = File(path)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            pdfFile
        )

        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Add the FLAG_GRANT_READ_URI_PERMISSION flag

        // Check if the user has Adobe Reader installed
        val adobeReaderPackage = "com.adobe.reader"
        val pm = context.packageManager
        val adobeReaderInstalled: Boolean = try {
            pm.getPackageInfo(adobeReaderPackage, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (adobeReaderInstalled) {
            // If Adobe Reader is installed, use it to open the PDF
            pdfIntent.setPackage(adobeReaderPackage)
        } else {
            // Otherwise, let Android choose the default PDF viewer
            pdfIntent.setPackage(null)
        }

        try {
            context.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            println("In Open File : Error1 = ${e.localizedMessage}")
            toastErrorMessage(context,"Can't read pdf file")
        } catch (e: Exception) {
            println("In Open File : Error2 = ${e.localizedMessage}")
            toastErrorMessage(context,"Error opening pdf file")
        }
    }

     fun toastErrorMessage(context: Context, s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
}
