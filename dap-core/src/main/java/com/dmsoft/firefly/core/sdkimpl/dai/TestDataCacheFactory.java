package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.CoreExceptionCode;
import com.dmsoft.firefly.core.utils.CoreExceptionParser;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * cache data for source data
 *
 * @author Can Guan
 */
@Service
public class TestDataCacheFactory {
    private LoadingCache<String, Set<String>> testDataCache;
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private String separator = "!@#";

    /**
     * constructor
     */
    public TestDataCacheFactory() {
        testDataCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterAccess(2000L, TimeUnit.SECONDS).build(new CacheLoader<String, Set<String>>() {
            @Override
            public Set<String> load(String s) {
                String[] keys = s.split(separator);
                if (keys.length != 2) {
                    return null;
                } else {
                    List<String> projectNameList = mapper.fromJson(keys[0], mapper.buildCollectionType(List.class, String.class));
                    String testItemName = keys[1];
                    Set<String> result = Sets.newLinkedHashSet();
                    List<RowDataDto> rowDataDtos = RuntimeContext.getBean(SourceDataService.class).findTestData(projectNameList, Lists.newArrayList(testItemName));
                    for (RowDataDto rowDataDto : rowDataDtos) {
                        result.add(rowDataDto.getData().get(testItemName));
                    }
                    return result;
                }
            }
        });
    }

    /**
     * method to find test data
     *
     * @param projectNameList list of project name
     * @param testItemName    test item name
     * @return set of test data
     */
    public Set<String> findTestData(List<String> projectNameList, String testItemName) {
        if (projectNameList != null && testItemName != null) {
            String names = mapper.toJson(projectNameList);
            String key = names + separator + testItemName;
            try {
                return testDataCache.get(key);
            } catch (Exception e) {
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001));
            }
        }
        return null;
    }

    public void clean(){
        testDataCache.invalidateAll();
    }
}
