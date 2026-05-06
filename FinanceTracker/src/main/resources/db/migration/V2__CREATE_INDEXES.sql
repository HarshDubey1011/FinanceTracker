CREATE UNIQUE INDEX idx_unique_system_category
    ON category (cat_name)
    WHERE user_id IS NULL;

CREATE UNIQUE INDEX idx_unique_user_category_budget
    ON budget (user_id, cat_id, start_date)
    WHERE cat_id IS NOT NULL;

CREATE UNIQUE INDEX idx_unique_user_global_budget
    ON budget (user_id, start_date)
    WHERE cat_id IS NULL;