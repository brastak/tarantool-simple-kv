package com.brastak.tarantool.simplekv;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
class Data {
    private String strValue;
    private int intValue;
    private Data embedValue;
}
