package com.hefy.gucboot.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.hefy.gucboot.entity.Stock;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Create by hefy
 * @Date 2022/5/18 19:21
 */
public class Test {

    static Log log = LogFactory.get();

    public static Consumer<List<DataMerge.MergeData<Stock,Boolean>>> consumer = list ->{

        List<Stock> stocks = list.stream().map(DataMerge.MergeData::getData).collect(Collectors.toList());
        System.out.println("本次合并处理了"+stocks.size()+"条数据");
        list.stream().forEach(item->item.complete(DataMerge.ResponseData.ok(true,"扣减库存成功")));

    };

    public static DataMerge<Stock,Boolean> mergeMap = new DataMerge<>(10,consumer);

    public static void main(String[] args)throws Exception {
        //SphU sphU = new SphU(2,1000,10,null);

        IntStream.rangeClosed(1,1000).forEach(i->{
            AsyncUtil.submit(()->{
                Stock stock = new Stock();
                stock.setId(1);
                stock.setQty(1);
                try {
                    DataMerge.ResponseData<Boolean> booleanResponseData = mergeMap.putAndGet(stock);
                    log.info(booleanResponseData.getMsg());
                    if(booleanResponseData.isSuccess()){
                        return booleanResponseData.getRes();
                    }
                    return false;
                }catch (Exception e){
                    return false;
                }
            });
        });




    }

}
