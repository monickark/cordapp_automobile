package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CarContract;
import com.template.states.CarState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static com.template.contracts.CarContract.CAR_CONTRACT_ID;

@InitiatingFlow
@StartableByRPC
public class ShipmentFlow extends FlowLogic<SignedTransaction> {

    private String model;
    private Party owner;

    private final ProgressTracker progressTracker = new ProgressTracker();

    public ShipmentFlow(String model, Party owner) {
        this.model = model;
        this.owner = owner;
        System.out.println("Inside constructor");
    }

    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("name is equals: " + getOurIdentity().getName());
        if(getOurIdentity().getName().equals("Microsoft")) {
            System.out.println("Identity verified");
        } else {
            throw new FlowException("This is not Microsoft");
        }
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        CarState outputState = new CarState(model,owner,getOurIdentity());
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, CAR_CONTRACT_ID)
                .addCommand(new CarContract.Shipment(), getOurIdentity().getOwningKey());
        SignedTransaction shipmentTransaction = getServiceHub().signInitialTransaction(txBuilder);
        FlowSession otherPartySession =  initiateFlow(owner);
        return subFlow(new FinalityFlow(shipmentTransaction, otherPartySession));
    }
}