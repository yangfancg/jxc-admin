package cn.toesbieya.jxc.model.vo.result;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total;
    private List<T> list;
    private Object data;

    public PageResult(List<T> list) {
        this.list = list;
        if (list instanceof Page) {
            this.total = ((Page<T>) list).getTotal();
        }
        else {
            this.total = list.size();
        }
    }

    public PageResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
