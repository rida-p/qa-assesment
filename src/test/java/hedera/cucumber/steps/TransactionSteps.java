package hedera.cucumber.steps;

import hedera.cucumber.domain.AddressType;
import hedera.cucumber.domain.HederaOperatingAccount;
import hedera.cucumber.domain.HederaAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.github.cdimascio.dotenv.Dotenv;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import org.junit.jupiter.api.Assertions;

public class TransactionSteps {
    AccountId myAccountId = AccountId.fromString(Dotenv.load().get("MY_ACCOUNT_ID"));
    PrivateKey myPrivateKey = PrivateKey.fromString(Dotenv.load().get("MY_PRIVATE_KEY"));
    HederaOperatingAccount operatingAccount = new HederaOperatingAccount(myAccountId, myPrivateKey);

    HederaAccount accountTwo;
    AddressType addressType;

    @Given("the transaction uses the account's {string}")
    public void the_transaction_uses_the_account_s_address(String input) throws Exception {
        addressType = AddressType.fromString(input);
        accountTwo = operatingAccount.createAccount(0);
    }

    @When("account one sends hBar to account two")
    public void account_one_sends_h_bar_to_account_two() throws Exception {
        switch (addressType) {
            case id -> operatingAccount.transferHBarTo(accountTwo, 1);
            case alias -> operatingAccount.transferHBarTo(accountTwo.evmAlias, 1);
        }
    }

    @Then("account two should receive hBar")
    public void account_two_should_receive_h_bar() throws Exception {
        Assertions.assertEquals( 1, accountTwo.fetchBalance());
    }


}
