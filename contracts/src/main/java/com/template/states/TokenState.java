package com.template.states;

import com.template.contracts.TokenContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
@BelongsToContract(TokenContract.class)
public class TokenState implements ContractState {
    private final Party issuer;
    private final Party owner;
    private final int amount;

    public TokenState(Party issuer, Party owner, int amount) {
        this.issuer = issuer;
        this.owner = owner;
        this.amount = amount;
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(issuer, owner);
    }
}