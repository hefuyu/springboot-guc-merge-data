package com.hefy.gucboot.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hefy.gucboot.entity.Stock;
import com.hefy.gucboot.entity.StockLog;
import com.hefy.gucboot.mapper.StockMapper;
import com.hefy.gucboot.service.IStockLogService;
import com.hefy.gucboot.service.IStockService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-05-17
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Resource
    JdbcTemplate template;
    @Resource
    IStockLogService stockLogService;

    // 500个线程 10次  QPS 135
    //
    @Override
    @Transactional
    public boolean delStock(String id, Integer qty) {
        boolean flag = template.update("update stock set qty = qty-? where id=? and qty-? >=0", qty, id, qty) == 1;
        if (flag) {
            StockLog stockLog = new StockLog();
            stockLog.setId(StrUtil.uuid());
            stockLog.setCreatetime(LocalDateTime.now());
            stockLog.setProduct(id);
            stockLog.setUserid(1);
            stockLog.setQty(qty);
            stockLogService.save(stockLog);
        }
        return flag;
    }

    @Override
    @Transactional
    public boolean delStockBatch(List<Stock> stocks){
        AtomicInteger atomicInteger = new AtomicInteger();
        List<Object[]> list = stocks.stream().map(item -> {
            Object[] objects = new Object[3];
            objects[0] = item.getQty();
            objects[1] = item.getId();
            objects[2] = item.getQty();
            atomicInteger.addAndGet(item.getQty());
            return objects;
        }).collect(Collectors.toList());

        List<StockLog> logs = stocks.stream().map(item -> {
            StockLog stockLog = new StockLog();
            stockLog.setId(StrUtil.uuid());
            stockLog.setCreatetime(LocalDateTime.now());
            stockLog.setProduct("1");
            stockLog.setUserid(1);
            stockLog.setQty(item.getQty());
            return stockLog;
        }).collect(Collectors.toList());


        template.batchUpdate("update stock set qty = qty-? where id=? and qty-? >=0",list);
        stockLogService.saveBatch(logs);
        return true;
    }
}
