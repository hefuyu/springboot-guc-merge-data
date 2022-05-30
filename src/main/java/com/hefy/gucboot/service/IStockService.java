package com.hefy.gucboot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hefy.gucboot.entity.Stock;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-05-17
 */
public interface IStockService extends IService<Stock> {

    boolean delStock(String id,Integer qty);

    boolean delStockBatch(List<Stock> stocks);
}
