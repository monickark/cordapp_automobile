package com.template.contracts;

import com.template.states.TokenState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

@BelongsToContract(TokenContract.class)
public class TokenContract implements Contract {
    public static String ID = "com.bootcamp.contracts.TokenContract";


    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getInputStates().size() !=0) {
            throw new IllegalArgumentException("Token contract requires zero inputs in the transaction");
        }
        if(tx.getOutputStates().size() !=1) {
            throw new IllegalArgumentException("Token contract requires one output in the transaction");
        }
        if(tx.getCommands().size() !=1) {
            throw new IllegalArgumentException("Token contract requires one command in the transaction");
        }
        if(!(tx.getOutput(0) instanceof TokenState)){
            throw new IllegalArgumentException("Token contract requires output should be tokenstate");
        }

        TokenState tokenState = (TokenState) tx.getOutput(0);

        if(!(tokenState.getAmount() >0)) {
            throw new IllegalArgumentException("Token contract requires the tx output to have a positive amount ");
        }

        if(!(tx.getCommand(0).getValue() instanceof TokenContract.Commands.Issue)) {
            throw new IllegalArgumentException("Token contract requires the tx command to ba an issue command");
        }

        if(!(tx.getCommand(0).getSigners().contains(tokenState.getIssuer().getOwningKey()))) {
            throw new IllegalArgumentException("Token contract requires the issuer should be the required signer of the tx");
        }

    }


    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}