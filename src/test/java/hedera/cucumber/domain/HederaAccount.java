package hedera.cucumber.domain;

import com.hedera.hashgraph.sdk.*;

import java.util.concurrent.TimeoutException;

public class HederaAccount {

    public Client client;
    public AccountId accountId;
    public String evmAlias;

    public HederaAccount() {
        this.client = Client.forTestnet();
    }

    public HederaAccount(AccountId accountId, Client client, String evmAlias) {
        super();
        this.accountId = accountId;
        this.client = client;
        this.evmAlias = evmAlias;
    }

    public long fetchBalance() throws PrecheckStatusException, TimeoutException {
        AccountBalance accountBalance = new AccountBalanceQuery()
                .setAccountId(this.accountId)
                .execute(client);
        return accountBalance.hbars.toTinybars();
    }

    public void transferHBarTo(HederaAccount anotherAccountId, long amount) throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
        long minusAmount = -amount;
        TransactionResponse sendHbar = new TransferTransaction()
                .addHbarTransfer(this.accountId, Hbar.fromTinybars(minusAmount))
                .addHbarTransfer(anotherAccountId.accountId, Hbar.fromTinybars(amount))
                .execute(client);
        if (sendHbar.getReceipt(client).status != Status.SUCCESS) {
            throw new Error("Status: " + sendHbar.getReceipt(client).status);
        }
    }

    public void transferHBarTo(String alias, long amount) throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
        long minusAmount = -amount;
        TransactionResponse sendHbar = new TransferTransaction()
                .addHbarTransfer(this.accountId, Hbar.fromTinybars(minusAmount))
                .addHbarTransfer(EvmAddress.fromString(alias), Hbar.fromTinybars(amount))
                .execute(client);
        if (sendHbar.getReceipt(client).status != Status.SUCCESS) {
            throw new Error("Status: " + sendHbar.getReceipt(client).status);
        }
    }

}
