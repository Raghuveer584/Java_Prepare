package com.example.restservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;

@RestController
public class PayPalController {

    private final String  BASE = "https://api-m.sandbox.paypal.com";
    private final static Logger LOGGER =  Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    private String payment_id="";

    private String getAuth(String client_id, String app_secret) {
        String auth = client_id + ":" + app_secret;
        return Base64.getEncoder().encodeToString(auth.getBytes());
    }

    String clientId="AeH0qi1WjRFS95Kaz07FygMvp5J_EWN65E5DlZZFTyxQtuQNSvTHooTTv5vesD5DjXJEpbggtfLMDSp0";
	String clientSecret="EMatee9n4VecWew1cd7Mrh0_6j8O7ciONDx6yYZeNahLYZNpsXTIsLmyC0yIjhFpmBGq2gzVv2wSBdB5";
    public String generateAccessToken() {
        String auth = this.getAuth(
                "AeH0qi1WjRFS95Kaz07FygMvp5J_EWN65E5DlZZFTyxQtuQNSvTHooTTv5vesD5DjXJEpbggtfLMDSp0",
                "EMatee9n4VecWew1cd7Mrh0_6j8O7ciONDx6yYZeNahLYZNpsXTIsLmyC0yIjhFpmBGq2gzVv2wSBdB5"
        );
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + auth);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(requestBody, headers);
        requestBody.add("grant_type", "client_credentials");

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE +"/v1/oauth2/token",
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            LOGGER.log(Level.INFO, "GET TOKEN: SUCCESSFUL!");
            return new JSONObject(response.getBody()).getString("access_token");
        } else {
            LOGGER.log(Level.SEVERE, "GET TOKEN: FAILED!");
            return "Unavailable to get ACCESS TOKEN, STATUS CODE " + response.getStatusCode();
        }
    }

    @RequestMapping(value="/api/orders/{orderId}/capture", method = RequestMethod.POST)
    @CrossOrigin
    public Object capturePayment(@PathVariable("orderId") String orderId) {
        String accessToken = generateAccessToken();
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.set("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                BASE + "/v2/checkout/orders/" + orderId + "/capture",
                HttpMethod.POST,
                entity,
                Object.class
        );

      //  processCreditPaymentRequests();
        if (response.getStatusCode() == HttpStatus.CREATED) {
            LOGGER.log(Level.INFO, "ORDER CREATED");
            return response.getBody();
        } else {
            LOGGER.log(Level.INFO, "FAILED CREATING ORDER");
            return "Unavailable to get CREATE AN ORDER, STATUS CODE " + response.getStatusCode();
        }
    }
    
    @RequestMapping(value="/api/orders/capture")
    @CrossOrigin
    public void capturePayment1() {
        System.out.println("test capture");
    }

    @RequestMapping(value="/api/orders1", method = RequestMethod.POST)
    @CrossOrigin
    public Object createOrder1() throws IOException {
        String accessToken = generateAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        //JSON String
        String requestJson = "{\"intent\":\"CAPTURE\",\"purchase_units\":[{\"amount\":{\"currency_code\":\"USD\",\"value\":\"55.00\"}}]}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                BASE + "/v2/checkout/orders",
                HttpMethod.POST,
                entity,
                Object.class
        );
        
      //  createOrder();
       

        if (response.getStatusCode() == HttpStatus.CREATED) {
            LOGGER.log(Level.INFO, "ORDER CAPTURE");
            return response.getBody();
        } else {
            LOGGER.log(Level.INFO, "FAILED CAPTURING ORDER");
            return "Unavailable to get CAPTURE ORDER, STATUS CODE " + response.getStatusCode();
        }

    }
    
    @RequestMapping(value="/api/orders", method = RequestMethod.POST)
    @CrossOrigin
    public Order createOrder() throws IOException {
    	OrdersCreateRequest request = new OrdersCreateRequest();
		request.header("prefer", "return=representation");
		request.header("Content-Type", "application/json");
		request.header("Accept", "application/json");
		
		request.requestBody(buildCreateOrderRequestBody("25"));
		
		PayPalEnvironment payPalEnvironment=new PayPalEnvironment.Sandbox(clientId, clientSecret);
		PayPalHttpClient client = new PayPalHttpClient(payPalEnvironment);
		com.paypal.http.HttpResponse<Order> orderResponse = client.execute(request);
		/*
		 * Order order = null; if (orderResponse.statusCode() == 201){ order =
		 * orderResponse.result(); // set PaypalCreateOrderResponseDTO values
		 * System.out.println(order.id()); System.out.println("10");
		 * System.out.println(order.status()); } ResponseEntity<Object> response =
		 * restTemplate.exchange( BASE + "/v2/checkout/orders", HttpMethod.POST, entity,
		 * Object.class );
		 * 
		 * if (response.getStatusCode() == HttpStatus.CREATED) { LOGGER.log(Level.INFO,
		 * "ORDER CAPTURE"); return response.getBody(); } else { LOGGER.log(Level.INFO,
		 * "FAILED CAPTURING ORDER"); return
		 * "Unavailable to get CAPTURE ORDER, STATUS CODE " + response.getStatusCode();
		 * }
		 */
		Order order = null;
		order = orderResponse.result();
		return order;

    }
    
    
    private void processCreditPaymentRequests() {
		try {
			System.out.println("start processCreditPaymentRequests");
			OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(payment_id); // payPal orderId
			ordersCaptureRequest.requestBody(new OrderRequest());
			PayPalEnvironment payPalEnvironment=new PayPalEnvironment.Sandbox(clientId, clientSecret);
			PayPalHttpClient client1 = new PayPalHttpClient(payPalEnvironment);
			com.paypal.http.HttpResponse<Order> ordersCaptureResponse =client1.execute(ordersCaptureRequest); // client is PayPalClient
			if (ordersCaptureResponse.statusCode() == 201) {
				System.out.println(ordersCaptureResponse.result().purchaseUnits().get(0).payments().captures().get(0).id()); // captureId needed for refund
				System.out.println(ordersCaptureResponse.result().status());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("End : processCreditPaymentRequests ");
	}
    
  
    private static OrderRequest buildCreateOrderRequestBody(String amount) {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.checkoutPaymentIntent("CAPTURE");
		ApplicationContext applicationContext = new ApplicationContext().cancelUrl("https://www.example.com").returnUrl("https://www.example.com");
		orderRequest.applicationContext(applicationContext);
		List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<PurchaseUnitRequest>();
		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
				.amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(amount)); // amount set here
		purchaseUnitRequests.add(purchaseUnitRequest);
		orderRequest.purchaseUnits(purchaseUnitRequests);
		return orderRequest;
	}
    
    @RequestMapping(value="/api/refund/{refundID}", method = RequestMethod.POST)
    @CrossOrigin
    private void processRefundRequests(@PathVariable("refundID") String refundID) throws Exception {
		try {
			System.out.println("Refund started");
			CapturesRefundRequest capturesRefundRequest = new CapturesRefundRequest(refundID); // PayPal CaptureId
			capturesRefundRequest.prefer("return=representation");
			capturesRefundRequest.requestBody(buildRefundRequestBody());
			PayPalEnvironment payPalEnvironment=new PayPalEnvironment.Sandbox(clientId, clientSecret);
			PayPalHttpClient client1 = new PayPalHttpClient(payPalEnvironment);
			
			com.paypal.http.HttpResponse<Refund> capturesRefundResponse = client1.execute(capturesRefundRequest); // client is PayPalClient
			if (capturesRefundResponse.statusCode() == 201) {
				//response.setCardTransactionId(Long.valueOf(capturesRefundResponse.hashCode()));
				System.out.println(capturesRefundResponse.result().id());
				System.out.println(capturesRefundResponse.result().status());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	public RefundRequest buildRefundRequestBody() {
		RefundRequest refundRequest = new RefundRequest();
		Money money = new Money();
		money.currencyCode("USD");
		money.value(String.valueOf("25"));
		refundRequest.amount(money);
		return refundRequest;
	}


}
