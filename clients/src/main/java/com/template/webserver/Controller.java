package com.template.webserver;

import com.template.flows.ShipmentFlow;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return "Address....";
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = TEXT_PLAIN_VALUE)
    private String peers() {
        return proxy.networkMapSnapshot().stream()
                .map(it -> it.getLegalIdentities().toString())
                .collect(Collectors.toList()).toString();
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }

    @PostMapping(value = "createShipment", produces =  TEXT_PLAIN_VALUE, headers = "Content-Type = application/x-www-form-urlencoded")
    public ResponseEntity<String> issueShipmentFlow(HttpServletRequest request) throws IllegalArgumentException {
       try{
           String model = request.getParameter("");
           String owner = request.getParameter("O=Google,L=London,C=GB");

               // Start the IOUIssueFlow. We block and waits for the flow to return.
               SignedTransaction result = proxy.startTrackedFlowDynamic(ShipmentFlow.class, model, owner).getReturnValue().get();
               // Return the response.
               return ResponseEntity
                       .status(HttpStatus.CREATED)
                       .body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
               // For the purposes of this demo app, we do not differentiate by exception type.
           } catch (Exception e) {
               return ResponseEntity
                       .status(HttpStatus.BAD_REQUEST)
                       .body(e.getMessage());
           }
    }
}