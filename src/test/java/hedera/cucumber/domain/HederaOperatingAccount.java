package hedera.cucumber.domain;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.*;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HederaOperatingAccount extends HederaAccount {

    private final PrivateKey privateKey;

    public HederaOperatingAccount(AccountId accountId, PrivateKey privateKey) {
        this.accountId = accountId;
        this.privateKey = privateKey;
        client.setOperator(this.accountId, this.privateKey);
    }

    public HederaAccount createAccount(long initalHBarAmount) throws Exception {
        PrivateKey newAccountPrivateKey = PrivateKey.generateED25519();
        PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();
        TransactionResponse newAccount = new AccountCreateTransaction()
                .setKey(newAccountPublicKey)
                .setInitialBalance(Hbar.fromTinybars(initalHBarAmount))
                .execute(client);
        AccountId newAccountId = newAccount.getReceipt(client).accountId;
        assert newAccountId != null;
        var alias = this.createAlias(newAccountId, newAccountPublicKey);
        HederaAccount account = new HederaAccount(newAccountId, this.client, alias);
        long balance = account.fetchBalance();
        if (balance != initalHBarAmount) {
            throw new Error("Initial Hbar amount not set correctly");
        }
        return account;
    }

    private String createAlias(AccountId accountId, PublicKey publicKey) {
        String ecdsaKey = publicKey.toString();
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashbytes = digest256.digest(
                ecdsaKey.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = new String(Hex.encode(hashbytes));

        var evmAlias = new StringBuilder()
                .append(accountId.shard)
                .append(".")
                .append(accountId.realm)
                .append(".")
                .append(sha3Hex);
        return sha3Hex;
    }
}
