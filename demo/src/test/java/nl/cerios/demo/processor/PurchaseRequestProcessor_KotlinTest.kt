package nl.cerios.demo.processor

import kotlinx.coroutines.experimental.runBlocking
import nl.cerios.demo.http.HttpRequestData
import nl.cerios.demo.service.ValidationException
import org.junit.Assert
import org.junit.Before

class PurchaseRequestProcessorTestKotlin : PurchaseRequestProcessorTestBaseKotlin() {
    @Before
    @Throws(Exception::class)
    fun setUp() {
        addPurchaseRequest(10, 10, 10)
    }

    // @Test
    suspend fun testHandle() {
        addPurchaseRequest(10, 10, 10)
        val requestData = HttpRequestData()
        requestData.purchaseRequestId = 10

        val purchaseResponse = PurchaseRequestProcessorKotlin().process(requestData)
        println(purchaseResponse)
        Assert.assertEquals(Integer.valueOf(90), purchaseResponse.purchaseRequest.orderId)

    }

    suspend fun testHandle2() {
        addPurchaseRequest(13, 13, 15)
        val requestData = HttpRequestData()
        requestData.purchaseRequestId = 13
        try {


            val purchaseResponse = PurchaseRequestProcessorKotlin().process(requestData)

            throw Exception("test failed")
        } catch (cv: ValidationException) {
            Assert.assertEquals("Customer validation failed", cv.message)
        }

    }

}

fun main(args: Array<String>) = runBlocking<Unit>
{
    val test = PurchaseRequestProcessorTestKotlin()
    test.testHandle()
    test.testHandle2()


}