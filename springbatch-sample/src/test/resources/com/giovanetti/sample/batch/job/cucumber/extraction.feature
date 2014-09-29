Feature: batch extraction+
  Scenario: scenario 1
    Given les utilisateurs
      |Id|Nom|Prenom|
      |1|nom1|prenom1|
      |2|nom2|prenom2|
    When je charge les utilisateurs en base de données
    And j'execute le job d'extraction
    Then mon fichier de sortie contient les lignes
  |1,prenom1,nom1|
  |2,prenom2,nom2|

  Scenario: scenario 2
    Given les utilisateurs
      |Id|Nom|Prenom|
      |1|nom1|prenom1|
    When je charge les utilisateurs en base de données
    And j'execute le job d'extraction
    Then mon fichier de sortie contient les lignes
  |1,prenom1,nom1|

