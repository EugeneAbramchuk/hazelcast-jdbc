/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.jdbc;

import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.sql.SqlColumnMetadata;
import com.hazelcast.sql.SqlColumnType;
import com.hazelcast.sql.SqlRow;
import com.hazelcast.sql.SqlRowMetadata;
import com.hazelcast.sql.impl.SqlRowImpl;
import com.hazelcast.sql.impl.row.JetSqlRow;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class JdbcDataBaseMetadata implements DatabaseMetaData {
    private static final Logger LOGGER = Logger.getLogger(JdbcDataBaseMetadata.class.getName());

    private static final int JDBC_VERSION_MAJOR = 4;
    private static final int JDBC_VERSION_MINOR = 3;

    private final JdbcConnection connection;

    public JdbcDataBaseMetadata(JdbcConnection connection) {
        this.connection = connection;
    }

    @Override
    public boolean allProceduresAreCallable() {
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() {
        return true;
    }

    @Override
    public String getURL() {
        return connection.getJdbcUrl().getRawUrl();
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean nullsAreSortedHigh() {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() {
        return true;
    }

    @Override
    public boolean nullsAreSortedAtStart() {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() {
        return false;
    }

    @Override
    public String getDatabaseProductName() {
        return "Hazelcast";
    }

    @Override
    public String getDatabaseProductVersion() {
        return this.getMasterVersion().toString();
    }

    @Override
    public String getDriverName() {
        return "Hazelcast JDBC";
    }

    @Override
    public String getDriverVersion() {
        return Driver.VER_MAJOR + "." + Driver.VER_MINOR;
    }

    @Override
    public int getDriverMajorVersion() {
        return Driver.VER_MAJOR;
    }

    @Override
    public int getDriverMinorVersion() {
        return Driver.VER_MINOR;
    }

    @Override
    public boolean usesLocalFiles() {
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() {
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() {
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return true;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() {
        return true;
    }

    @Override
    public String getIdentifierQuoteString() {
        return "\"";
    }

    @Override
    public String getSQLKeywords() {
        return "";
    }

    @Override
    public String getNumericFunctions() {
        return "ABS,CEIL,DEGREES,EXP,FLOOR,LN,LOG10,LOG10,RAND,ROUND,SIGN,TRUNCATE,ACOS,ASIN,ATAN,COS,COT,SIN,TAN";
    }

    @Override
    public String getStringFunctions() {
        return "ASCII,BTRIM,INITCAP,LENGTH,LIKE,ESCAPE,LOWER,LTRIM,RTRIM,SUBSTRING,TRIM,UPPER";
    }

    @Override
    public String getSystemFunctions() {
        return "";
    }

    @Override
    public String getTimeDateFunctions() {
        return "";
    }

    @Override
    public String getSearchStringEscape() {
        return "%";
    }

    @Override
    public String getExtraNameCharacters() {
        return "";
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() {
        return true;
    }

    @Override
    public boolean nullPlusNonNullIsNull() {
        return true;
    }

    @Override
    public boolean supportsConvert() {
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) {
        return false;
    }

    @Override
    public boolean supportsTableCorrelationNames() {
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() {
        return true;
    }

    @Override
    public boolean supportsOrderByUnrelated() {
        return true;
    }

    @Override
    public boolean supportsGroupBy() {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() {
        return true;
    }

    @Override
    public boolean supportsLikeEscapeClause() {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() {
        return false;
    }

    @Override
    public boolean supportsMultipleTransactions() {
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() {
        return true;
    }

    @Override
    public boolean supportsCoreSQLGrammar() {
        return true;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() {
        return true;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() {
        return true;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() {
        return true;
    }

    @Override
    public boolean supportsANSI92FullSQL() {
        return true;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    @Override
    public boolean supportsOuterJoins() {
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() {
        return false;
    }

    @Override
    public boolean supportsLimitedOuterJoins() {
        return false;
    }

    @Override
    public String getSchemaTerm() {
        return "schema";
    }

    @Override
    public String getProcedureTerm() {
        return "procedure";
    }

    @Override
    public String getCatalogTerm() {
        return "catalog";
    }

    @Override
    public boolean isCatalogAtStart() {
        return true;
    }

    @Override
    public String getCatalogSeparator() {
        return ".";
    }

    @Override
    public boolean supportsSchemasInDataManipulation() {
        return true;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() {
        return true;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() {
        return true;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() {
        return true;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return true;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() {
        return true;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() {
        return true;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() {
        return true;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() {
        return true;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() {
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInExists() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInIns() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    @Override
    public boolean supportsUnion() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    @Override
    public int getMaxBinaryLiteralLength() {
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() {
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() {
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() {
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() {
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() {
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() {
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() {
        return 0;
    }

    @Override
    public int getMaxConnections() {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() {
        return 0;
    }

    @Override
    public int getMaxIndexLength() {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() {
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() {
        return 0;
    }

    @Override
    public int getMaxRowSize() {
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    @Override
    public int getMaxStatementLength() {
        return 0;
    }

    @Override
    public int getMaxStatements() {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() {
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() {
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public boolean supportsTransactions() {
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) {
        return level == Connection.TRANSACTION_NONE;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getTables(String catalog, String schema, String tableName, String[] types) {
        final String catalogPattern = catalog == null || catalog.isEmpty()
                ? "%" : catalog;
        final String schemaPattern = schema == null || schema.isEmpty()
                ? "%" : schema;
        final String tableNamePattern = tableName == null || tableName.isEmpty()
                ? "%" : tableName;

        final String sqlTemplate = "SELECT "
                + "table_catalog,"
                + "table_schema,"
                + "table_name,"
                + "table_type "
                + "FROM information_schema.tables "
                + "WHERE table_catalog LIKE '%s' AND table_schema LIKE '%s' AND table_name LIKE '%s' %s";
        final String typesFilter = types == null || types.length == 0
                ? ""
                : "AND table_type IN ("
                        + Arrays.stream(types)
                        .map(s -> String.format("'%s'", s))
                        .collect(Collectors.joining(","))
                        + ")";
        final String sql = String.format(sqlTemplate, catalogPattern, schemaPattern, tableNamePattern, typesFilter);
        final List<List<String>> tableData = new ArrayList<>();
        try (ResultSet rs = this.connection.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                tableData.add(asList(
                        rs.getString(1), // catalog
                        rs.getString(2), // schema
                        rs.getString(3), // name
                        rs.getString(4)) // type
                );
            }

        } catch (SQLException e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }

        final SqlRowMetadata metadata = new SqlRowMetadata(asList(
                new SqlColumnMetadata("TABLE_CAT", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TABLE_SCHEM", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TABLE_NAME", SqlColumnType.VARCHAR, false),
                new SqlColumnMetadata("TABLE_TYPE", SqlColumnType.VARCHAR, true),

                new SqlColumnMetadata("REMARKS", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TYPE_CAT", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TYPE_SCHEM", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TYPE_NAME", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("SELF_REFERENCING_COL_NAME", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("REF_GENERATION", SqlColumnType.VARCHAR, true)
        ));

        final List<SqlRow> rows = tableData.stream()
                .map(data -> makeSqlRow(new Object[]{
                        data.get(0),
                        data.get(1),
                        data.get(2),
                        data.get(3),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                }, metadata))
                .collect(Collectors.toList());
        try {
            // TODO: fixedRowsStatement?
            // Since this result has no resources attached to it and therefore can be just GC'd normally
            // it might make sense to introduce a JdbcStatement that does.
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    @Override
    public ResultSet getSchemas() {
        final SqlRowMetadata metadata = new SqlRowMetadata(asList(
                new SqlColumnMetadata("TABLE_CATALOG", SqlColumnType.VARCHAR, false),
                new SqlColumnMetadata("TABLE_SCHEM", SqlColumnType.VARCHAR, false)
        ));

        final List<SqlRow> rows = singletonList(makeSqlRow(new Object[]{"hazelcast", "public"}, metadata));

        try {
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    @Override
    public ResultSet getCatalogs() {
        final SqlRowMetadata metadata = new SqlRowMetadata(singletonList(new SqlColumnMetadata(
                "TABLE_CAT",
                SqlColumnType.VARCHAR,
                false
        )));

        final List<SqlRow> rows = singletonList(makeSqlRow(new Object[]{"hazelcast"}, metadata));

        try {
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    @Override
    public ResultSet getTableTypes() {
        final SqlRowMetadata metadata = new SqlRowMetadata(singletonList(new SqlColumnMetadata(
                "TABLE_TYPE",
                SqlColumnType.VARCHAR,
                false
        )));

        final List<SqlRow> rows = asList(
                makeSqlRow(new Object[]{"BASE TABLE"}, metadata),
                makeSqlRow(new Object[]{"VIEW"}, metadata)
        );

        try {
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    @Override
    public ResultSet getColumns(
            String catalog,
            String schema,
            String tableName,
            String columnName
    ) {
        final String catalogPattern = catalog == null || catalog.isEmpty()
                ? "%" : catalog;
        final String schemaPattern = schema == null || schema.isEmpty()
                ? "%" : schema;
        final String tableNamePattern = tableName == null || tableName.isEmpty()
                ? "%" : tableName;
        final String columnNamePattern = columnName == null || columnName.isEmpty()
                ? "%" : columnName;

        final String sqlTemplate = "SELECT "
                + "table_catalog,"
                + "table_schema,"
                + "table_name,"
                + "column_name, "
                + "data_type,"
                + "is_nullable,"
                + "ordinal_position "
                + "FROM information_schema.columns "
                + "WHERE table_catalog LIKE '%s' "
                + "AND table_schema LIKE '%s' "
                + "AND table_name LIKE '%s' "
                + "AND column_name LIKE '%s' ";

        final String sql = String.format(
                sqlTemplate,
                catalogPattern,
                schemaPattern,
                tableNamePattern,
                columnNamePattern
        );

        final List<List<Object>> tableData = new ArrayList<>();
        try (ResultSet rs = this.connection.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                tableData.add(asList(
                        rs.getString(1), // catalog
                        rs.getString(2), // schema
                        rs.getString(3), // table_name
                        rs.getString(4), // column_name
                        rs.getString(5), // column_type
                        rs.getBoolean(6), // is_nullable
                        rs.getInt(7) // column_index
                ));
            }

        } catch (SQLException e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }

        final SqlRowMetadata metadata = new SqlRowMetadata(asList(
                new SqlColumnMetadata("TABLE_CAT", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TABLE_SCHEM", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("TABLE_NAME", SqlColumnType.VARCHAR, false),
                new SqlColumnMetadata("COLUMN_NAME", SqlColumnType.VARCHAR, false),
                new SqlColumnMetadata("DATA_TYPE", SqlColumnType.SMALLINT, false),
                new SqlColumnMetadata("TYPE_NAME", SqlColumnType.VARCHAR, false),

                new SqlColumnMetadata("COLUMN_SIZE", SqlColumnType.INTEGER, false),
                new SqlColumnMetadata("BUFFER_LENGTH", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("DECIMAL_DIGITS", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("NUM_PREC_RADIX", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("NULLABLE", SqlColumnType.INTEGER, false),

                new SqlColumnMetadata("REMARKS", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("COLUMN_DEF", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("SQL_DATA_TYPE", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("SQL_DATETIME_SUB", SqlColumnType.INTEGER, true),

                new SqlColumnMetadata("CHAR_OCTET_LENGTH", SqlColumnType.INTEGER, false),
                new SqlColumnMetadata("ORDINAL_POSITION", SqlColumnType.INTEGER, false),
                new SqlColumnMetadata("IS_NULLABLE", SqlColumnType.VARCHAR, false),

                new SqlColumnMetadata("SCOPE_CATALOG", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("SCOPE_SCHEMA", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("SCOPE_TABLE", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("SOURCE_DATA_TYPE", SqlColumnType.SMALLINT, true),
                new SqlColumnMetadata("IS_AUTOINCREMENT", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("IS_GENERATEDCOLUMN", SqlColumnType.VARCHAR, true)
        ));

        final List<SqlRow> rows = tableData.stream()
                .map(data -> {
                    final SqlColumnType sqlColumnType = TypeUtil.getTypeByQDTName((String) data.get(4));
                    final boolean isNullable = (boolean) data.get(5);
                    final TypeUtil.SqlTypeInfo typeInfo = TypeUtil.getTypeInfo(sqlColumnType);


                    return makeSqlRow(new Object[]{
                            data.get(0), // CAT
                            data.get(1), // SCHEM
                            data.get(2), // TABLE_NAME
                            data.get(3), // COLUMN_NAME
                            TypeUtil.getJdbcType(sqlColumnType), // DATA_TYPE
                            // Source column is QueryDataTypeFamily.name()
                            ((String) data.get(4)).replaceAll("_", " "), // TYPE_NAME

                            typeInfo.getPrecision(), // COLUMN_SIZE
                            null, // BUFFER_LENGTH
                            typeInfo.getScale() == 0 ? null : typeInfo.getScale(), // DECIMAL_DIGITS
                            TypeUtil.isNumeric(sqlColumnType) ? 10 : null, // NUM_PREC_RADIX
                            isNullable ? DatabaseMetaData.columnNullable : DatabaseMetaData.columnNoNulls, // NULLABLE

                            null, // REMARKS
                            null, // COLUMN_DEF
                            null, // SQL_DATA_TYPE
                            null, // SQL_DATETIME_SUB

                            sqlColumnType.equals(SqlColumnType.VARCHAR) ? typeInfo.getPrecision() : null, // CHAR_OCTET_LENGTH
                            data.get(6), // ORDINAL_POSITION
                            isNullable ? "YES" : "NO", // IS_NULLABLE

                            null, // SCOPE_CATALOG
                            null, // SCOPE_SCHEMA
                            null, // SCOPE_TABLE
                            null, // SOURCE_DATA_TYPE
                            null, // IS_AUTOINCREMENT
                            null // IS_GENERATEDCOLUMN
                    }, metadata);
                })
                .collect(Collectors.toList());
        try {
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getCrossReference(
            String parentCatalog, String parentSchema, String parentTable,
            String foreignCatalog, String foreignSchema, String foreignTable) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getTypeInfo() {
        final SqlRowMetadata metadata = new SqlRowMetadata(asList(
                new SqlColumnMetadata("TYPE_NAME", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("DATA_TYPE", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("PRECISION", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("LITERAL_PREFIX", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("LITERAL_SUFFIX", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("CREATE_PARAMS", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("NULLABLE", SqlColumnType.SMALLINT, true),
                new SqlColumnMetadata("CASE_SENSITIVE", SqlColumnType.BOOLEAN, true),
                new SqlColumnMetadata("SEARCHABLE", SqlColumnType.SMALLINT, true),
                new SqlColumnMetadata("UNSIGNED_ATTRIBUTE", SqlColumnType.BOOLEAN, true),
                new SqlColumnMetadata("FIXED_PREC_SCALE", SqlColumnType.BOOLEAN, true),
                new SqlColumnMetadata("AUTO_INCREMENT", SqlColumnType.BOOLEAN, true),
                new SqlColumnMetadata("LOCAL_TYPE_NAME", SqlColumnType.VARCHAR, true),
                new SqlColumnMetadata("MINIMUM_SCALE", SqlColumnType.SMALLINT, true),
                new SqlColumnMetadata("MAXIMUM_SCALE", SqlColumnType.SMALLINT, true),
                new SqlColumnMetadata("SQL_DATA_TYPE", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("SQL_DATETIME_SUB", SqlColumnType.INTEGER, true),
                new SqlColumnMetadata("NUM_PREC_RADIX", SqlColumnType.INTEGER, true)
        ));

        final List<SqlRow> rows = asList(
                typeInfoRow(SqlColumnType.VARCHAR, metadata),
                typeInfoRow(SqlColumnType.BOOLEAN, metadata),
                typeInfoRow(SqlColumnType.BIGINT, metadata),
                typeInfoRow(SqlColumnType.TINYINT, metadata),
                typeInfoRow(SqlColumnType.SMALLINT, metadata),
                typeInfoRow(SqlColumnType.INTEGER, metadata),
                typeInfoRow(SqlColumnType.DECIMAL, metadata),
                typeInfoRow(SqlColumnType.REAL, metadata),
                typeInfoRow(SqlColumnType.DOUBLE, metadata),
                typeInfoRow(SqlColumnType.TIME, metadata),
                typeInfoRow(SqlColumnType.DATE, metadata),
                typeInfoRow(SqlColumnType.TIMESTAMP, metadata),
                typeInfoRow(SqlColumnType.TIMESTAMP_WITH_TIME_ZONE, metadata),
                typeInfoRow(SqlColumnType.OBJECT, metadata),
                typeInfoRow(SqlColumnType.JSON, metadata)
        );

        try {
            return new JdbcResultSet(new FixedRowsSqlResult(metadata, rows), new JdbcStatement(null, connection));
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            return JdbcResultSet.EMPTY;
        }
    }

    private SqlRow typeInfoRow(SqlColumnType columnType, SqlRowMetadata metadata) {
        final TypeUtil.SqlTypeInfo typeInfo = TypeUtil.getTypeInfo(columnType);
        final Object[] values = new Object[]{
                TypeUtil.getName(columnType),
                TypeUtil.getJdbcType(columnType),
                typeInfo.getPrecision(),
                null,
                null,
                null,
                DatabaseMetaData.typeNullable,
                true,
                DatabaseMetaData.typeSearchable,
                typeInfo.isSigned(),
                false,
                false,
                null,
                0,
                0,
                0,
                0,
                0
        };
        return makeSqlRow(values, metadata);
    }

    private SqlRow makeSqlRow(Object[] values, SqlRowMetadata sqlRowMetadata) {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        JetSqlRow jetSqlRow = new JetSqlRow(serializationService, values);
        return new SqlRowImpl(sqlRowMetadata, jetSqlRow);
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public boolean supportsResultSetType(int type) {
        return connection.supportsResultSetType(type);
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) {
        return connection.supportsResultSetConcurrency(concurrency);
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) {
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) {
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) {
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) {
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() {
        return false;
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean supportsSavepoints() {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() {
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getAttributes(
            String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) {
        return connection.supportsHoldability(holdability);
    }

    @Override
    public int getResultSetHoldability() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public int getDatabaseMajorVersion() {
        return this.getMasterVersion().getMajor();
    }

    @Override
    public int getDatabaseMinorVersion() {
        return this.getMasterVersion().getMinor();
    }

    @Override
    public int getJDBCMajorVersion() {
        return JDBC_VERSION_MAJOR;
    }

    @Override
    public int getJDBCMinorVersion() {
        return JDBC_VERSION_MINOR;
    }

    @Override
    public int getSQLStateType() {
        return DatabaseMetaData.sqlStateSQL;
    }

    @Override
    public boolean locatorsUpdateCopy() {
        return false;
    }

    @Override
    public boolean supportsStatementPooling() {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) {
        return getSchemas();
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() {
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() {
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getFunctions(
            String catalog, String schemaPattern, String functionNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        return JdbcResultSet.EMPTY;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return JdbcUtils.unwrap(this, iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return JdbcUtils.isWrapperFor(this, iface);
    }

    // See https://github.com/hazelcast/hazelcast/issues/21301
    private Version getMasterVersion() {
        // connection.getClientInstance().getCluster().getClusterVersion();
        MemberVersion memberVersion = connection.getClientInstance().getCluster()
                .getMembers().iterator().next().getVersion();
        return Version.of(memberVersion.getMajor(), memberVersion.getMinor());
    }
}
