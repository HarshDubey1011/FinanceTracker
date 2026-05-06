1. how did you handle uniqueness in your category table?
Ans:- I used the combination of the partial unique indexing and the constriant composite key on user_id and category_name
2. Will u give client to access to assign the role?
Ans:- No only the admin should have access to elevate the role by default the client should have a role of User
3. How did you do anything for performance/scalability?
Ans:- I enabled virtual threads via Spring Boot 3.2's built-in support. Since the app is I/O bound — every request hits PostgreSQL and Redis — virtual threads handle blocking operations much more efficiently than platform threads without any code changes.