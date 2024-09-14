package hedera.cucumber.steps;

import hedera.cucumber.setup.TestConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.hedera.hashgraph.sdk.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class NFTSteps {

    TokenId tokenId;
    Client client = Client.forTestnet();
    TokenCreateTransaction nftCreate;

    PrivateKey supplyKey = PrivateKey.generateED25519();

    AccountId nftCreator;
    PrivateKey nftCreatorKey = PrivateKey.generateED25519();
    PublicKey nftCreatorPublicKey = nftCreatorKey.getPublicKey();

    AccountId nftReceiver;
    PrivateKey nftReceiverKey = PrivateKey.generateED25519();
    PublicKey nftReceiverPublicKey = nftReceiverKey.getPublicKey();

    @Given("a non-fungible token has been created")
    public void a_non_fungible_token_has_been_created() throws Exception {
        AccountId operatorAccount = TestConfiguration.shared().operatorAccount();
        PrivateKey operatorPrivateKey = TestConfiguration.shared().operatorPrivateKey();

        client.setOperator(operatorAccount, operatorPrivateKey);

        TransactionResponse creatorAccountTransaction = new AccountCreateTransaction()
                .setKey(nftCreatorPublicKey)
                .setInitialBalance(new Hbar(10))
                .execute(client);
        nftCreator = creatorAccountTransaction.getReceipt(client).accountId;

        TransactionResponse receiverAccountTransaction = new AccountCreateTransaction()
                .setKey(nftReceiverPublicKey)
                .setInitialBalance(new Hbar(10))
                .execute(client);
        nftReceiver = receiverAccountTransaction.getReceipt(client).accountId;

        nftCreate = new TokenCreateTransaction()
                .setTokenName("anTokenName")
                .setTokenSymbol("ANSYMBOL")
                .setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
                .setDecimals(0)
                .setInitialSupply(0)
                .setTreasuryAccountId(nftCreator)
                .setSupplyType(TokenSupplyType.FINITE)
                .setMaxSupply(250)
                .setSupplyKey(supplyKey)
                .freezeWith(client);
    }

    @When("this non-fungible token gets minted")
    public void this_non_fungible_token_gets_minted() throws Exception {
        TokenCreateTransaction nftCreateSignTransactino = nftCreate.sign(nftCreatorKey);
        TransactionResponse nftCreateSubmit = nftCreateSignTransactino.execute(client);
        TransactionReceipt nftCreateReceipt = nftCreateSubmit.getReceipt(client);
        tokenId = nftCreateReceipt.tokenId;

        final int MAX_TRANSACTION_FEE = 20;
        String[] CID = {
                "ipfs://bafyreiao6ajgsfji6qsgbqwdtjdu5gmul7tv2v3pd6kjgcw5o65b2ogst4/metadata.json",
                "ipfs://bafyreic463uarchq4mlufp7pvfkfut7zeqsqmn3b2x3jjxwcjqx6b5pk7q/metadata.json",
                "ipfs://bafyreihhja55q6h2rijscl3gra7a3ntiroyglz45z5wlyxdzs6kjh2dinu/metadata.json",
                "ipfs://bafyreidb23oehkttjbff3gdi4vz7mjijcxjyxadwg32pngod4huozcwphu/metadata.json",
                "ipfs://bafyreie7ftl6erd5etz5gscfwfiwjmht3b52cevdrf7hjwxx5ddns7zneu/metadata.json"
        };
        TokenMintTransaction mintTransaction = new TokenMintTransaction().setTokenId(tokenId)
                .setMaxTransactionFee(new Hbar(MAX_TRANSACTION_FEE));
        for (String cid : CID) {
            mintTransaction.addMetadata(cid.getBytes());
        }
        mintTransaction.freezeWith(client);
        TokenMintTransaction tokenSignTransaction = mintTransaction.sign(supplyKey);
        TransactionResponse tokenSignTransactionResponse = tokenSignTransaction.execute(client);
        TransactionReceipt tokenMindReceipt = tokenSignTransactionResponse.getReceipt(client);
        Assertions.assertNotNull(tokenMindReceipt.serials);
    }

    @Then("this non-fungible token can be sent to another account")
    public void this_non_fungible_token_can_be_sent_to_another_account() throws Exception{
        TokenAssociateTransaction associateReceiverTransaction = new TokenAssociateTransaction()
                .setAccountId(nftReceiver)
                .setTokenIds(Collections.singletonList(tokenId))
                .freezeWith(client)
                .sign(nftReceiverKey);
        TransactionResponse submitReceiverAssociation = associateReceiverTransaction.execute(client);
        TransactionReceipt receiverTransactionReceipt = submitReceiverAssociation.getReceipt(client);
        Assertions.assertEquals(Status.SUCCESS, receiverTransactionReceipt.status);

        AccountBalance creatorBalanceBeforeTransfer = new AccountBalanceQuery().setAccountId(nftCreator).execute(client);
        Assertions.assertEquals(5, creatorBalanceBeforeTransfer.tokens.get(tokenId));

        AccountBalance receiverBalanceBeforeTransfer = new AccountBalanceQuery().setAccountId(nftReceiver).execute(client);
        Assertions.assertEquals(0, receiverBalanceBeforeTransfer.tokens.get(tokenId));

        TransferTransaction tokenTransferTransaction = new TransferTransaction()
                .addNftTransfer(new NftId(tokenId, 1), nftCreator, nftReceiver)
                .freezeWith(client).sign(nftCreatorKey);
        TransactionResponse tokenTransferSubmit = tokenTransferTransaction.execute(client);
        TransactionReceipt tokenTransferReceipt = tokenTransferSubmit.getReceipt(client);

        AccountBalance creatorBalanceAfterTransfer = new AccountBalanceQuery().setAccountId(nftCreator).execute(client);
        Assertions.assertEquals(4, creatorBalanceAfterTransfer.tokens.get(tokenId));

        AccountBalance receiverBalanceAfterTransfer = new AccountBalanceQuery().setAccountId(nftReceiver).execute(client);
        Assertions.assertEquals(1, receiverBalanceAfterTransfer.tokens.get(tokenId));
    }
}
