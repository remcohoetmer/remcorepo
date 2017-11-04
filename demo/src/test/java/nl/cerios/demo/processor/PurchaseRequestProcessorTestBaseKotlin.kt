package nl.cerios.demo.processor

import nl.cerios.demo.http.PurchaseHttpHandler
import nl.cerios.demo.service.PurchaseRequest
import nl.cerios.demo.service.PurchaseRequestController
import nl.cerios.demo.service.PurchaseResponse
import java.util.logging.Logger

abstract class PurchaseRequestProcessorTestBaseKotlin {
    internal class PurchaseHttpHandlerStub : PurchaseHttpHandler {
        var purchaseResponse: PurchaseResponse?=null
        var message: String?=null
        override fun notifyError(throwable: Throwable) {
            message = throwable.message
            LOG.info(throwable.toString())
            //throwable.printStackTrace(System.out);
        }

        override fun notifyComplete(purchaseResponse: PurchaseResponse) {
            this.purchaseResponse = purchaseResponse
            LOG.info(purchaseResponse.toString())
        }
    }

    internal fun addPurchaseRequest(purchaseRequestId: Int?, customerId: Int?, locationId: Int?) {
        val purchaseRequest = PurchaseRequest()
        purchaseRequest.customerId = customerId
        purchaseRequest.locationId = locationId
        PurchaseRequestController.getInstance().add(purchaseRequestId, purchaseRequest)
    }

    companion object {
        private val LOG = Logger.getLogger(PurchaseRequestProcessorTestBase::class.java.name)
    }
}