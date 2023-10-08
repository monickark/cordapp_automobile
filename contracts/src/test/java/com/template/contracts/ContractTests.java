package com.template.contracts;

import com.template.states.TemplateState;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import org.jvnet.hk2.annotations.Contract;

import java.util.Arrays;

import static net.corda.testing.node.NodeTestUtils.ledger;
import static net.corda.testing.node.NodeTestUtils.transaction;


public class ContractTests {
    private final MockServices ledgerServices = new MockServices(Arrays.asList("com.template"));
    TestIdentity alice = new TestIdentity(new CordaX500Name("Alice",  "TestLand",  "US"));
    TestIdentity bob = new TestIdentity(new CordaX500Name("Alice",  "TestLand",  "US"));

    @Test
    public void TokenContractImplementsContract() {
        assert(new TokenContract() instanceof Contract);
    }
    @Test
    public void tokenCOntractRequiresZeroInputsInTheTransaction() {
        transaction(ledgerServices, tx -> {
                tx.input(TokenContract.ID, tokenState);
                tx.output(TokenContract.ID, tokenState);
                tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()));
                tx.fails();
                return null;
            });
        transaction(ledgerServices, tx -> {
                tx.output(TokenContract.ID, state);
                tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()));
                tx.verifies();
            return null;
            });
    }
}