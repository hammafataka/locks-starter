package dev.mfataka.locks.core.service;

import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.exception.LockOperationException;
import dev.mfataka.locks.api.service.JdbcService;


/**
 * @author HAMMA FATAKA
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class SimpleJdbcService implements JdbcService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @PostConstruct
    public void init() throws LockOperationException {
        ensureLockTableExists();
    }

    public void ensureLockTableExists() throws LockOperationException {
        log.info("Checking if distributed_lock table exists");
        try (final var conn = dataSource.getConnection()) {
            final var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS distributed_locks (\n" +
                    "                            name VARCHAR(100) PRIMARY KEY,\n" +
                    "                            owner VARCHAR(100),\n" +
                    "                            locked_at int \n" +
                    "                        )");
            stmt.execute();
        } catch (SQLException e) {
            throw new LockOperationException("Failed to ensure lock table exists", e);
        }
    }


    public boolean insertLock(final String name, final String ownerId) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("owner", ownerId);

        var rows = 0;
        try {
            rows = jdbcTemplate.update(
                    "INSERT INTO distributed_locks (name, owner, locked_at) VALUES (:name, :owner, UNIX_TIMESTAMP())",
                    parameters
            );
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        return rows > 0;
    }

    public boolean isLocked(final String name) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", name);

        final var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distributed_locks WHERE name = :name",
                parameters,
                Integer.class
        );
        return count != null && count > 0;
    }

    public boolean isLockedBy(final String name, final String ownerId) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("owner", ownerId);

        final var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distributed_locks WHERE name = :name AND owner = :owner",
                parameters,
                Integer.class
        );
        return count != null && count > 0;
    }


    public void deleteLock(final String name, final String ownerId) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("owner", ownerId);
        jdbcTemplate.update("DELETE FROM distributed_locks WHERE name = :name AND owner = :owner ", parameters);

    }

    public void deleteLock(final String name) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", name);
        jdbcTemplate.update("DELETE FROM distributed_locks WHERE name = :name ", parameters);
    }

    public long cleanExpiredLocks(final Duration maxAge) {
        final var parameters = new MapSqlParameterSource()
                .addValue("time_to_check", maxAge.getSeconds());
        final var sql = "DELETE FROM distributed_locks WHERE locked_at < (UNIX_TIMESTAMP() - :time_to_check)";
        int removed = jdbcTemplate.update(sql, parameters);
        if (removed > 0) {
            log.info("Expired lock cleanup: removed {} stale locks", removed);
        }
        return removed;
    }

    public int existingLocksCount() {
        final var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM distributed_locks ", new HashMap<>(), Integer.class);
        return count != null ? count : 0;
    }

    public int deleteAllLocks() {
        return jdbcTemplate.update("DELETE FROM distributed_locks", new HashMap<>());
    }

    @Override
    public long elapsedTime(final String lockName) {
        final var parameters = new MapSqlParameterSource()
                .addValue("name", lockName);
        final var elapsedTime = jdbcTemplate.queryForObject("SELECT (locked_at) FROM distributed_locks where name= :name", parameters, Long.class);
        return elapsedTime != null ? elapsedTime : 0;
    }

    @Override
    public List<Locker> findAll(final LockMode lockMode) {
        return jdbcTemplate.query("SELECT * FROM distributed_locks", new HashMap<>(), (rs, rowNum) -> Locker.jdbcLocker(rs.getString(1), rs.getLong(3), lockMode));
    }
}
