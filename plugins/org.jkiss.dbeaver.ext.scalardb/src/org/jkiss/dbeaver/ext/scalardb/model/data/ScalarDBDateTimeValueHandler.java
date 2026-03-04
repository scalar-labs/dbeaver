/*
 * Copyright (C) 2026 Scalar, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.scalardb.model.data;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.data.DBDFormatSettings;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCResultSet;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.impl.jdbc.data.handlers.JDBCDateTimeValueHandler;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ScalarDB datetime value handler.
 *
 * <p>This handler addresses two issues with the ScalarDB JDBC driver:
 * <ol>
 *   <li>ScalarDB JDBC driver does not support getDate()/getTime() methods
 *       (throws SQLFeatureNotSupportedException), so we use getObject() instead.</li>
 *   <li>ScalarDB returns LocalTime with microsecond precision. Converting to
 *       java.sql.Time would lose precision (milliseconds only), so we preserve
 *       LocalTime/LocalDate directly.</li>
 * </ol>
 */
public class ScalarDBDateTimeValueHandler extends JDBCDateTimeValueHandler {

    public ScalarDBDateTimeValueHandler(DBDFormatSettings formatSettings) {
        super(formatSettings);
    }

    @Override
    public Object fetchValueObject(
        @NotNull DBCSession session,
        @NotNull DBCResultSet resultSet,
        @NotNull DBSTypedObject type,
        int index
    ) throws DBCException {
        if (resultSet instanceof JDBCResultSet dbResults) {
            try {
                // ScalarDB JDBC driver does not support getDate()/getTime() methods.
                // Use getObject() for all date/time types.
                Object value = dbResults.getObject(index + 1);
                return getValueFromObject(session, type, value, false, false);
            } catch (SQLException e) {
                throw new DBCException(e, session.getExecutionContext());
            }
        }
        return super.fetchValueObject(session, resultSet, type, index);
    }

    @Override
    public Object getValueFromObject(
        @NotNull DBCSession session,
        @NotNull DBSTypedObject type,
        Object value,
        boolean copy,
        boolean validateValue
    ) throws DBCException {
        // Preserve LocalTime/LocalDate precision from ScalarDB JDBC driver.
        // ScalarDB returns LocalTime with microsecond precision, and converting
        // to java.sql.Time would lose precision (milliseconds only).
        // DBeaver's DateTimeDataFormatter can format TemporalAccessor objects
        // (including LocalTime/LocalDate) correctly with full precision.
        if (value instanceof LocalTime || value instanceof LocalDate) {
            return value;
        }
        return super.getValueFromObject(session, type, value, copy, validateValue);
    }

}
