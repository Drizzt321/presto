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
package com.facebook.presto.orc.metadata.statistics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class LongDecimalStatisticsBuilder
        implements StatisticsBuilder
{
    private long nonNullValueCount;
    private BigDecimal minimum;
    private BigDecimal maximum;

    public void addValue(BigDecimal value)
    {
        requireNonNull(value, "value is null");

        nonNullValueCount++;

        if (minimum == null) {
            minimum = value;
            maximum = value;
        }
        else {
            minimum = minimum.min(value);
            maximum = maximum.max(value);
        }
    }

    private void addDecimalStatistics(long valueCount, DecimalStatistics value)
    {
        requireNonNull(value, "value is null");
        requireNonNull(value.getMin(), "value.getMin() is null");
        requireNonNull(value.getMax(), "value.getMax() is null");

        nonNullValueCount += valueCount;
        if (minimum == null) {
            minimum = value.getMin();
            maximum = value.getMax();
        }
        else {
            minimum = minimum.min(value.getMin());
            maximum = maximum.max(value.getMax());
        }
    }

    private Optional<DecimalStatistics> buildDecimalStatistics()
    {
        if (nonNullValueCount == 0) {
            return Optional.empty();
        }
        return Optional.of(new DecimalStatistics(minimum, maximum));
    }

    @Override
    public ColumnStatistics buildColumnStatistics()
    {
        return new ColumnStatistics(
                nonNullValueCount,
                null,
                null,
                null,
                null,
                null,
                buildDecimalStatistics().orElse(null),
                null);
    }

    public static Optional<DecimalStatistics> mergeDecimalStatistics(List<ColumnStatistics> stats)
    {
        LongDecimalStatisticsBuilder decimalStatisticsBuilder = new LongDecimalStatisticsBuilder();
        for (ColumnStatistics columnStatistics : stats) {
            DecimalStatistics partialStatistics = columnStatistics.getDecimalStatistics();
            if (columnStatistics.getNumberOfValues() > 0) {
                if (partialStatistics == null) {
                    // there are non null values but no statistics, so we can not say anything about the data
                    return Optional.empty();
                }
                decimalStatisticsBuilder.addDecimalStatistics(columnStatistics.getNumberOfValues(), partialStatistics);
            }
        }
        return decimalStatisticsBuilder.buildDecimalStatistics();
    }
}
