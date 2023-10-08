package com.template.contracts;

import com.template.states.TemplateState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import org.junit.Test;

public class StateTests {

    private final Party alice = new TestIdentity(new CordaX500Name("Alice","New York", "US")).getParty();
    private final Party bob = new TestIdentity(new CordaX500Name("Bob","Washington", "US")).getParty();
    //Mock State test check for if the state has correct parameters type
    @Test
    public void hasFieldOfCorrectType() throws NoSuchFieldException {
        TemplateState.class.getDeclaredField("msg");
        assert (TemplateState.class.getDeclaredField("msg").getType().equals(String.class));
    }
}