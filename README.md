# Sortable Coding Challenge
This is my entry for the Sortable Coding Challenge.

Since the challenge is relatively open ended, my approach focuses on simplicity.

## Running
`sbt run`

## Approach
For this kind of task it is often the case that a few hand-crafted rules can beat rather complex machine learning based methods. If we assume that we want write code for use in production, then simpler and extensible methods are likely a better choice than complex systems. The machine learning approach also suffers from no labeled test set, so we can't easily optimize an estimator.

Data exploration is key in this challenge. I have experimented with different rules and looked at the percentage of matches, negatives and positives. The code contains a set of rules that seems to work reasonable well.

## Assumptions
I make the following assumptions:
- If there are duplicates in the products (e.g. TODO) it is better to ignore the product because it is uncertain which one is correct (and we want to avoid errors at all costs).
- *"The task is to match each listing to the correct product."* I assume that a listing for a complement to a product (e.g. accessoire such as a battery or flash) should not be matched to a product, since it is technically a different product.
