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

package com.facebook.presto.spi.statistics;

import static com.facebook.presto.spi.statistics.Estimate.unknownValue;
import static java.util.Objects.requireNonNull;

public final class ColumnStatistics
{
    private final Estimate dataSize;
    private final Estimate nullsCount;
    private final Estimate distinctValuesCount;

    private ColumnStatistics(Estimate dataSize, Estimate nullsCount, Estimate distinctValuesCount)
    {
        this.dataSize = requireNonNull(dataSize, "dataSize can not be null");
        this.nullsCount = requireNonNull(nullsCount, "nullsCount can not be null");
        this.distinctValuesCount = requireNonNull(distinctValuesCount, "distinctValuesCount can not be null");
    }

    public Estimate getDataSize()
    {
        return dataSize;
    }

    public Estimate getNullsCount()
    {
        return nullsCount;
    }

    public Estimate getDistinctValuesCount()
    {
        return distinctValuesCount;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Estimate dataSize = unknownValue();
        private Estimate nullsCount = unknownValue();
        private Estimate distinctValuesCount = unknownValue();

        public Builder setDataSize(Estimate dataSize)
        {
            this.dataSize = dataSize;
            return this;
        }

        public Builder setNullsCount(Estimate nullsCount)
        {
            this.nullsCount = nullsCount;
            return this;
        }

        public Builder setDistinctValuesCount(Estimate distinctValuesCount)
        {
            this.distinctValuesCount = distinctValuesCount;
            return this;
        }

        public ColumnStatistics build()
        {
            return new ColumnStatistics(dataSize, nullsCount, distinctValuesCount);
        }
    }
}
