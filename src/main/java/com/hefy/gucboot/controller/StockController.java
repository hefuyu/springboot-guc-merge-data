package com.hefy.gucboot.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.hefy.gucboot.entity.Stock;
import com.hefy.gucboot.service.IStockService;
import com.hefy.gucboot.util.DataMerge;
import com.hefy.gucboot.util.TimeUtil;
import com.hefy.gucboot.util.window.SphU;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-05-17
 */
@Controller
@RequestMapping("/system/stock")
@RestController
public class StockController {

    Log log = LogFactory.get();

    @Resource
    IStockService iStockService;

    SphU sphU = new SphU(5, 1000, 10000);

    // tps 不超过100  样本：100/100
    // tps 不超过70  样本：1000/10
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public boolean delStock(@RequestParam Integer id, @RequestParam Integer qty) throws Exception {
        return iStockService.delStock(id.toString(), qty);
        //return iStockService.delStock(id,qty);
    }


    public Consumer<List<DataMerge.MergeData<Stock, Boolean>>> consumer = list -> {
        //long l = TimeUtil.currentTimeMillis();
        List<Stock> stocks = list.stream().map(DataMerge.MergeData::getData).collect(Collectors.toList());
        iStockService.delStockBatch(stocks);
        //log.info("本次合并" + stocks.size() + "条,耗时" + (TimeUtil.currentTimeMillis() - l) + "msg");
        list.forEach(item -> item.complete(DataMerge.ResponseData.ok(true, "成功扣减库存")));
    };

    public DataMerge<Stock, Boolean> mergeMap = new DataMerge<>(10,3,5000,500, consumer);

    // tps 1365 样本：100/100
    // tps 3864 样本：1000/10
    // tps 3346 样本：1000/20 (tps 7000 样本：1000/20 20)
    @RequestMapping(value = "/del2", method = RequestMethod.GET)
    public boolean delStock2(@RequestParam Integer id, @RequestParam Integer qty) throws Exception {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setQty(qty);
        DataMerge.ResponseData<Boolean> responseData = mergeMap.putAndGet(stock);
        //log.info(responseData.getMsg());
        if (responseData.isSuccess()) {
            return responseData.getRes();
        }
        return false;
        //return iStockService.delStock(id,qty);
    }

    @RequestMapping("/query")
    public List<Stock> queryStock() {
        return iStockService.list();

    }

    @RequestMapping("/base")
    public boolean base() {
        return true;

    }

}
