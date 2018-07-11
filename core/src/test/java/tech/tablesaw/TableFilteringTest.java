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

package tech.tablesaw;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.QueryHelper;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static tech.tablesaw.api.QueryHelper.*;

/**
 * Tests for filtering on the T class
 */
public class TableFilteringTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testFilter1() {
        Table result = table.where(numberColumn("approval").isLessThan(70));
        NumberColumn a = result.numberColumn("approval");
        for (double v : a) {
            assertTrue(v < 70);
        }
    }

    /**
     * Tests that you can reference a column in a table that is returned by query, without creating a variable for the
     * intermediate table
     */
    @Test
    public void testQueryChaining() {
        Table structureWithoutDates =
                table.structure()
                    .dropWhere(stringColumn("column type").equalsIgnoreCase("Local_Date"));
        assertEquals(2, structureWithoutDates.rowCount());
        assertFalse(structureWithoutDates.stringColumn("Column Name").contains("Date"));
    }

    @Test
    public void testReject() {
        Table result = table.dropWhere(numberColumn("approval").isLessThan(70));
        NumberColumn a = result.numberColumn("approval");
        for (double v : a) {
            assertFalse(v < 70);
        }
    }

    @Test
    public void testRejectWithMissingValues() {

        String[] values = {"a", "b", "", "d"};
        double[] values2 = {1, Double.NaN, 3, 4};
        StringColumn sc = StringColumn.create("s", values);
        NumberColumn nc = DoubleColumn.create("n", values2);
        Table test = Table.create("test", sc, nc);
        Table result = test.dropRowsWithMissingValues();
        assertEquals(2, result.rowCount());
        assertEquals("a", result.stringColumn("s").get(0));
        assertEquals("d", result.stringColumn("s").get(1));
    }

    @Test
    public void testSelectRange() {
        Table result = table.inRange(20, 30);
        assertEquals(10, result.rowCount());
        for (Column c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(table.get(r+20, c.name()), result.get(r, c.name()));
            }
        }
    }

    @Test
    public void testSelectRows() {
        Table result = table.rows(20, 30);
        assertEquals(2, result.rowCount());
        for (Column c: result.columns()) {
            assertEquals(table.get(20, c.name()), result.get(0, c.name()));
            assertEquals(table.get(30, c.name()), result.get(1, c.name()));
        }
    }

    @Test
    public void testSampleRows() {
        Table result = table.sampleN(20);
        assertEquals(20, result.rowCount());
    }

    @Test
    public void testSampleProportion() {
        Table result = table.sampleX(.1);
        assertEquals(32, result.rowCount());
    }

    @Test
    public void testRejectRows() {
        Table result = table.dropRows(20, 30);
        assertEquals(table.rowCount() - 2, result.rowCount());
        for (Column c: result.columns()) {
            assertEquals(table.get(21, c.name()), result.get(20, c.name()));
            assertEquals(table.get(32, c.name()), result.get(30, c.name()));
        }
    }

    @Test
    public void testRejectRange() {
        Table result = table.dropRange(20, 30);
        assertEquals(table.rowCount() - 10, result.rowCount());
        for (Column c: result.columns()) {
            for (int r = 30; r < result.rowCount(); r++) {
                assertEquals(result.get(r, c.name()), table.get(r + 10, c.name()));
            }
        }
    }

    @Test
    public void testFilter2() {
        Table result = table.where(dateColumn("date").isInApril());
        DateColumn d = result.dateColumn("date");
        for (LocalDate v : d) {
            assertTrue(PackedLocalDate.isInApril(PackedLocalDate.pack(v)));
        }
    }

    @Test
    public void testFilter3() {
        Table result = table.where(
                QueryHelper.both(
                        table.dateColumn("date").isInApril(),
                        table.numberColumn("approval").isGreaterThan(70)));

        DateColumn dates = result.dateColumn("date");
        NumberColumn approval = result.numberColumn("approval");
        for (int row = 0; row < result.rowCount(); row++) {
            assertTrue(PackedLocalDate.isInApril(dates.getIntInternal(row)));
            assertTrue(approval.get(row) > 70);
        }
    }

    @Test
    public void testFilter4() {
        Table result =
                table.retainColumns("who", "approval")
                        .where(
                                QueryHelper.both(
                                        table.dateColumn("date").isInApril(),
                                        table.numberColumn("approval").isGreaterThan(70)));
        assertEquals(2, result.columnCount());
        assertTrue(result.columnNames().contains("who"));
        assertTrue(result.columnNames().contains("approval"));
    }
}
