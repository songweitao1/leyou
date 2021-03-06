package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.emuns.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.untils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.Repository.GoodsRepository;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pjo.Goods;
import com.leyou.search.pjo.SearchRequest;
import com.leyou.search.pjo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    BrandClient brandClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SpecificationClient specificationClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    GoodsRepository goodsRepository;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private Spu spu;

    public SearchResult search(SearchRequest request) {

        // ??????????????????
        if (StringUtils.isBlank(request.getKey())) {
            // ?????????????????????
            return null;
        }

        // ?????????????????????????????????
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ??????????????????
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // ????????????????????????????????????id,subTitle, skus
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        // ??????????????????
        Integer page = request.getPage();
        Integer size = request.getSize();
        // ????????????
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // ???????????????????????????????????????
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        // ?????????????????????
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        // ?????????????????????????????????
        return new SearchResult(goodsPage.getContent(), goodsPage.getTotalElements(), goodsPage.getTotalPages(), categories, brands);
    }
    /**
     * ???????????????????????????
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        // ?????????????????????
        LongTerms terms = (LongTerms)aggregation;
        // ?????????????????????id???
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // ??????????????????????????????????????????????????????
        List<Brand> brands = new ArrayList<>();
        // ???????????????id??????????????????
        buckets.forEach(bucket -> {
            Brand brand = this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;
        // ?????????????????????????????????????????????????????????id?????????
        // List<Long> brandIds = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        // ??????ids????????????
        //return brandIds.stream().map(id -> this.brandClient.queryBrandById(id)).collect(Collectors.toList());
        // return terms.getBuckets().stream().map(bucket -> this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue())).collect(Collectors.toList());
    }

    /**
     * ????????????
     * @param aggregation
     * @return
     */
    private List<Map<String,Object>> getCategoryAggResult(Aggregation aggregation) {
        // ?????????????????????
        LongTerms terms = (LongTerms)aggregation;
        // ?????????????????????id???
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // ??????????????????????????????????????????????????????
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        // ???????????????id??????????????????
        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            System.out.println(names.get(i));
            categories.add(map);
        }
        return categories;
//        return terms.getBuckets().stream().map(bucket -> {
//            Map<String,Object> map = new HashMap<>();
//            Long id = bucket.getKeyAsNumber().longValue();
//            List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(id));
//            map.put("id",id);
//            map.put("name",names.get(0));
//            return map;
//        }).collect(Collectors.toList());
    }


    public Goods bulidGdoods(Spu spu) throws JsonProcessingException {
        this.spu = spu;
        Goods goods = new Goods();
//        ????????????
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
//        ??????????????????
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(names)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
/// ??????spu????????????sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException((ExceptionEnum.GOODS_SKU_NOT_FOUND));
        }
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // ??????skus?????????????????????
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });
//        ??????????????????
//        ??????????????????????????????
        List<SpecParam> Params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPEC_DETAIL_NOT_FOUND);
        }
        //??????????????????

        Map<Long, Object> genericSpecMap = null;
        try {
            genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
//??????????????????
        Map<String, List<Object>> specionSpecMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });
        Map specs = new HashMap();
        for (SpecParam param : Params) {

            String key = param.getName();
            Object value = "";
            if (param.getGeneric()) {

                value = genericSpecMap.get(param.getId());
                if (param.getNumeric()) {
                    String vlaue = chooseSegment(value.toString(), param);
                    System.out.println(vlaue.indexOf(2));
                }
            } else {
                value = specionSpecMap.get(param.getId());
            }
            specs.put(key, value);
        }
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setPrice(prices);
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setSpecs(specs);
        return goods;
    }

    ;

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "??????";
        // ???????????????
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // ??????????????????
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // ????????????????????????
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "??????";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "??????";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


}
