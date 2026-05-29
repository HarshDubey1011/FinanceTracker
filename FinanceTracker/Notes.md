1. how did you handle uniqueness in your category table?
Ans:- I used the combination of the partial unique indexing and the constriant composite key on user_id and category_name
2. Will u give client to access to assign the role?
Ans:- No only the admin should have access to elevate the role by default the client should have a role of User
3. How did you do anything for performance/scalability?
Ans:- I enabled virtual threads via Spring Boot 3.2's built-in support. Since the app is I/O bound — every request hits PostgreSQL and Redis — virtual threads handle blocking operations much more efficiently than platform threads without any code changes.
4. ## Why soft delete on transactions?
Ans:- Financial data must never be permanently deleted.
Hard deleting a transaction would retroactively change
budget calculations and monthly reports.
5. ## What is the Use of JpaSpecificationExecutor
Ans: - It solves the Query Explosion Problem.
6. We should use UUID id for the micrsoservice applications and for the monolithic applicatoin we should stick the Long id while using the postgresql, why?
Ans:- Because the size of UUID is 36 bytes and that of long is 8 bytes and the postregsql uses the B-Tree structure for there primary key indexes. Sequential numbers are inserted continuously at the end of the tree, which is lightning fast. So if we have used the UUID it will cause the Index Fragmentation(because UUIDs are completely random, the database constantly has to split and re-balance the B-Tree index on every single insert). So its a tradeoff.

## Budget Alert Design Decisions

### Why alertSent flag?
Without it, every transaction after budget breach triggers
an email. One flag per budget period prevents spam.
Reset on budget update so new limits get fresh alerts.

### Why filter by TransactionType in sum queries?
DEPOSIT and REFUND transactions are money coming IN.
Summing them as spending would trigger false alerts.
Only WITHDRAWAL and PURCHASE count as spending.