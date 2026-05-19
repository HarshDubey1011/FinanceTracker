-- V9__add_transaction_indexes.sql
CREATE INDEX idx_transaction_user ON transactions(user_id);
CREATE INDEX idx_transaction_category ON transactions(cat_id);
CREATE INDEX idx_transaction_date ON transactions(transaction_date);