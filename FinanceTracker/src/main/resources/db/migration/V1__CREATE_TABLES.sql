CREATE TABLE USERS(id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                   name TEXT NOT NULL,
                   email TEXT UNIQUE NOT NULL,
                   password TEXT NOT NULL,
                   dob DATE NOT NULL,
                   created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                   updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                   deleted_at TIMESTAMPTZ,
                   role TEXT NOT NULL  DEFAULT 'USER' CHECK (ROLE IN('ADMIN','USER','GUEST'))
);

CREATE TABLE CATEGORY(
                         id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         user_id BIGINT,
                         cat_name TEXT NOT NULL,
                         cat_description TEXT NOT NULL,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMPTZ,
                         is_system_generated BOOLEAN NOT NULL DEFAULT FALSE,
                         CONSTRAINT fk_category_user FOREIGN KEY(user_id) REFERENCES users(id),
                         CONSTRAINT unique_user_category_name UNIQUE(user_id, cat_name)
);

CREATE TABLE SUBSCRIPTION(
     id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY ,
     user_id BIGINT NOT NULL,
     cat_id BIGINT,
     amount DECIMAL(15,2) NOT NULL,
     sub_description TEXT,
     billing_interval TEXT NOT NULL CHECK (billing_interval IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
     status TEXT NOT NULL CHECK (status IN ('ACTIVE', 'PAUSED', 'CANCELLED')) DEFAULT 'ACTIVE',
     start_date DATE NOT NULL,
     last_billed_date DATE,
     next_billing_date DATE NOT NULL,
     created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_sub_user FOREIGN KEY(user_id) REFERENCES users(id),
     CONSTRAINT fk_sub_cat FOREIGN KEY(cat_id) REFERENCES category(id)
);


CREATE TABLE TRANSACTION(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cat_id BIGINT,
    user_id BIGINT NOT NULL,
    sub_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    trans_description TEXT,
    transaction_type TEXT NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'PURCHASE', 'REFUND')),
    transaction_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN('PENDING', 'COMPLETED', 'REJECTED')),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trans_category FOREIGN KEY(cat_id) REFERENCES category(id),
    CONSTRAINT fk_trans_user FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_trans_sub FOREIGN KEY(sub_id) REFERENCES SUBSCRIPTION(id)
);

CREATE TABLE BUDGET(
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       user_id BIGINT NOT NULL,
                       cat_id BIGINT,
                       amount DECIMAL(15,2) NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                       period TEXT NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,
                       CONSTRAINT fk_budget_user FOREIGN KEY(user_id) REFERENCES users(id),
                       CONSTRAINT fk_budget_cat FOREIGN KEY(cat_id) REFERENCES category(id)
);
