package hedera.cucumber.steps;

import com.hedera.hashgraph.sdk.*;
import hedera.cucumber.setup.TestConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;

public class FTSteps {

    Client client = Client.forTestnet();

    AccountId creator;
    PrivateKey creatorKey = PrivateKey.generate();
    PublicKey creatorPublicKey = creatorKey.getPublicKey();

    AccountId receiver;
    PrivateKey receiverKey = PrivateKey.generate();
    PublicKey receiverPublicKey = receiverKey.getPublicKey();

    TokenId tokenId;
    TokenCreateTransaction tokenCreateTransaction;

    @Given("a fungible token has been created")
    public void a_fungible_token_has_been_created() throws Exception {
        AccountId operatorAccount = TestConfiguration.shared().operatorAccount();
        PrivateKey operatorPrivateKey = TestConfiguration.shared().operatorPrivateKey();

        client.setOperator(operatorAccount, operatorPrivateKey);

        TransactionResponse creatorAccountCreation = new AccountCreateTransaction()
                .setKey(creatorPublicKey)
                .setInitialBalance(new Hbar(5))
                .execute(client);
        creator = creatorAccountCreation.getReceipt(client).accountId;

        TransactionResponse receiverAccountCreation = new AccountCreateTransaction()
                .setKey(receiverPublicKey)
                .setInitialBalance(new Hbar(5))
                .execute(client);
        receiver = receiverAccountCreation.getReceipt(client).accountId;

        PrivateKey supplyKey = PrivateKey.generate();
        tokenCreateTransaction = new TokenCreateTransaction()
                .setTokenName("FungibleToken")
                .setTokenSymbol("FT")
                .setTokenType(TokenType.FUNGIBLE_COMMON)
                .setDecimals(2)
                .setInitialSupply(10)
                .setTreasuryAccountId(creator)
                .setSupplyType(TokenSupplyType.INFINITE)
                .setSupplyKey(supplyKey)
                .freezeWith(client);
    }
    @When("this fungible token has been minted")
    public void this_fungible_token_has_been_minted() throws Exception {
        TokenCreateTransaction tokenCreateSign = tokenCreateTransaction.sign(creatorKey);
        TransactionResponse tokenCreateSubmit = tokenCreateSign.execute(client);
        TransactionReceipt tokenCreateReceipt = tokenCreateSubmit.getReceipt(client);
        tokenId = tokenCreateReceipt.tokenId;
    }
    @Then("this fungible token can be sent to another account")
    public void this_fungible_token_can_be_sent_to_another_account() throws Exception {
        TokenAssociateTransaction receiverAssociationTransaction = new TokenAssociateTransaction()
                .setAccountId(receiver)
                .setTokenIds(Collections.singletonList(tokenId))
                .freezeWith(client)
                .sign(receiverKey);
        TransactionResponse receiverAssociationTransactionResponse = receiverAssociationTransaction.execute(client);
        TransactionReceipt receiverTransactionReceipt = receiverAssociationTransactionResponse.getReceipt(client);

        Assertions.assertEquals(Status.SUCCESS, receiverTransactionReceipt.status);

        TransferTransaction tokenTransferTransaction = new TransferTransaction()
                .addTokenTransfer(tokenId, creator, -5)
                .addTokenTransfer(tokenId, receiver, 5)
                .freezeWith(client)
                .sign(creatorKey);
        TransactionResponse tokenTransferSubmit = tokenTransferTransaction.execute(client);
        TransactionReceipt tokenTransferReceipt = tokenTransferSubmit.getReceipt(client);

        Assertions.assertEquals(Status.SUCCESS, tokenTransferReceipt.status);
    }
}
