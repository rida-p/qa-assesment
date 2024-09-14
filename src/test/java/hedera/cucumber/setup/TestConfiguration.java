package hedera.cucumber.setup;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import io.github.cdimascio.dotenv.Dotenv;

public class TestConfiguration {

    public static TestConfiguration shared() {
        if(sharedInstance == null) {
            sharedInstance = new TestConfiguration();
        }
        return sharedInstance;
    }

    public AccountId operatorAccount() {
        return this.operatorAccount;
    }

    public PrivateKey operatorPrivateKey() {
        return this.operatorPrivateKey;
    }

    private static TestConfiguration sharedInstance;

    private AccountId operatorAccount;
    private PrivateKey operatorPrivateKey;

    private TestConfiguration() {
        this.operatorAccount = AccountId.fromString(Dotenv.load().get("MY_ACCOUNT_ID"));
        this.operatorPrivateKey = PrivateKey.fromString(Dotenv.load().get("MY_PRIVATE_KEY"));
    }
}
