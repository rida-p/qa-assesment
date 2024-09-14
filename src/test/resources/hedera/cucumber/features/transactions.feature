Feature: As a QA, I want to validate transactions between two testnet accounts

  Scenario Outline: transfer hBar between two testnet accounts
    Given the transaction uses the account's "<address_type>"
    When account one sends hBar to account two
    Then account two should receive hBar

    Examples:
    | address_type |
    | id |
#    | alias   |