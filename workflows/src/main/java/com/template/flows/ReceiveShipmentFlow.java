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

@InitiatedBy(ShipmentFlow.class)
public class ReceiveShipmentFlow extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;
    public ReceiveShipmentFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Received Shipment");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));
    }
}