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

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBPDataKind;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.data.DBDFormatSettings;
import org.jkiss.dbeaver.model.data.DBDValueHandler;
import org.jkiss.dbeaver.model.data.DBDValueHandlerProvider;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;

/**
 * ScalarDB value handler provider.
 *
 * Provides custom value handlers for ScalarDB-specific data type handling.
 * Currently handles DATETIME types to work around ScalarDB JDBC driver limitations.
 */
public class ScalarDBValueHandlerProvider implements DBDValueHandlerProvider {

    @Nullable
    @Override
    public DBDValueHandler getValueHandler(DBPDataSource dataSource, DBDFormatSettings preferences, DBSTypedObject typedObject) {
        if (typedObject.getDataKind() == DBPDataKind.DATETIME) {
            return new ScalarDBDateTimeValueHandler(preferences);
        }
        return null;
    }

}
