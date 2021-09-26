# Installatie PostgreSQL-databank

### Inleiding

In deze handleiding staat hoe men de databank lokaal kan installeren en de integratie met de Spring Boot-server
configureerd. Het UML-diagram van de databank staat in database/src/diagram.png. Dit diagram is gemaakt
met [Umbrello](https://umbrello.kde.org/).

### Stap 0 (Optioneel): Opstellen SQL-code voor tabeldefinities

1. Delen van de broncode worden gemaakt door [Umbrello](https://umbrello.kde.org/). Deze kun je steeds opnieuw maken
   door Umbrello te installeren en vervolgens code -> code generation wizard op te roepen.
2. Run finalise_sql.sh om all_schemas.sql aan te maken.

### Stap 1: Installatie databank

1. Installeer [PostgreSQL](https://www.postgresql.org/). Laat de PostgreSQL-server draaien op poort 2002.
2. Maak een nieuwe databank aan. Noem deze "magdadatabase". Gebruik de SQL-code uit database/src/code/create_user.sql om
   de gebruiker aan te maken.

### Stap 2: Tabellen opstellen

1. Gebruik de SQL-code uit database/src/code/all_schemas.sql om de tabellen op te stellen. Wanneer dit bestand niet
   bestaat, kan dit aangemaakt worden zoals beschreven in stap 0.
