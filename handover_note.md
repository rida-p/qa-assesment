## Handover Note

### On the use of Singletons

Generally singletons are avoided for good reasons, in non-Cucumber tests, I would usually load test configuration and their variables in an abstract base class, which then every test class (should/can) subclass and have all testconfiguration and setup ready at their disposal.

### On the difference of abstracting code between the two feature files

One may notice that barely any code is abstracted in the steps connected with the `tokens.feature` file, while for `transactions.feature`, most of the code has been abstracted with focus on readability, this difference is intended for demonstration purposes.

The abstracted code for the `transactions.feature` file resides in the `domain` folder, the focus was on improving readability in the steps files, abstracted code in the `domain` folder could be further improved

### On account aliases

In the `transactions.feature` file, you will find the example with `alias` commented out, when the code is uncommented, this Cucumber example will fail.

The reason: couldn't figure out how/where to create or get an account alias.

While I did understand the hints given in the pdf, while going through the Hedera documentation, there are supposed to be two different types of aliases (one based of a public key, the other an EVM address) and I couldn't figure out on which type of alias the hints were meant for.

An attempt was made at creating an EVM alia (see `HederaOperatingAccount.java`, in the `private String createAlias(AccountId accountId, PublicKey publicKey)` method), however this does not result in a correct alias
Documentation source: https://docs.hedera.com/hedera/core-concepts/accounts/account-properties#account-alias