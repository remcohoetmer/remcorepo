package nl.cerios.demo.processor;

import nl.cerios.demo.service.CustomerService;
import nl.cerios.demo.service.LocationService_CF;
import nl.cerios.demo.service.LocationService_Rx;
import nl.cerios.demo.service.LocationService_Sync;
import nl.cerios.demo.service.MailboxHandler;
import nl.cerios.demo.service.OrderService;
import nl.cerios.demo.service.PurchaseRequestController;
import nl.cerios.demo.service.PurchaseResponse;
import nl.cerios.demo.service.TransactionService;

abstract class BaseProcessor {
	protected LocationService_CF locationService_CF = new LocationService_CF();
	protected LocationService_Sync locationService_Sync = new LocationService_Sync();
	protected LocationService_Rx locationService_Rx = new LocationService_Rx();
	protected CustomerService customerService = new CustomerService();
	protected OrderService orderService= new OrderService();
	protected TransactionService transactionService= new TransactionService();
	protected PurchaseRequestController purchaseRequestController= PurchaseRequestController.getInstance();
	protected MailboxHandler mailboxHandler= new MailboxHandler();
	
	protected String composeLinkingFailedMessage(PurchaseResponse purchaseResponse) {
		return "Linking Transaction to Order failed" + purchaseResponse.toString();
	}

}
