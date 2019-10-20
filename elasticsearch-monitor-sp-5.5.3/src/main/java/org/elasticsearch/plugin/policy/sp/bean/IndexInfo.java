package org.elasticsearch.plugin.policy.sp.bean;

import org.elasticsearch.common.unit.ByteSizeValue;

public class IndexInfo {
    String indexName;
    long createTime;
    long docNumber;
    ByteSizeValue primaryByteSize;

    public IndexInfo(String indexName, long createTime, long docNumber, ByteSizeValue ByteSizeValue) {
        this.indexName = indexName;
        this.createTime = createTime;
        this.docNumber = docNumber;
        this.primaryByteSize = primaryByteSize;
    }

    public String getIndexName() {
        return indexName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getDocNumber() {
        return docNumber;
    }

    public ByteSizeValue getPrimaryByteSize() {
        return primaryByteSize;
    }

    @Override
    public String toString() {
        return "IndexInfo{" +
                "indexName='" + indexName + '\'' +
                ", createTime=" + createTime +
                ", docNumber=" + docNumber +
                ", primaryByteSize=" + primaryByteSize +
                '}';
    }
}
