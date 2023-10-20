package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(TokenIssueAccountFlowInitiator.class)
public class TokenIssueAccountFlowResponder extends FlowLogic<Void> {
    private final FlowSession otherPartySession;
    public TokenIssueAccountFlowResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        System.out.println("Token issueresponder");
        subFlow(new SignTransactionFlow(otherPartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {

            }
        });
        subFlow(new ReceiveFinalityFlow(otherPartySession));
        return null;
    }
}