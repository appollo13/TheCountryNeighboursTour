deVRealm: The Country Neighbours Tour
-------------------------------------

Angel likes to travel around neighbor countries. To help him plan his next trip you need to create a 
REST API accepting the following request parameters: Starting country Budget per country (equal for 
all neighbor countries) Total budget Input Currency.

Result:

1. The API should calculate how many exact times can Angel go through all neighbor countries within his total budget.

2. The API should calculate the budget for each country in their respected currencies. If the 
exchange rate is missing it should return the amount in the original currency. The potential 
leftover amount from the total budget should also be returned in the original currency.

Example: Angel fills in the following request values: 
• Starting country: Bulgaria (BG) • Budget per country: 100 • Total budget: 1200 • Currency: EUR

Example Result: Bulgaria has 5 neighbor countries (TR, GR, MK, SR, RO) and Angel can travel around them 2 times. 
He will have 200 EUR leftover. 
For Turkey he will need to buy 1325.30 TL, for North Macedonia he will need to buy 12232.51 MKD, and so on.

Requirements:

1. Use Java Spring Boot Framework to build the REST API

2. Upload project to GIT/Bitbucket


Bonuses :

1. Secure API with Google Oauth2 Login

2. Implement UI Login form with Google Oauth2


More
----
+ the Input Currency is optional, as it can be extracted from the Starting country default/primary/first currency
