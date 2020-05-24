package com.leyou.search.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecClient specClient;

    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //自定义查询
        //构建搜索条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //调用方法，返回一个查询条件对象
        QueryBuilder query =  buildBasicQueryWithFilter(searchRequest);

        //添加查询条件
        queryBuilder.withQuery(query);
        //分页,从searchRequest.getPage()-1页开始，每页searchRequest.getSize()条数据
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));

        //添加聚合
        String categoryAggName = "category";//商品分类聚合名称
        String brandAggName = "brand";//商品品牌聚合名称
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));//分类聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));//品牌聚合

        List<Category> categories = new ArrayList<>();
        List<Brand> brands = new ArrayList<>();
        //对商品的分类和品牌进行聚合
        //执行查询
        Page<Goods> search = goodsRepository.search(queryBuilder.build());
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)search;

        //获取分类聚合结果
        Aggregation aggregation = goodsPage.getAggregation(categoryAggName);
        LongTerms categoryTerms = (LongTerms)aggregation;
        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();
        List<Long> ids = new ArrayList<>();
        //从分类的聚合分桶中获取所有分类id
        for (LongTerms.Bucket bucket : buckets) {
            ids.add(bucket.getKeyAsNumber().longValue());//聚合后分类id集合
        }
        //根据id查询分类名称
        List<String> names = categoryClient.queryNamesByIds(ids);
        for (int i = 0;i <  ids.size();i++) {
            Category category = new Category();
            category.setId(ids.get(i));
            category.setName(names.get(i));
            categories.add(category);
        }

        //获取品牌聚合结果
        LongTerms brandTerms = (LongTerms)goodsPage.getAggregation(brandAggName);
        List<LongTerms.Bucket> buckets1 = brandTerms.getBuckets();
        List<Long> brandIds = new ArrayList<>();
        //从品牌的聚合分桶中获取所有品牌id
        for (LongTerms.Bucket bucket : buckets1) {
            brandIds.add(bucket.getKeyAsNumber().longValue());//聚合后品牌id集合
        }
        //根据id查询品牌
        for (Long brandId : brandIds) {
            System.out.println("大*****大" + brandId);
            Brand brand = brandClient.queryBrandById(brandId);
            brands.add(brand);
        }

        //只有分类唯一才展示规格参数
        List<Map<String,Object>> specs = null;
        if (categories.size() == 1) {
            specs = getSpecs(categories.get(0).getId(),query);
        }
        System.out.println("嘀嘀嘀第"+brands);
        return new SearchResult(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent(),categories,brands,specs);
    }

    //构建一个基本查询条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        //创建基本的bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));

        //过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //商品的分类和品牌可以直接查询，不需要拼接
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            //字符串类型进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key,value));
        }
        //添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

    /**
     * 对查询到数据进行可搜索规格参数的聚合操作，聚合时要和，搜索条件相关，搜索到什么内容，聚合什么，不要全部聚合
     * @param id
     * @return
     */
    private List<Map<String, Object>> getSpecs(Long id, QueryBuilder query) {

        List<Map<String,Object>> specList = new ArrayList<>();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //先根据查询条件，执行查询
        queryBuilder.withQuery(query);

        //对规格参数进行聚合，聚合要拿到所有的可搜索的规格参数

        List<SpecParam> searchingSpecParams = this.specClient.querySpecParam(null, id, true,null);

        //添加聚合条件,聚合的名称就是可搜索规格参数的名称,聚合的字段就是合成字段
        searchingSpecParams.forEach(specParam -> queryBuilder.addAggregation(
                AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword")));

        AggregatedPage<Goods> page = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        searchingSpecParams.forEach(specParam -> {
            //可搜索规格参数的名称
            String name = specParam.getName();

            //根据聚合名称获取聚合的结果
            StringTerms stringTerms = (StringTerms) page.getAggregation(name);

            List<String> values = new ArrayList<>();
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

            //把聚合分桶中每个值存入values集合中
            buckets.forEach(bucket -> values.add(bucket.getKeyAsString()));

            Map<String,Object> specMap = new HashMap<>();
            specMap.put("k",name);//k===>CPU品牌

            specMap.put("options",values);//options===》["骁龙","联发科","展讯"]
            specList.add(specMap);
        });
        return specList;
    }
//    public PageResult<Goods> search(SearchRequest searchRequest) {
//        String key = searchRequest.getKey();
//        if (null == key) {
//            return null;
//        }
//        //构建搜索条件
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        //all里搜key，and条件
//        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
//        //查询过滤，只要"id","skus","subTitle"三个字段
//        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
//        //分页,从searchRequest.getPage()-1页开始，每页searchRequest.getSize()条数据
//        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));
//        //开始搜索，获取结果
//        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());
//        //返回结果,goodsPage.getTotalElements()总条数，new Long(goodsPage.getTotalPages())总页数，goodsPage.getContent()数据
//        return new PageResult<>(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent());
//    }
}
