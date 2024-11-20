package org.sounfury.jooq.page.utils;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.page.PageRepDto;

import java.util.List;
import java.util.Objects;

public class JooqPageHelper {

    // SelectConditionStep表示查询在添加了WHERE条件之后的阶段。这个接口允许你添加ORDER BY、LIMIT、OFFSET等子句。


    /**
     * 执行分页查询
     *
     * @param query 原始查询
     * @param pageRequest 分页请求参数
     * @param dsl DSLContext实例
     * @param <R> 结果记录类型
     * @return 分页响应
     */
    public static <R extends Record> PageRepDto<List<R>> getPage(
            SelectConditionStep<R> query,
            PageReqDto pageRequest,
            DSLContext dsl) {

        // 1. 将 `query` 转换为子查询
        TableLike<?> subQuery = query.asTable("subquery");

        // 2. 使用子查询生成计数SQL
        Long total = dsl.select(DSL.count())
                .from(subQuery)
                .fetchOne(0, Long.class);


        if (total == 0) {
            return PageRepDto.empty();
        }


        // 2. 添加排序和分页条件
        List<R> records = query
                .orderBy(pageRequest.getSortFields())
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .fetch();

        return new PageRepDto<>(total, records);
    }

    /**
     * 执行分页查询并自动转换为指定类型
     *
     * @param query 原始查询
     * @param pageRequest 分页请求参数
     * @param dsl DSLContext实例
     * @param targetClass 目标类型
     * @param <R> 结果记录类型
     * @param <T> 目标类型
     * @return 分页响应
     */
    public static <R extends Record, T> PageRepDto<List<T>> getPage(
            SelectHavingStep<R> query,
            PageReqDto pageRequest,
            DSLContext dsl,
            Class<T> targetClass) {
        Table<R> subquery = query.asTable("subquery");
        Long total = dsl.select(DSL.count())
                .from(subquery)
                .fetchOne(0, Long.class);
        if(Objects.isNull(total) || total == 0) {
            return PageRepDto.empty();
        }

        // 2. 添加排序和分页条件，并自动转换为目标类型
        List<T> records = query
                .orderBy(pageRequest.getSortFields())
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .fetch()
                .into(targetClass);

        return new PageRepDto<>(total, records);
    }



    /**
     * 执行分页查询（返回指定类型）
     *
     * @param query 原始查询
     * @param pageRequest 分页请求参数
     * @param dsl DSLContext实例
     * @param mapper 结果映射函数
     * @param <R> 结果记录类型
     * @param <T> 目标类型
     * @return 分页响应
     */
    public static <R extends Record, T> PageRepDto<List<T>> getPage(
            SelectHavingStep<R> query,
            PageReqDto pageRequest,
            DSLContext dsl,
            RecordMapper<R, T> mapper) {
        // 1. 将 `query` 转换为子查询
        TableLike<?> subQuery = query.asTable("subquery");

        // 2. 使用子查询生成计数SQL
        Long total = dsl.select(DSL.count())
                .from(subQuery)
                .fetchOne(0, Long.class);
        if(Objects.isNull(total) || total == 0) {
            return PageRepDto.empty();
        }

        // 2. 添加排序和分页条件
        List<T> records = query
                .orderBy(pageRequest.getSortFields())
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .fetch()
                .map(mapper);

        return new PageRepDto<>(total, records);
    }

    /**
     * 构建带计数的分页查询
     *
     * @param selectQuery 选择查询
     * @param countQuery 计数查询
     * @param pageRequest 分页请求参数
     * @param <R> 结果记录类型
     * @return 分页响应
     */
    public static <R extends Record> PageRepDto<List<R>> getPage(
            SelectConditionStep<R> selectQuery,
            SelectConditionStep<?> countQuery,
            PageReqDto pageRequest) {

        // 1. 获取总记录数
        long total = countQuery.fetchOne(0, Long.class);

        if (total == 0) {
            return PageRepDto.empty();
        }

        // 2. 添加排序和分页条件
        List<R> records = selectQuery
                .orderBy(pageRequest.getSortFields())
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .fetch();

        return new PageRepDto<>(total, records);
    }

    /**
     * 构建带计数的分页查询（返回指定类型）
     *
     * @param selectQuery 选择查询
     * @param countQuery 计数查询
     * @param pageRequest 分页请求参数
     * @param mapper 结果映射函数
     * @param <R> 结果记录类型
     * @param <T> 目标类型
     * @return 分页响应
     */
    public static <R extends Record, T> PageRepDto<List<T>> getPage(
            SelectConditionStep<R> selectQuery,
            SelectConditionStep<?> countQuery,
            PageReqDto pageRequest,
            RecordMapper<R, T> mapper) {

        // 1. 获取总记录数
        long total = countQuery.fetchOne(0, Long.class);

        if (total == 0) {
            return PageRepDto.empty();
        }

        // 2. 添加排序和分页条件
        List<T> records = selectQuery
                .orderBy(pageRequest.getSortFields())
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .fetch()
                .map(mapper);

        return new PageRepDto<>(total, records);
    }
}