package com.template.contracts;

import com.template.states.CarState;
import com.template.states.TemplateState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class CarContract implements Contract {
    public static final String CAR_CONTRACT_ID = "com.template.contracts.CarContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() !=1) {
            throw new IllegalArgumentException("There can be only one cmd...");
        }
        Command command = tx.getCommand(0);
        CommandData commandType = command.getValue();
        List<PublicKey> requiredSigners = command.getSigners();

        if(commandType instanceof Shipment) {

            //shape rules
            if(tx.getInputStates().size() !=0){
                throw new IllegalArgumentException("There cannot be input states");
            }
            if(tx.getOutputStates().size() !=0){
                throw new IllegalArgumentException("Only vehicle can be at a time");
            }

            //content rules
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof CarState)) {
                throw new IllegalArgumentException("Output can only be of type car state");
            }

            CarState carState = (CarState) outputState;
            if(carState.getModel().equals("Cybertruck")){
                throw new IllegalArgumentException("Only a cyber truck can be shipped");
            }

            //Signer rules
            PublicKey manufacturerKey = carState.getManufacturer().getOwningKey();
            if(!(requiredSigners.contains(manufacturerKey))) {
                throw new IllegalArgumentException("Manufacturer must sign the tx");
            }

        }
    }

    public static class Shipment implements CommandData {

    }
}