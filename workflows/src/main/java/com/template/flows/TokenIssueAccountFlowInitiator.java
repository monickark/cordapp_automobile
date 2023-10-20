package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TokenContract;
import com.template.states.TokenAccountState;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class TokenIssueAccountFlowInitiator extends FlowLogic<String> {
    private final String issuer;
    private final String owner;
    private final int amount;

    public TokenIssueAccountFlowInitiator(String issuer, String owner, int amount) {
        this.issuer = issuer;
        this.owner = owner;
        this.amount = amount;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        //Generate AccountInfo & AnonymousParty object for transaction
        AccountInfo issuerAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(issuer).get(0).getState().getData();
        AccountInfo ownerAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(owner).get(0).getState().getData();

        AnonymousParty issuerAccount = subFlow(new RequestKeyForAccount(issuerAccountInfo));
        AnonymousParty ownerAccount = subFlow(new RequestKeyForAccount(ownerAccountInfo));

        FlowSession ownerSession = initiateFlow(ownerAccountInfo.getHost());

        //grab the notary for transaction building
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //create a transaction builder
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        TokenAccountState tokenState = new TokenAccountState(issuerAccount, ownerAccount, amount);

        transactionBuilder.addOutputState(tokenState);
        transactionBuilder.addCommand(new TokenContract.Commands.Issue(),
                ImmutableList.of(issuerAccount.getOwningKey(), ownerAccount.getOwningKey()));

        transactionBuilder.verify(getServiceHub());

        //Sign the txion with the issuer account hosted on the initiating node
        SignedTransaction selfSignedTransaction = getServiceHub().signInitialTransaction(transactionBuilder, issuerAccount.getOwningKey());

        //call CollectSignatureFlows to get the signature from the owner by specify with the issuer key telling collectsigatureflows that issuer has already
        final SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(selfSignedTransaction, Arrays.asList(ownerSession), Collections.singleton(issuerAccount.getOwningKey())));

        //call FinalityFlow for finality
        SignedTransaction stx = subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(ownerSession)));

        return "One token state issued to " + owner + " from "+ issuer + " with amount: " + amount + "\ntxId: " + stx.getId();
    }
}