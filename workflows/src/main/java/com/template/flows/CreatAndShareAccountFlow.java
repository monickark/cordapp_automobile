package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CreatAndShareAccountFlow extends FlowLogic<String> {

    private final String accountName;
    private final List<Party> partyToShareAccountInfoToList;

    public CreatAndShareAccountFlow(String accountName, List<Party> partyToShareAccountInfoToList) {
        this.accountName = accountName;
        this.partyToShareAccountInfoToList = partyToShareAccountInfoToList;
    }
    @Suspendable
    @Override
    public String call() throws FlowException {
        System.out.println("Account flow started");

        //Call inbuilt createAccount flow to create the AccountInfo object
        StateAndRef <AccountInfo> accountInfoStateAndRef = (StateAndRef<AccountInfo>) subFlow(new CreateAccount(accountName));

        //Share this AccountInfo Object with the parties who want to transact with this account
        subFlow(new ShareAccountInfo(accountInfoStateAndRef, partyToShareAccountInfoToList));
        return " "+ accountName + " has been created and shared to " + partyToShareAccountInfoToList + " ";

    }
}