package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;

/**
 * interface class for grr view data (include)
 *
 * @author Can Guan
 */
@FunctionalInterface
public interface GrrViewDataListener {
    /**
     * event change api for grr view data (include)
     *
     * @param grrViewDataDto grr view data dto
     */
    void selectChange(GrrViewDataDto grrViewDataDto);
}
