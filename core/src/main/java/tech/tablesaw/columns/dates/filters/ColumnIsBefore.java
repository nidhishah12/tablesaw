/*
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

package tech.tablesaw.columns.dates.filters;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.TwoColumnFilter;
import tech.tablesaw.selection.Selection;

import static tech.tablesaw.columns.DateAndTimePredicates.*;

/**
 */
public class ColumnIsBefore extends TwoColumnFilter {

    public ColumnIsBefore(ColumnReference reference, Column otherColumn) {
        super(reference, otherColumn);
    }

    public ColumnIsBefore(DateColumn columnToCompareAgainst) {
        super(columnToCompareAgainst);
    }

    public ColumnIsBefore(ColumnReference reference, ColumnReference columnToCompareAgainst) {
        super(reference, columnToCompareAgainst);
    }

    /**
     * Returns a selection formed by applying a predicate for case-neutral equality between
     * each value in the given column and the corresponding value of the column passed into the constructor.
     *
     * @param columnToFilter The column whose results will be filtered by the operation
     */
    @Override
    public Selection apply(Column columnToFilter) {
        DateColumn dateColumn = (DateColumn) columnToFilter;
        return dateColumn.eval(isLessThan, (DateColumn) otherColumn());
    }
}