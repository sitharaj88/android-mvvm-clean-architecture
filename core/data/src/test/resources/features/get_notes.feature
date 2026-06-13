Feature: Retrieve notes

  Scenario: Get notes and exclude deleted ones
    Given the following notes exist:
      | id | title | content | syncState |
      | 1  | t1    | c1      | SYNCED    |
      | 2  | t2    | c2      | DELETED   |
    When I request all notes
    Then the response should contain only note with id 1
