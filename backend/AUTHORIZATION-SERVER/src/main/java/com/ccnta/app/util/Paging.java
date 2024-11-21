package com.ccnta.app.util;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Paging {

    private int page;
    private int size;
    private long totalElement;
    private long totalPage;
    private boolean first;
    private boolean last;

}