package com.hazelcast.jdbc;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JdbcConnectionIntegrationTest {
    private HazelcastJdbcClient client;

    @BeforeEach
    public void setUp() {
        HazelcastInstance member = Hazelcast.newHazelcastInstance();
        client = new HazelcastJdbcClient(Objects.requireNonNull(JdbcUrl.valueOf("jdbc:hazelcast://localhost:5701/public", new Properties())));

        IMap<Integer, Person> personMap = member.getMap("person");
        for (int i = 0; i < 3; i++) {
            personMap.put(i, new Person("Jack"+i, i));
        }
    }

    @AfterEach
    public void tearDown() {
        HazelcastClient.shutdownAll();
        Hazelcast.shutdownAll();
    }

    @Test
    public void shouldCloseConnection() throws SQLException {
        Connection connection = new JdbcConnection(client);
        connection.close();
        assertThatThrownBy(connection::createStatement)
                .isInstanceOf(SQLException.class)
                .hasMessage("Connection is closed");
        assertThat(client.isRunning()).isFalse();
    }

    @Test
    void shouldNotSupportPrepareCall() {
        Connection connection = new JdbcConnection(client);
        assertThatThrownBy(() -> connection.prepareCall("{call getPerson(?, ?)}"))
                .isInstanceOf(SQLFeatureNotSupportedException.class)
                .hasMessage("CallableStatement not supported");
    }

    @Test
    void shouldAutoCloseStatementWhenResultSetIsClosed() throws SQLException {
        Connection connection = new JdbcConnection(client);
        Statement statement = connection.createStatement();
        statement.closeOnCompletion();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
        resultSet.close();

        assertThat(statement.isClosed()).isTrue();
    }
}