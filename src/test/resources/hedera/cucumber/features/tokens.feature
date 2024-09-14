Feature: As a user, I want to create Fungible and non-Fungible tokens

  Scenario: Create Fungible token and send it to another account
    Given a fungible token has been created
    When this fungible token has been minted
    Then this fungible token can be sent to another account

  Scenario: Create non-Fungible token and send it to another account
    Given a non-fungible token has been created
    When this non-fungible token gets minted
    Then this non-fungible token can be sent to another account