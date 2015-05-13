package com.zjk.wifiproject.entity;

import java.util.Comparator;

/**
 * @author george
 *         该类用来保存文件路径和文件名，同时实现Comparable接口，根据type的值来进行排序，(调用方法compareto、Collections
 *         .sort) type=1代表当前存的是目录信息 type=2代表当前存的是文件信息
 *         根据type的值从小到大排例，这样文件夹均被排在前面，文件排在后面
 */
public class FileStyle implements Comparator<WFile> {


    @Override
    public int compare(WFile lhs, WFile rhs) {
        int type1 = lhs.isDirectory() ? 1 : 2;
        int type2 = rhs.isDirectory() ? 1 : 2;
        return type1 == type2 ? lhs.getName().compareTo(rhs.getName ()):type1 - type2;
    }


}
