CREATE TABLE IF NOT EXISTS distributed_locks
(
    name VARCHAR(100) PRIMARY KEY,
    owner VARCHAR(100),
    locked_at INT
);

CREATE ALIAS IF NOT EXISTS UNIX_TIMESTAMP FOR "dev.mfataka.locks.core.utils.H2Utils.unixTimestamp";
