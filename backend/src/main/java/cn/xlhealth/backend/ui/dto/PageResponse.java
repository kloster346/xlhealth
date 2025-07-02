package cn.xlhealth.backend.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 分页响应DTO
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 计算总页数
     */
    public void calculatePages() {
        if (total != null && size != null && size > 0) {
            this.pages = (total + size - 1) / size;
        } else {
            this.pages = 0L;
        }
    }

    /**
     * 计算是否有下一页
     */
    public void calculateHasNext() {
        if (current != null && pages != null) {
            this.hasNext = current < pages;
        } else {
            this.hasNext = false;
        }
    }

    /**
     * 计算是否有上一页
     */
    public void calculateHasPrevious() {
        if (current != null) {
            this.hasPrevious = current > 1;
        } else {
            this.hasPrevious = false;
        }
    }

    /**
     * 计算所有分页信息
     */
    public void calculateAll() {
        calculatePages();
        calculateHasNext();
        calculateHasPrevious();
    }
}