package com.mishenev.post_book.db;

import java.util.Objects;

/**
 * Database Connection Details POJO.
 *
 * @author Dmitrii_Mishenev
 */
public class DbConnectionDetails {
    private String username;
    private String password;
    private String host;
    private String port;
    private String dbname;

    public DbConnectionDetails() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbConnectionDetails)) {
            return false;
        }
        DbConnectionDetails that = (DbConnectionDetails) o;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getHost(), that.getHost()) && Objects.equals(getPort(), that.getPort()) && Objects.equals(getDbname(), that.getDbname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getHost(), getPort(), getDbname());
    }
}