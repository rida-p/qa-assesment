# Readme

## Tech Stack

Java in combination with Gradle (and optionally Docker)

## How to Run:

```bash
./gradlew test
```

or run in docker (should work on any computer):

```bash
./run_in_docker.sh
```

alternatively, open the project in your favourite IDE with cucumber/java support

## Environment variables

In the root folder a `.env` file holds variables which are available through the `TestConfiguration` singleton class, which reads the variables in this file on first use.
The variables in question `MY_ACCOUNT_ID` and `MY_PRIVATE_KEY` ideally would not be included in the git repository.

Both Variables are used to initialise the operator account (which then spawns new accounts as needed), this account has been freshly topped up with 1000 hBars before sending over the code

Should this file not be visible on linux/unix systems: `ls -a` will display hidden files starting with `.` (a dot)

## Code organisation

```
src/test/resources/hedera/cucumber/features/    # Feature files with Gherkin code
src/test/java/hedera/cucumber/steps/            # Cucumber Steps 
src/test/java/hedera/cucumber/domain/           # Domain specific code (Hedera)
```

### Operating System support
No Windows machine was available at the time of development, commands on how to run the code will work on linux/unix(macos) with gradle and/or docker installed.
Tested on Debian and MacOS.

### IDE Support
Code was written using Jetbrains Intellij (hence the `.idea` (hidden) configuration folder residing in the project root), the `.idea` folder can be deleted without consequences when using another IDE.

Given that the code was instructed to be handed over in a compressed zip file, previously mentioned `.idea` folder is present, yet wouldn't be if this code were to be pushed to Github/Gitlab/Bitbucket/...
